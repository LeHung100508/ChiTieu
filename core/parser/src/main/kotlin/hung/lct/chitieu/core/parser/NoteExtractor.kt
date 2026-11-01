package hung.lct.chitieu.core.parser

private const val OFFSET_ONE = 1
private const val OFFSET_TWO = 2
private const val OFFSET_THREE = 3

private val expenditureNoiseWords = setOf("tieu", "het", "mat", "duoc")
private val singleWordPrepositions = setOf("bang", "qua", "tru", "tu", "quet")
private val twoWordPrepositions = setOf("tru vi", "tu vi", "qua vi", "chuyen khoan", "chuyen qua")
private val threeWordPrepositions = setOf("thanh toan qua", "thanh toan bang")

// TODO(S2-08): Xóa defaultWalletAliases khi ví được quản lý và nạp từ Room DB (FR-06)
internal val defaultWalletAliases = listOf(
    "quet the tin dung",
    "quẹt thẻ tín dụng",
    "thanh toan qua",
    "thanh toán qua",
    "the tin dung",
    "thẻ tín dụng",
    "ngan hang vcb",
    "ngân hàng vcb",
    "vi tien mat",
    "ví tiền mặt",
    "viettel money",
    "tien mat",
    "tiền mặt",
    "vi momo",
    "ví momo",
    "the vcb",
    "thẻ vcb",
    "quet the",
    "quẹt thẻ",
    "chuyen khoan",
    "chuyển khoản",
    "zalo pay",
    "shopee pay",
    "quan doi",
    "quân đội",
    "momo",
    "vcb",
    "vietcombank",
    "techcombank",
    "tech",
    "tcb",
    "mb",
    "mbbank",
    "bidv",
    "zalopay",
    "shopeepay",
    "viettelpay",
    "credit",
)

private data class WalletAliasRule(
    val words: List<String>,
)

/**
 * Trích xuất nội dung ghi chú (note) từ danh sách token của một mệnh đề (FR-01, FR-08).
 */
object NoteExtractor {

    /**
     * Trích xuất ghi chú từ mệnh đề sau khi đã loại bỏ tiền, thời gian và từ khóa ví.
     */
    fun extract(
        tokens: List<Token>,
        raw: String,
        amount: ParsedAmount,
        dateTime: ParsedDateTime?,
        walletAliases: List<String> = defaultWalletAliases,
    ): String? {
        val excludedIndices = mutableSetOf<Int>()

        // 1. Loại bỏ các token thuộc về số tiền
        excludeAmountTokens(tokens, amount, excludedIndices)

        // 2. Loại bỏ các token thuộc về thời gian (nếu không phải là thời gian mặc định)
        if (dateTime != null && !dateTime.isDefault) {
            excludeDateTimeTokens(tokens, dateTime, excludedIndices)
        }

        // 3. Loại bỏ các cụm từ ví và giới từ đứng trước
        WalletAliasHelper.excludeWalletAliases(tokens, walletAliases, excludedIndices)

        // 4. Loại bỏ hư từ tiêu hao ("tiêu", "hết", "mất", "được") quanh số tiền
        NoiseWordHelper.excludeExpenditureNoise(tokens, amount, excludedIndices)

        // 5. Cắt chuỗi từ raw theo các dải token liên tiếp còn lại
        return SpanExtractor.extractNoteFromSpans(tokens, raw, excludedIndices)
    }

    private fun excludeAmountTokens(
        tokens: List<Token>,
        amount: ParsedAmount,
        excludedIndices: MutableSet<Int>,
    ) {
        for (i in tokens.indices) {
            val t = tokens[i]
            if (t.startIndex >= amount.startIndex && t.endIndex <= amount.endIndex) {
                excludedIndices.add(i)
            }
        }
    }

    private fun excludeDateTimeTokens(
        tokens: List<Token>,
        dateTime: ParsedDateTime,
        excludedIndices: MutableSet<Int>,
    ) {
        for (i in tokens.indices) {
            val t = tokens[i]
            if (t.startIndex >= dateTime.startIndex && t.endIndex <= dateTime.endIndex) {
                excludedIndices.add(i)
            }
        }
    }
}

private object WalletAliasHelper {

    private val WHITESPACE_REGEX = Regex("\\s+")

    fun excludeWalletAliases(
        tokens: List<Token>,
        walletAliases: List<String>,
        excludedIndices: MutableSet<Int>,
    ) {
        val rules = walletAliases.map { alias ->
            val words = alias.trim().lowercase().split(WHITESPACE_REGEX).filter { it.isNotEmpty() }
            val unaccentedWords = words.map { VietnameseNormalizer.removeDiacritics(it) }
            WalletAliasRule(unaccentedWords)
        }.sortedWith(
            compareByDescending<WalletAliasRule> { it.words.size }
                .thenByDescending { it.words.joinToString(" ").length },
        )

        for (rule in rules) {
            applyWalletAliasRule(tokens, rule, excludedIndices)
        }
    }

