package hung.lct.chitieu.core.parser

private val clauseDelimiters = setOf(",", ";", "\n")
private val conjunctionDelimiters = setOf("va", "voi")

/**
 * Đại diện cho một phân đoạn token thô kèm số tiền tương ứng trước khi phân tích chi tiết.
 *
 * @property tokens Danh sách các token thuộc mệnh đề.
 * @property amount Khoản tiền được trích xuất của mệnh đề.
 * @property tokenRange Dải chỉ số token ban đầu trong câu.
 */
internal data class RawClause(
    val tokens: List<Token>,
    val amount: ParsedAmount,
    val tokenRange: IntRange,
)

/**
 * Bộ phân tách câu thành các mệnh đề giao dịch độc lập (FR-07).
 */
internal object ClauseSplitter {

    /**
     * Tách câu thành danh sách các [RawClause].
     *
     * Một câu chỉ được tách thành nhiều mệnh đề khi có nhiều khoản tiền tương ứng.
     */
    fun split(sentence: NormalizedSentence): List<RawClause> {
        val amounts = AmountParser.parseAll(sentence)
        return when (amounts.size) {
            0 -> emptyList()

            1 -> {
                val filteredTokens = sentence.tokens.filter {
                    it.text !in clauseDelimiters && it.type != TokenType.NEWLINE
                }
                listOf(
                    RawClause(
                        tokens = filteredTokens,
                        amount = amounts.first(),
                        tokenRange = sentence.tokens.indices,
                    ),
                )
            }

            else -> splitMultipleAmounts(sentence.tokens, amounts)
        }
    }

    private fun splitMultipleAmounts(
        tokens: List<Token>,
        amounts: List<ParsedAmount>,
    ): List<RawClause> {
        val splitBoundaries = mutableListOf<IntRange>()

        for (k in 0 until amounts.size - 1) {
            val endPrev = amounts[k].tokenRange.last
            val startNext = amounts[k + 1].tokenRange.first

            val delimIndex = findDelimiterBetween(tokens, endPrev + 1, startNext)
            if (delimIndex != null) {
                // Điểm ranh giới là token tại delimIndex
                splitBoundaries.add(delimIndex..delimIndex)
            } else {
                // Không có token phân cách, ranh giới ảo ngay sau endPrev
                splitBoundaries.add((endPrev + 1)..(endPrev))
            }
        }

        return buildClausesFromBoundaries(tokens, amounts, splitBoundaries)
    }

    private fun findDelimiterBetween(tokens: List<Token>, fromIndex: Int, toIndex: Int): Int? {
        for (idx in fromIndex until toIndex) {
            val t = tokens[idx]
            val isDelim = t.text in clauseDelimiters ||
                t.type == TokenType.NEWLINE ||
                t.unaccented in conjunctionDelimiters
            if (isDelim) return idx
        }
        return null
    }

    private fun buildClausesFromBoundaries(
        tokens: List<Token>,
        amounts: List<ParsedAmount>,
        splitBoundaries: List<IntRange>,
    ): List<RawClause> {
        val result = mutableListOf<RawClause>()
        var startIdx = 0

        for (k in amounts.indices) {
            val endIdx = if (k < splitBoundaries.size) splitBoundaries[k].first - 1 else tokens.size - 1
            if (startIdx <= endIdx) {
                val clauseTokens = tokens.subList(startIdx, endIdx + 1).filter {
                    it.text !in clauseDelimiters && it.type != TokenType.NEWLINE
                }
                result.add(
                    RawClause(
                        tokens = clauseTokens,
                        amount = amounts[k],
                        tokenRange = startIdx..endIdx,
                    ),
                )
            }
            if (k < splitBoundaries.size) {
                startIdx = splitBoundaries[k].last + 1
            }
        }

        return result
    }
}
