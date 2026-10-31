package hung.lct.chitieu.core.parser

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class DateTimeParserTest {

    // Neo mốc quy chiếu chuẩn: Thứ Ba, 20/10/2026 lúc 10:00:00 (+07:00)
    private val clock = Clock.fixed(
        Instant.parse("2026-10-20T03:00:00Z"),
        ZoneId.of("Asia/Ho_Chi_Minh"),
    )

    private fun normalize(input: String): NormalizedSentence = VietnameseNormalizer.normalize(input)

    @Test
    fun parseAll_emptyWhenNoDateTimeKeywords() {
        val matches = DateTimeParser.parseAll(normalize("phở bò 45k"), clock)
        assertTrue(matches.isEmpty())
    }

    @Test
    fun parseAll_matchesHomQua() {
        val matches = DateTimeParser.parseAll(normalize("hôm qua ăn ốc 150k"), clock)
        assertEquals(1, matches.size)
        assertEquals(LocalDateTime.of(2026, 10, 19, 10, 0, 0), matches[0].localDateTime)
        assertEquals(0..1, matches[0].tokenRange)
        assertEquals(0, matches[0].startIndex)
        assertEquals(7, matches[0].endIndex)
    }

    @Test
    fun parseAll_matchesHomKia() {
        val matches = DateTimeParser.parseAll(normalize("hôm kia mua thuốc 60k"), clock)
        assertEquals(1, matches.size)
        assertEquals(LocalDateTime.of(2026, 10, 18, 10, 0, 0), matches[0].localDateTime)
        assertEquals(0..1, matches[0].tokenRange)
    }

    @Test
    fun parseAll_matchesHomNay() {
        val matches = DateTimeParser.parseAll(normalize("hôm nay ăn cơm 30k"), clock)
        assertEquals(1, matches.size)
        assertEquals(LocalDateTime.of(2026, 10, 20, 10, 0, 0), matches[0].localDateTime)
        assertEquals(0..1, matches[0].tokenRange)
    }

    @Test
    fun parseAll_matchesSangNay() {
        val matches = DateTimeParser.parseAll(normalize("sáng nay cafe 30k"), clock)
        assertEquals(1, matches.size)
        assertEquals(LocalDateTime.of(2026, 10, 20, 8, 0, 0), matches[0].localDateTime)
        assertEquals(0..1, matches[0].tokenRange)
    }

    @Test
    fun parseAll_matchesTruaNay() {
        val matches = DateTimeParser.parseAll(normalize("trưa nay cơm văn phòng 40k"), clock)
        assertEquals(1, matches.size)
        assertEquals(LocalDateTime.of(2026, 10, 20, 12, 0, 0), matches[0].localDateTime)
    }

    @Test
    fun parseAll_matchesChieuNay() {
        val matches = DateTimeParser.parseAll(normalize("chiều nay đổ xăng 70k"), clock)
        assertEquals(1, matches.size)
        assertEquals(LocalDateTime.of(2026, 10, 20, 15, 0, 0), matches[0].localDateTime)
    }

    @Test
    fun parseAll_matchesToiQua() {
        val matches = DateTimeParser.parseAll(normalize("tối qua xem phim 120k"), clock)
        assertEquals(1, matches.size)
        assertEquals(LocalDateTime.of(2026, 10, 19, 20, 0, 0), matches[0].localDateTime)
    }

    @Test
    fun parseAll_matchesDemQua() {
        val matches = DateTimeParser.parseAll(normalize("đêm qua gọi đồ ăn 80k"), clock)
        assertEquals(1, matches.size)
        assertEquals(LocalDateTime.of(2026, 10, 19, 22, 0, 0), matches[0].localDateTime)
    }

    @Test
    fun parseAll_matchesThuHaiTuanTruoc() {
        val matches = DateTimeParser.parseAll(normalize("thứ hai tuần trước mua balo 250k"), clock)
        assertEquals(1, matches.size)
        assertEquals(LocalDateTime.of(2026, 10, 12, 10, 0, 0), matches[0].localDateTime)
        assertEquals(0..3, matches[0].tokenRange)
    }

    @Test
    fun parseAll_matchesThu2TuanTruoc() {
        val matches = DateTimeParser.parseAll(normalize("thứ 2 tuần trước đóng tiền học 500k"), clock)
        assertEquals(1, matches.size)
        assertEquals(LocalDateTime.of(2026, 10, 12, 10, 0, 0), matches[0].localDateTime)
        assertEquals(0..3, matches[0].tokenRange)
    }

    @Test
    fun parseAll_matchesNgay5() {
        val matches = DateTimeParser.parseAll(normalize("ngày 5 đóng tiền nhà 3tr"), clock)
        assertEquals(1, matches.size)
        assertEquals(LocalDateTime.of(2026, 10, 5, 10, 0, 0), matches[0].localDateTime)
        assertEquals(0..1, matches[0].tokenRange)
    }

    @Test
    fun parseAll_matchesMung5() {
        val matches = DateTimeParser.parseAll(normalize("mùng 5 mua máy xay 450k"), clock)
        assertEquals(1, matches.size)
        assertEquals(LocalDateTime.of(2026, 10, 5, 10, 0, 0), matches[0].localDateTime)
    }

    @Test
    fun parseAll_matchesNgay15Thang10() {
        val matches = DateTimeParser.parseAll(normalize("ngày 15 tháng 10 mua sữa cho con 380k"), clock)
        assertEquals(1, matches.size)
        assertEquals(LocalDateTime.of(2026, 10, 15, 10, 0, 0), matches[0].localDateTime)
        assertEquals(0..3, matches[0].tokenRange)
    }

    @Test
    fun parseAll_matchesSlashDate() {
        val matches = DateTimeParser.parseAll(normalize("15/10 mua vé xem kịch 300k"), clock)
        assertEquals(1, matches.size)
        assertEquals(LocalDateTime.of(2026, 10, 15, 10, 0, 0), matches[0].localDateTime)
        assertEquals(0..2, matches[0].tokenRange)
    }

    @Test
    fun parseAll_matchesHomSlashDate() {
        val matches = DateTimeParser.parseAll(normalize("tiền gym 400k hôm 15/10"), clock)
        assertEquals(1, matches.size)
        assertEquals(LocalDateTime.of(2026, 10, 15, 10, 0, 0), matches[0].localDateTime)
    }

    @Test
    fun parseAll_matchesAtEndOfSentence() {
        val matches = DateTimeParser.parseAll(normalize("ăn phở 45k hôm qua"), clock)
        assertEquals(1, matches.size)
        assertEquals(LocalDateTime.of(2026, 10, 19, 10, 0, 0), matches[0].localDateTime)
    }

    @Test
    fun parseAll_ignoresMealWordsPrefix() {
        val matches = DateTimeParser.parseAll(normalize("hôm qua ăn sáng 35k, ăn trưa 45k"), clock)
        assertEquals(1, matches.size)
        // Chỉ khớp "hôm qua", không bị nuốt "sáng" hay "trưa" trong "ăn sáng", "ăn trưa"
        assertEquals(LocalDateTime.of(2026, 10, 19, 10, 0, 0), matches[0].localDateTime)
    }

    @Test
    fun parseAll_multipleClausesInOneSentence() {
        val matches = DateTimeParser.parseAll(normalize("sáng phở 45k, trưa cơm 35k, cafe 29k"), clock)
        assertEquals(2, matches.size)
        assertEquals(LocalDateTime.of(2026, 10, 20, 8, 0, 0), matches[0].localDateTime)
        assertEquals(LocalDateTime.of(2026, 10, 20, 12, 0, 0), matches[1].localDateTime)
    }

    @Test
    fun parseAll_ignoresTransferSang() {
        val matches = DateTimeParser.parseAll(normalize("chuyển 2tr từ VCB sang ví tiền mặt"), clock)
        assertTrue(matches.isEmpty())
    }

    @Test
    fun parseOrDefault_fallsBackToClockTimeWhenNoDateTime() {
        val dt = DateTimeParser.parseOrDefault(normalize("phở bò 45k"), clock)
        assertEquals(LocalDateTime.of(2026, 10, 20, 10, 0, 0), dt)
    }

    @Test
    fun dateTimeParser_accuracyOn200DatasetCases() {
        val inputStream = javaClass.classLoader.getResourceAsStream("test_cases.json")
        val content = inputStream!!.bufferedReader().use { it.readText() }
        val json = Json { ignoreUnknownKeys = true }
        val dataset = json.decodeFromString<DatasetDto>(content)
        val zone = ZoneId.of("Asia/Ho_Chi_Minh")
        val formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ssXXX")

        val failedCases = mutableListOf<String>()
        for (tc in dataset.testCases) {
            checkTestCaseAccuracy(tc, zone, formatter, failedCases)
        }

        assertTrue(
            "Tất cả các mốc thời gian trong 200 câu mẫu phải nhận diện đúng 100%.\n" +
                failedCases.joinToString("\n"),
            failedCases.isEmpty(),
        )
    }

    @Test
    fun parseAll_non10AmClock_usesCurrentClockTime() {
        // Clock lúc 21:35 tối ngày 20/10/2026
        val eveningClock = Clock.fixed(
            Instant.parse("2026-10-20T14:35:00Z"),
            ZoneId.of("Asia/Ho_Chi_Minh"),
        )
        val homQua = DateTimeParser.parseAll(normalize("hôm qua ăn ốc 150k"), eveningClock)
        assertEquals(LocalDateTime.of(2026, 10, 19, 21, 35, 0), homQua.first().localDateTime)

        val homNay = DateTimeParser.parseAll(normalize("hôm nay ăn cơm 30k"), eveningClock)
        assertEquals(LocalDateTime.of(2026, 10, 20, 21, 35, 0), homNay.first().localDateTime)
    }

    @Test
    fun parseAll_invalidDateDoesNotCrash_returnsEmpty() {
        // Ngày 31 vào tháng 4 (tháng trước của tháng 5 chỉ có 30 ngày)
        val mayClock = Clock.fixed(Instant.parse("2026-05-10T03:00:00Z"), ZoneId.of("Asia/Ho_Chi_Minh"))
        val match31 = DateTimeParser.parseAll(normalize("ngày 31 đóng tiền nhà 3tr"), mayClock)
        assertTrue(match31.isEmpty())

        // 31/11 (tháng 11 chỉ có 30 ngày)
        val matchNov31 = DateTimeParser.parseAll(normalize("31/11 ăn 50k"), clock)
        assertTrue(matchNov31.isEmpty())

        // Ngày 30/2 không tồn tại
        val matchFeb30 = DateTimeParser.parseAll(normalize("30/2 ăn 50k"), clock)
        assertTrue(matchFeb30.isEmpty())
    }

    @Test
    fun parseAll_futureDatesAdjustedToPast() {
        // Hôm nay ngày 10/11/2026, nói "ngày 25" -> phải là 25/10/2026 (tháng trước)
        val nov10Clock = Clock.fixed(Instant.parse("2026-11-10T03:00:00Z"), ZoneId.of("Asia/Ho_Chi_Minh"))
        val matchDay25 = DateTimeParser.parseAll(normalize("ngày 25 mua sách 100k"), nov10Clock)
        assertEquals(LocalDateTime.of(2026, 10, 25, 10, 0, 0), matchDay25.first().localDateTime)

        // Hôm nay 03/01/2027, nói "15/12" -> phải là 15/12/2026 (năm trước)
        val jan2027Clock = Clock.fixed(Instant.parse("2027-01-03T03:00:00Z"), ZoneId.of("Asia/Ho_Chi_Minh"))
        val matchDec15 = DateTimeParser.parseAll(normalize("15/12 mua quà 500k"), jan2027Clock)
        assertEquals(LocalDateTime.of(2026, 12, 15, 10, 0, 0), matchDec15.first().localDateTime)
    }

    @Test
    fun parseAll_sangQuaAndToiHomQua() {
        val sangQua = DateTimeParser.parseAll(normalize("sáng qua ăn phở 30k"), clock)
        assertEquals(LocalDateTime.of(2026, 10, 19, 8, 0, 0), sangQua.first().localDateTime)

        val toiHomQua = DateTimeParser.parseAll(normalize("tối hôm qua nhậu 300k"), clock)
        assertEquals(1, toiHomQua.size)
        assertEquals(LocalDateTime.of(2026, 10, 19, 20, 0, 0), toiHomQua.first().localDateTime)
        assertEquals(0..2, toiHomQua.first().tokenRange)
    }

    @Test
    fun parseAll_muaDenSangDoesNotMatchMorning() {
        val matches = DateTimeParser.parseAll(normalize("mua đèn sáng 200k"), clock)
        assertTrue(matches.isEmpty())
    }

    @Test
    fun parseAll_fractionDoesNotMatchDate() {
        val matches = DateTimeParser.parseAll(normalize("mua 1/2 kg thịt 50k"), clock)
        assertTrue(matches.isEmpty())
    }

    private fun checkTestCaseAccuracy(
        tc: TestCaseDto,
        zone: ZoneId,
        formatter: DateTimeFormatter,
        failedCases: MutableList<String>,
    ) {
        val nonDefaultExpected = tc.expected
            .map { it.datetime }
            .filter { it != "2026-10-20T10:00:00+07:00" }

        val matches = DateTimeParser.parseAll(normalize(tc.input), clock)
        val actualDts = matches.map { it.localDateTime.atZone(zone).format(formatter) }

        if (nonDefaultExpected.isNotEmpty()) {
            verifyExpectedDates(tc, nonDefaultExpected, actualDts, failedCases)
        } else {
            verifyNoUnexpectedDates(tc, actualDts, failedCases)
        }
    }

    private fun verifyExpectedDates(
        tc: TestCaseDto,
        expected: List<String>,
        actual: List<String>,
        failedCases: MutableList<String>,
    ) {
        for (item in expected) {
            if (item !in actual) {
                failedCases.add("${tc.id}: input='${tc.input}', expected=$item, actual=$actual")
            }
        }
    }

    private fun verifyNoUnexpectedDates(
        tc: TestCaseDto,
        actual: List<String>,
        failedCases: MutableList<String>,
    ) {
        for (item in actual) {
            if (item != "2026-10-20T10:00:00+07:00") {
                failedCases.add("${tc.id}: input='${tc.input}', falsely matched non-default $item")
            }
        }
    }
}
