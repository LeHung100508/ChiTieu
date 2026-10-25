package hung.lct.chitieu.core.parser

import java.math.BigDecimal

private const val ONE_THOUSAND = 1_000L
private const val FIVE_HUNDRED = 500L
private const val ONE_HUNDRED = 100L
private const val FIVE = 5L
private const val TEN = 10L
private const val TEN_THOUSAND = 10_000L
private const val FIFTY_THOUSAND = 50_000L
private const val ONE_HUNDRED_THOUSAND = 100_000L
private const val FIVE_HUNDRED_THOUSAND = 500_000L
private const val ONE_MILLION = 1_000_000L
private const val MAX_SINGLE_DIGIT = 9L
private const val MAX_THREE_DIGIT = 999L
private const val THREE = 3

private val thousandUnits = setOf("k", "nghin", "ngan")
private val millionUnits = setOf("tr", "trieu", "cu")
private val hundredThousandUnits = setOf("lit", "loet")
private val currencyUnits = setOf("d", "vnd", "dong", "đồng", "đ")
private val contextNoiseWords = setOf("nam", "thang", "ngay", "so", "tang", "ban", "phong", "gio", "phut")
private val thousandsSepRegex = Regex("""^\d{1,3}(?:[.,]\d{3})+$""")

private val wordDigits = mapOf(
    "mot" to 1L,
    "hai" to 2L,
    "ba" to 3L,
    "bon" to 4L,
    "tu" to 4L,
    "nam" to 5L,
    "lam" to 5L,
    "sau" to 6L,
    "bay" to 7L,
    "tam" to 8L,
    "chin" to 9L,
    "muoi" to 10L,
)

private data class AmountMatch(
    val value: Long,
    val consumedCount: Int,
)

object AmountParser {

    fun parseAll(sentence: NormalizedSentence): List<ParsedAmount> {
        val tokens = sentence.tokens
        val result = mutableListOf<ParsedAmount>()
        var i = 0

        while (i < tokens.size) {
            val match = tryMatchNumberWithUnit(tokens, i)
                ?: tryMatchWordBasedNumber(tokens, i)
                ?: tryMatchStandaloneNumber(tokens, i)

            if (match != null) {
                val tokenStart = i
                val tokenEnd = i + match.consumedCount - 1
                result.add(
                    ParsedAmount(
                        value = match.value,
                        startIndex = tokens[tokenStart].startIndex,
                        endIndex = tokens[tokenEnd].endIndex,
                        tokenRange = tokenStart..tokenEnd,
                    ),
                )
                i += match.consumedCount
            } else {
                i++
            }
        }
        return result
    }

    private fun tryMatchNumberWithUnit(tokens: List<Token>, index: Int): AmountMatch? {
        val numToken = tokens.getOrNull(index)
        val unitToken = tokens.getOrNull(index + 1)
        if (numToken?.type != TokenType.NUMBER || unitToken?.type != TokenType.WORD) {
            return null
        }

        val nextToken = tokens.getOrNull(index + 2)
        val rawMatch = AmountMatcherHelper.matchUnit(numToken, unitToken, nextToken)
        return rawMatch?.let { AmountMatcherHelper.appendCurrencySuffix(tokens, index, it) }
    }

    private fun tryMatchWordBasedNumber(tokens: List<Token>, index: Int): AmountMatch? {
        val firstToken = tokens.getOrNull(index)
        val prevToken = tokens.getOrNull(index - 1)
        val isMiddle = index > 0 && prevToken?.type == TokenType.WORD &&
            (prevToken.unaccented in wordDigits || prevToken.unaccented in setOf("muoi", "chuc"))

        if (firstToken?.type != TokenType.WORD || isMiddle) {
            return null
        }

        val rawMatch = AmountMatcherHelper.matchWordTokens(tokens, index)
        return rawMatch?.let { AmountMatcherHelper.appendCurrencySuffix(tokens, index, it) }
    }

    private fun tryMatchStandaloneNumber(tokens: List<Token>, index: Int): AmountMatch? {
        val numToken = tokens.getOrNull(index)
        if (numToken?.type != TokenType.NUMBER || AmountMatcherHelper.isNoise(tokens, index, numToken.text)) {
            return null
        }

        val hasCurrency = tokens.getOrNull(index + 1)?.let {
            it.type == TokenType.WORD && it.text in currencyUnits
        } == true
        val value = AmountMatcherHelper.evaluateStandalone(numToken, hasCurrency)
        return value?.let { AmountMatch(it, if (hasCurrency) 2 else 1) }
    }
}

private object AmountMatcherHelper {

    fun isNoise(tokens: List<Token>, index: Int, text: String): Boolean {
        val isPhone = text.startsWith("0") && text.length > 1
        val isContext = index > 0 && tokens[index - 1].unaccented in contextNoiseWords
        return isPhone || isContext
    }

    fun evaluateStandalone(numToken: Token, hasCurrency: Boolean): Long? {
        val text = numToken.text
        val value = text.replace(".", "").replace(",", "").toLongOrNull() ?: 0L
        val hasSep = text.contains('.') || text.contains(',')
        val isValid = value > 0L && when {
            hasCurrency -> true
            hasSep -> thousandsSepRegex.matches(text)
            else -> value >= TEN_THOUSAND
        }
        return if (isValid) value else null
    }

