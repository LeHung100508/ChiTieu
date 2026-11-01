package hung.lct.chitieu.core.parser

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId

private const val MIN_REQUIRED_ACCURACY = 95.0
private const val EXPECTED_FULL_CONFIDENCE = 1.0
private const val EXPECTED_DEFAULT_CONFIDENCE = 0.5

private data class ClauseEvaluation(
    val amountMatches: Int,
    val dateTimeMatches: Int,
    val categoryMatches: Int,
    val noteMatches: Int,
)

class TransactionParserTest {

    private val zoneId = ZoneId.of("Asia/Ho_Chi_Minh")

    // Mốc quy chiếu 2026-10-20T10:00:00+07:00 (Thứ Ba)
    private val clock = Clock.fixed(Instant.parse("2026-10-20T03:00:00Z"), zoneId)

    // 8 câu chuyển khoản hoãn sang S2-08 theo Nợ kỹ thuật
    private val skippedTransferCases = setOf(
        "tc_117",
        "tc_118",
        "tc_119",
        "tc_120",
        "tc_121",
        "tc_122",
        "tc_123",
        "tc_124",
    )

    private fun normalize(input: String) = VietnameseNormalizer.normalize(input)

    @Test
    fun parse_singleItem_extractsAllFieldsCorrectly() {
        val result = TransactionParser.parse(normalize("sáng phở bò 45k"), clock, zoneId)
        assertEquals(1, result.size)

        val item = result[0]
        assertEquals(45_000L, item.amount.value)
        assertEquals("food_dining", item.category.categoryKey)
        assertEquals(EXPECTED_FULL_CONFIDENCE, item.category.confidence, 0.001)
        assertEquals("phở bò", item.note)
        assertEquals("2026-10-20T08:00", item.dateTime.localDateTime.toString())
        assertFalse(item.dateTime.isDefault)
    }

    @Test
    fun parse_noTimeProvided_assignsDefaultTimeWithLowerConfidence() {
        val result = TransactionParser.parse(normalize("bún riêu 35k"), clock, zoneId)
        assertEquals(1, result.size)

        val item = result[0]
        assertEquals("2026-10-20T10:00", item.dateTime.localDateTime.toString())
        assertTrue(item.dateTime.isDefault)
        assertEquals(EXPECTED_DEFAULT_CONFIDENCE, item.dateTime.confidence, 0.001)
        assertEquals(-1, item.dateTime.startIndex)
        assertEquals(-1, item.dateTime.endIndex)
        assertNull(item.dateTime.tokenRange)
    }

    @Test
    fun parse_multiItemsWithInheritedDate_appliesYesterdayToBothClauses() {
        val result = TransactionParser.parse(normalize("hôm qua ăn sáng 35k, ăn trưa 45k"), clock, zoneId)
        assertEquals(2, result.size)

        assertEquals(35_000L, result[0].amount.value)
        assertEquals("2026-10-19", result[0].dateTime.localDateTime.toLocalDate().toString())
        assertEquals("food_dining", result[0].category.categoryKey)
        assertEquals("ăn sáng", result[0].note)
        assertTrue(result[0].dateTime.startIndex >= 0)

        assertEquals(45_000L, result[1].amount.value)
        assertEquals("2026-10-19", result[1].dateTime.localDateTime.toLocalDate().toString())
        assertEquals("food_dining", result[1].category.categoryKey)
        assertEquals("ăn trưa", result[1].note)
        assertFalse(result[1].dateTime.isDefault)
        assertEquals(-1, result[1].dateTime.startIndex)
        assertEquals(-1, result[1].dateTime.endIndex)
        assertNull(result[1].dateTime.tokenRange)
    }

    @Test
    fun parse_standaloneSessionDoesNotInherit_laterClausesUseDefault() {
        val result = TransactionParser.parse(normalize("sáng phở 45k, trưa cơm 35k, cafe 29k"), clock, zoneId)
        assertEquals(3, result.size)

        assertEquals("2026-10-20T08:00", result[0].dateTime.localDateTime.toString())
        assertEquals("phở", result[0].note)

        assertEquals("2026-10-20T12:00", result[1].dateTime.localDateTime.toString())
        assertEquals("cơm", result[1].note)

        assertEquals("2026-10-20T10:00", result[2].dateTime.localDateTime.toString())
        assertTrue(result[2].dateTime.isDefault)
        assertEquals("cafe", result[2].note)
    }