    private fun applyWalletAliasRule(
        tokens: List<Token>,
        rule: WalletAliasRule,
        excludedIndices: MutableSet<Int>,
    ) {
        val k = rule.words.size
        if (k > tokens.size) return
        for (i in 0..(tokens.size - k)) {
            val hasExcluded = (i until (i + k)).any { it in excludedIndices }
            if (!hasExcluded && isAliasMatch(tokens, i, rule.words)) {
                for (j in 0 until k) {
                    excludedIndices.add(i + j)
                }
                excludeLeadingPreposition(tokens, i, excludedIndices)
            }
        }
    }

    private fun isAliasMatch(tokens: List<Token>, startIndex: Int, words: List<String>): Boolean {
        for (j in words.indices) {
            if (tokens[startIndex + j].unaccented != words[j]) return false
        }
        return true
    }

    private fun excludeLeadingPreposition(tokens: List<Token>, aliasStart: Int, excludedIndices: MutableSet<Int>) {
        if (aliasStart <= 0) return

        val handled = excludeMultiWordPreposition(tokens, aliasStart, excludedIndices)
        if (!handled && tokens[aliasStart - OFFSET_ONE].unaccented in singleWordPrepositions) {
            excludedIndices.add(aliasStart - OFFSET_ONE)
        }
    }

    private fun excludeMultiWordPreposition(
        tokens: List<Token>,
        aliasStart: Int,
        excludedIndices: MutableSet<Int>,
    ): Boolean {
        val isThreeWords = aliasStart >= OFFSET_THREE &&
            "${tokens[aliasStart - OFFSET_THREE].unaccented} ${tokens[aliasStart - OFFSET_TWO].unaccented} " +
            tokens[aliasStart - OFFSET_ONE].unaccented in threeWordPrepositions
        if (isThreeWords) {
            excludedIndices.add(aliasStart - OFFSET_THREE)
            excludedIndices.add(aliasStart - OFFSET_TWO)
            excludedIndices.add(aliasStart - OFFSET_ONE)
            return true
        }

        val isTwoWords = aliasStart >= OFFSET_TWO &&
            "${tokens[aliasStart - OFFSET_TWO].unaccented} " +
            tokens[aliasStart - OFFSET_ONE].unaccented in twoWordPrepositions
        if (isTwoWords) {
            excludedIndices.add(aliasStart - OFFSET_TWO)
            excludedIndices.add(aliasStart - OFFSET_ONE)
        }
        return isTwoWords
    }
}

private object NoiseWordHelper {

    fun excludeExpenditureNoise(
        tokens: List<Token>,
        amount: ParsedAmount,
        excludedIndices: MutableSet<Int>,
    ) {
        val amountFirstIndex = tokens.indexOfFirst {
            it.startIndex >= amount.startIndex && it.endIndex <= amount.endIndex
        }
        val amountLastIndex = tokens.indexOfLast {
            it.startIndex >= amount.startIndex && it.endIndex <= amount.endIndex
        }

        if (amountFirstIndex > 0) {
            excludeNoiseTokenIfSurrounded(tokens, amountFirstIndex - OFFSET_ONE, excludedIndices)
        }

        if (amountLastIndex in 0 until tokens.size - OFFSET_ONE) {
            excludeNoiseTokenIfSurrounded(tokens, amountLastIndex + OFFSET_ONE, excludedIndices)
        }
    }

    private fun excludeNoiseTokenIfSurrounded(
        tokens: List<Token>,
        targetIndex: Int,
        excludedIndices: MutableSet<Int>,
    ) {
        if (targetIndex !in excludedIndices && tokens[targetIndex].unaccented in expenditureNoiseWords) {
            val hasOtherWords = tokens.indices.any {
                it !in excludedIndices && it != targetIndex && tokens[it].type == TokenType.WORD
            }
            if (hasOtherWords) {
                excludedIndices.add(targetIndex)
            }
        }
    }
}

private object SpanExtractor {

    fun extractNoteFromSpans(
        tokens: List<Token>,
        raw: String,
        excludedIndices: Set<Int>,
    ): String? {
        val remainingIndices = tokens.indices.filter { idx ->
            idx !in excludedIndices &&
                tokens[idx].type != TokenType.PUNCTUATION &&
                tokens[idx].type != TokenType.NEWLINE
        }
        if (remainingIndices.isEmpty()) return null

        val spans = mutableListOf<List<Token>>()
        var currentSpan = mutableListOf<Token>()

        for (i in remainingIndices.indices) {
            val currIdx = remainingIndices[i]
            if (currentSpan.isEmpty() || currIdx == remainingIndices[i - OFFSET_ONE] + OFFSET_ONE) {
                currentSpan.add(tokens[currIdx])
            } else {
                spans.add(currentSpan)
                currentSpan = mutableListOf(tokens[currIdx])
            }
        }
        if (currentSpan.isNotEmpty()) {
            spans.add(currentSpan)
        }

        val spanTexts = spans.mapNotNull { span ->
            val start = span.first().startIndex
            val end = span.last().endIndex
            if (start in 0..raw.length && end in start..raw.length) {
                raw.substring(start, end).trim().ifBlank { null }
            } else {
                null
            }
        }

        val result = spanTexts.joinToString(" ").trim()
        return result.ifBlank { null }
    }
}