    fun parseDecimalAmount(text: String, multiplier: Long): Long? {
        if (thousandsSepRegex.matches(text)) {
            val clean = text.replace(".", "").replace(",", "")
            return clean.toLongOrNull()?.times(multiplier)
        }
        val numStr = text.replace(',', '.')
        return try {
            val bd = BigDecimal(numStr)
            val result = bd.multiply(BigDecimal(multiplier))
            val valLong = result.toLong()
            if (valLong > 0) valLong else null
        } catch (_: NumberFormatException) {
            null
        }
    }

    fun appendCurrencySuffix(tokens: List<Token>, index: Int, rawMatch: AmountMatch): AmountMatch {
        val nextAfter = tokens.getOrNull(index + rawMatch.consumedCount)
        val finalConsumed = if (nextAfter?.text in currencyUnits) {
            rawMatch.consumedCount + 1
        } else {
            rawMatch.consumedCount
        }
        return AmountMatch(rawMatch.value, finalConsumed)
    }

    private fun calculateMillionBonus(
        unit: String,
        isAttached: Boolean,
        text: String,
        subVal: Long,
    ): Long {
        val canCombine = isAttached || unit in setOf("cu", "trieu")
        val isValid = canCombine && subVal in 1L..MAX_THREE_DIGIT && text.length <= THREE
        val mult = when (text.length) {
            1 -> ONE_HUNDRED_THOUSAND
            2 -> TEN_THOUSAND
            else -> ONE_THOUSAND
        }
        return if (isValid) subVal * mult else 0L
    }

    fun calculateUnitBonus(
        unit: String,
        isAttached: Boolean,
        nextToken: Token?,
        halfBonus: Long,
    ): Long {
        val isRuoi = nextToken?.type == TokenType.WORD && nextToken.unaccented == "ruoi"
        val text = nextToken?.text.orEmpty()
        val subVal = text.toLongOrNull() ?: 0L

        val bonus = when {
            isRuoi -> halfBonus
            unit in thousandUnits && isAttached && subVal in 1L..MAX_SINGLE_DIGIT -> subVal * ONE_HUNDRED
            unit in millionUnits -> calculateMillionBonus(unit, isAttached, text, subVal)
            else -> 0L
        }
        return bonus
    }

    fun matchUnit(
        numToken: Token,
        unitToken: Token,
        nextToken: Token?,
    ): AmountMatch? {
        val unit = unitToken.unaccented
        val config = when (unit) {
            in millionUnits -> ONE_MILLION to FIVE_HUNDRED_THOUSAND
            in thousandUnits -> ONE_THOUSAND to FIVE_HUNDRED
            in hundredThousandUnits -> ONE_HUNDRED_THOUSAND to FIFTY_THOUSAND
            else -> null
        }
        val base = config?.let { parseDecimalAmount(numToken.text, it.first) }
        if (base == null) return null

        val isAttached = nextToken != null && unitToken.endIndex == nextToken.startIndex
        val bonus = calculateUnitBonus(unit, isAttached, nextToken, config.second)
        val consumed = if (bonus > 0L) THREE else 2
        return AmountMatch(base + bonus, consumed)
    }

    fun matchWordTokens(tokens: List<Token>, index: Int): AmountMatch? {
        val first = tokens[index].unaccented
        val second = tokens.getOrNull(index + 1)?.unaccented
        val third = tokens.getOrNull(index + 2)?.unaccented
        val next2 = tokens.getOrNull(index + 2)
        val hasRuoi = next2?.type == TokenType.WORD && next2.unaccented == "ruoi"

        val match = when (first) {
            "nua" -> when (second) {
                in millionUnits -> AmountMatch(FIVE_HUNDRED_THOUSAND, 2)
                in hundredThousandUnits -> AmountMatch(FIFTY_THOUSAND, 2)
                else -> null
            }

            else -> matchCompoundWord(first, second, third)
                ?: wordDigits[first]?.let { matchSingleWordDigit(it, second, hasRuoi) }
        }
        return match
    }

    private fun matchCompoundWord(first: String, second: String?, third: String?): AmountMatch? {
        if (third !in thousandUnits || second == null) return null

        return when {
            first == "muoi" -> {
                val sub = if (second == "lam") FIVE else (wordDigits[second] ?: 0L)
                if (sub in 1..MAX_SINGLE_DIGIT) AmountMatch((TEN + sub) * ONE_THOUSAND, THREE) else null
            }

            second in setOf("muoi", "chuc") && wordDigits.containsKey(first) -> {
                AmountMatch((wordDigits[first] ?: 0L) * TEN * ONE_THOUSAND, THREE)
            }

            else -> null
        }
    }

    private fun matchSingleWordDigit(digit: Long, second: String?, hasRuoi: Boolean): AmountMatch? = when (second) {
        "chuc" -> AmountMatch(digit * TEN_THOUSAND, 2)

        "tram" -> AmountMatch(
            digit * ONE_HUNDRED_THOUSAND + if (hasRuoi) FIFTY_THOUSAND else 0L,
            if (hasRuoi) THREE else 2,
        )

        in thousandUnits -> AmountMatch(
            digit * ONE_THOUSAND + if (hasRuoi) FIVE_HUNDRED else 0L,
            if (hasRuoi) THREE else 2,
        )

        in millionUnits -> AmountMatch(
            digit * ONE_MILLION + if (hasRuoi) FIVE_HUNDRED_THOUSAND else 0L,
            if (hasRuoi) THREE else 2,
        )

        else -> null
    }
}