    @Test
    fun parse_edgeCasesBeyondDataset_behaveAsExpected() {
        // 1. Grab về nhà 62k -> note giữ chữ hoa
        val grabResult = TransactionParser.parse(normalize("Grab về nhà 62k"), clock, zoneId)
        assertEquals("Grab về nhà", grabResult[0].note)

        // 2. mua quà sinh nhật 300k -> không mất chữ "quà"
        val quaResult = TransactionParser.parse(normalize("mua quà sinh nhật 300k"), clock, zoneId)
        assertEquals("mua quà sinh nhật", quaResult[0].note)

        // 3. vé chuyến bay 2tr -> không mất chữ "chuyến"
        val bayResult = TransactionParser.parse(normalize("vé chuyến bay 2tr"), clock, zoneId)
        assertEquals("vé chuyến bay", bayResult[0].note)

        // 4. mua thẻ điện thoại 100k -> không mất chữ "thẻ"
        val theResult = TransactionParser.parse(normalize("mua thẻ điện thoại 100k"), clock, zoneId)
        assertEquals("mua thẻ điện thoại", theResult[0].note)

        // 5. bảng vẽ 80k -> không mất chữ "bảng"
        val bangResult = TransactionParser.parse(normalize("bảng vẽ 80k"), clock, zoneId)
        assertEquals("bảng vẽ", bangResult[0].note)

        // 6. sáng mua quà 200k, cafe 30k -> "quà" không làm lan thời gian sáng
        val sangQuaResult = TransactionParser.parse(normalize("sáng mua quà 200k, cafe 30k"), clock, zoneId)
        assertEquals("2026-10-20T08:00", sangQuaResult[0].dateTime.localDateTime.toString())
        assertEquals("2026-10-20T10:00", sangQuaResult[1].dateTime.localDateTime.toString())

        // 7. cà phê với bạn 30k, phở 40k -> không mất "với" trong note
        val voiBanResult = TransactionParser.parse(normalize("cà phê với bạn 30k, phở 40k"), clock, zoneId)
        assertEquals("cà phê với bạn", voiBanResult[0].note)
        assertEquals("phở", voiBanResult[1].note)

        // 8. bán đồ cũ được 350k -> loại bỏ chữ "được"
        val banDoCuResult = TransactionParser.parse(normalize("bán đồ cũ được 350k"), clock, zoneId)
        assertEquals("bán đồ cũ", banDoCuResult[0].note)

        // 9. nạp 4g 50k trừ ví momo -> note là "nạp 4g"
        val nap4gResult = TransactionParser.parse(normalize("nạp 4g 50k trừ ví momo"), clock, zoneId)
        assertEquals("nạp 4g", nap4gResult[0].note)
    }

    @Test
    fun parseDataset_evaluatesAllFieldsAcrossAllClauses() {
        val dataset = TestDatasetLoader.dataset
        val walletAliases = dataset.mockWallets.flatMap { it.aliases }

        var totalClauses = 0
        var correctAmountClauses = 0
        var correctDateTimeClauses = 0
        var correctCategoryClauses = 0
        var correctNoteClauses = 0

        for (tc in dataset.testCases) {
            if (tc.id in skippedTransferCases) continue
            val expected = tc.expected
            totalClauses += expected.size

            val actual = TransactionParser.parse(normalize(tc.input), clock, zoneId, walletAliases = walletAliases)
            if (expected.size == actual.size) {
                val eval = evaluateClauses(expected, actual)
                correctAmountClauses += eval.amountMatches
                correctDateTimeClauses += eval.dateTimeMatches
                correctCategoryClauses += eval.categoryMatches
                correctNoteClauses += eval.noteMatches
            }
        }

        assertAccuracyThresholds(
            totalClauses,
            correctAmountClauses,
            correctDateTimeClauses,
            correctCategoryClauses,
            correctNoteClauses,
        )
    }

    private fun evaluateClauses(
        expected: List<ParsedItemDto>,
        actual: List<ParsedClause>,
    ): ClauseEvaluation {
        var amounts = 0
        var times = 0
        var cats = 0
        var notes = 0

        for (k in expected.indices) {
            if (isAmountMatch(expected[k], actual[k])) amounts++
            if (isDateTimeMatch(expected[k], actual[k])) times++
            if (isCategoryMatch(expected[k], actual[k])) cats++
            if (isNoteMatch(expected[k], actual[k])) notes++
        }
        return ClauseEvaluation(amounts, times, cats, notes)
    }

    private fun assertAccuracyThresholds(
        totalClauses: Int,
        amounts: Int,
        times: Int,
        cats: Int,
        notes: Int,
    ) {
        val amountAccuracy = (amounts.toDouble() / totalClauses) * 100.0
        val dateTimeAccuracy = (times.toDouble() / totalClauses) * 100.0
        val categoryAccuracy = (cats.toDouble() / totalClauses) * 100.0
        val noteAccuracy = (notes.toDouble() / totalClauses) * 100.0

        println("Đo lường 4 trường trên tổng số $totalClauses khoản:")
        println("- Số tiền (amount): $amounts / $totalClauses ($amountAccuracy%)")
        println("- Thời gian (datetime): $times / $totalClauses ($dateTimeAccuracy%)")
        println("- Danh mục (category): $cats / $totalClauses ($categoryAccuracy%)")
        println("- Ghi chú (note): $notes / $totalClauses ($noteAccuracy%)")

        assertTrue("Độ chính xác amount >= 95%", amountAccuracy >= MIN_REQUIRED_ACCURACY)
        assertTrue("Độ chính xác datetime >= 95%", dateTimeAccuracy >= MIN_REQUIRED_ACCURACY)
        assertTrue("Độ chính xác category >= 95%", categoryAccuracy >= MIN_REQUIRED_ACCURACY)
        assertTrue("Độ chính xác note >= 95%", noteAccuracy >= MIN_REQUIRED_ACCURACY)
    }

    private fun isAmountMatch(
        expected: ParsedItemDto,
        actual: ParsedClause,
    ): Boolean = expected.amount == actual.amount.value

    private fun isDateTimeMatch(expected: ParsedItemDto, actual: ParsedClause): Boolean {
        val expLocal = OffsetDateTime.parse(expected.datetime).toLocalDateTime()
        return expLocal == actual.dateTime.localDateTime
    }

    private fun isCategoryMatch(expected: ParsedItemDto, actual: ParsedClause): Boolean {
        val expCategory = expected.categoryKey
        return expCategory == null || expCategory == actual.category.categoryKey
    }

    private fun isNoteMatch(expected: ParsedItemDto, actual: ParsedClause): Boolean {
        val expNote = expected.note?.trim()?.lowercase()
        val actNote = actual.note?.trim()?.lowercase()
        return expNote == actNote
    }
}
