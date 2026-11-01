package hung.lct.chitieu.core.parser

import java.time.Clock
import java.time.LocalDateTime
import java.time.ZoneId

private const val DEFAULT_TIME_CONFIDENCE = 0.5

/**
 * Bộ điều phối phân tích câu giao dịch hoàn chỉnh (FR-01, FR-02, FR-03, FR-04, FR-07).
 */
object TransactionParser {

    /**
     * Phân tích toàn bộ câu nhập thành danh sách các mệnh đề giao dịch [ParsedClause].
     *
     * @param sentence Câu đã được tiền xử lý và tách token.
     * @param clock Đồng hồ hệ thống để xác định thời gian hiện tại (bắt buộc truyền).
     * @param zoneId Múi giờ sử dụng để quy đổi thời gian.
     * @param userKeywords Từ điển từ khóa danh mục tùy chỉnh của người dùng (FR-09).
     * @param walletAliases Danh sách bí danh ví (FR-06). TODO(S2-08): Mặc định emptyList() khi ví đến từ Room.
     */
    fun parse(
        sentence: NormalizedSentence,
        clock: Clock,
        zoneId: ZoneId = DateTimeParser.DEFAULT_ZONE_ID,
        userKeywords: Map<String, String> = emptyMap(),
        walletAliases: List<String> = defaultWalletAliases,
    ): List<ParsedClause> {
        val rawClauses = ClauseSplitter.split(sentence)
        if (rawClauses.isEmpty()) return emptyList()

        val fallbackDateTime = createFallbackDateTime(clock, zoneId)
        val firstClauseTime = DateTimeParser.parseAll(rawClauses[0].tokens, clock, zoneId).firstOrNull()
        val inheritedDateTime = resolveInheritedDateTime(firstClauseTime)
        val result = mutableListOf<ParsedClause>()

        for (i in rawClauses.indices) {
            val clause = rawClauses[i]
            val clauseTimeMatch = if (i == 0) {
                firstClauseTime
            } else {
                DateTimeParser.parseAll(clause.tokens, clock, zoneId).firstOrNull()
            }

            val resolvedDateTime = when {
                clauseTimeMatch != null -> clauseTimeMatch
                i > 0 && inheritedDateTime != null -> inheritedDateTime
                else -> fallbackDateTime
            }

            val category = CategoryClassifier.classify(clause.tokens, userKeywords)
            val note = NoteExtractor.extract(
                tokens = clause.tokens,
                raw = sentence.raw,
                amount = clause.amount,
                dateTime = clauseTimeMatch,
                walletAliases = walletAliases,
            )

            result.add(
                ParsedClause(
                    amount = clause.amount,
                    dateTime = resolvedDateTime,
                    category = category,
                    note = note,
                    tokenRange = clause.tokenRange,
                ),
            )
        }

        return result
    }

    private fun createFallbackDateTime(clock: Clock, zoneId: ZoneId): ParsedDateTime = ParsedDateTime(
        localDateTime = LocalDateTime.now(clock.withZone(zoneId)),
        startIndex = -1,
        endIndex = -1,
        tokenRange = null,
        confidence = DEFAULT_TIME_CONFIDENCE,
        isDefault = true,
        type = DateTimeType.DEFAULT_CLOCK,
    )

    private fun resolveInheritedDateTime(firstClauseTime: ParsedDateTime?): ParsedDateTime? {
        if (firstClauseTime == null || firstClauseTime.isDefault) return null
        val canInherit = firstClauseTime.type == DateTimeType.DATE_EXPLICIT ||
            firstClauseTime.type == DateTimeType.SESSION_WITH_MODIFIER
        return if (canInherit) {
            firstClauseTime.copy(startIndex = -1, endIndex = -1, tokenRange = null)
        } else {
            null
        }
    }
}
