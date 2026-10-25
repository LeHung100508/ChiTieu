package hung.lct.chitieu.core.parser

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AmountParserTest {

    private fun parse(input: String): List<ParsedAmount> {
        val sentence = VietnameseNormalizer.normalize(input)
        return AmountParser.parseAll(sentence)
    }

    private val json = Json {
        ignoreUnknownKeys = false
        isLenient = false
    }

    @Test
    fun parseAll_standardUnits_returnsCorrectValues() {
        assertEquals(45_000L, parse("phở bò 45k").first().value)
        assertEquals(40_000L, parse("bún bò 40 nghìn").first().value)
        assertEquals(30_000L, parse("bánh cuốn 30 ngàn").first().value)
        assertEquals(2_000_000L, parse("mua xe đạp 2 triệu").first().value)
        assertEquals(15_000_000L, parse("mua laptop 15tr").first().value)
        assertEquals(2_000_000L, parse("tiền phòng trọ 2 củ").first().value)
    }

    @Test
    fun parseAll_decimalUnits_returnsExactValuesWithoutRoundingErrors() {
        assertEquals(1_500_000L, parse("mua kính 1.5tr").first().value)
        assertEquals(1_500_000L, parse("mua kính 1,5tr").first().value)
        assertEquals(2_200_000L, parse("bàn làm việc 2,2tr").first().value)
    }

    @Test
    fun parseAll_thousandSeparatorsAndStandaloneNumbers() {
        assertEquals(450_000L, parse("tiền điện 450.000").first().value)
        assertEquals(350_000L, parse("mua áo sơ mi 350000").first().value)
        assertEquals(150_000L, parse("thay nhớt xe 150.000 đ").first().value)
        assertEquals(1_250_000L, parse("mua giày 1.250.000").first().value)
    }

    @Test
    fun parseAll_multiAmountSentence_extractsAllAmountsInOrder() {
        val sentence = VietnameseNormalizer.normalize("sáng phở 45k, trưa cơm 35k")
        val amounts = AmountParser.parseAll(sentence)

        assertEquals(2, amounts.size)
        assertEquals(45_000L, amounts[0].value)
        assertEquals(35_000L, amounts[1].value)

        // Kiểm tra cắt chuỗi gốc theo tọa độ
        assertEquals("45k", sentence.raw.substring(amounts[0].startIndex, amounts[0].endIndex))
        assertEquals("35k", sentence.raw.substring(amounts[1].startIndex, amounts[1].endIndex))
    }

    @Test
    fun parseAll_noMoneyInSentence_returnsEmptyList() {
        val amounts = parse("hôm nay trời đẹp đi dạo phố")
        assertTrue(amounts.isEmpty())
    }

    @Test
    fun parseAll_slangUnits_returnsCorrectValues() {
        assertEquals(500_000L, parse("đổ xăng 5 lít").first().value)
        assertEquals(300_000L, parse("mua balo 3 loét").first().value)
        assertEquals(150_000L, parse("cắt tóc 1 lít rưỡi").first().value)
        assertEquals(250_000L, parse("sửa điện nước 2 loét rưỡi").first().value)
    }

    @Test
    fun parseAll_compoundAmounts_returnsCorrectValues() {
        assertEquals(1_200_000L, parse("tai nghe 1tr2").first().value)
        assertEquals(1_500_000L, parse("mua máy ép 1tr5").first().value)
        assertEquals(1_200_000L, parse("mua bàn phím 1 củ 2").first().value)
        assertEquals(1_800_000L, parse("ăn hải sản 1 củ 8").first().value)
        assertEquals(85_500L, parse("sữa tắm 85k5").first().value)
        assertEquals(1_500_000L, parse("bảo dưỡng xe 1 củ rưỡi").first().value)
    }

    @Test
    fun parseAll_wordBasedAmounts_returnsCorrectValues() {
        assertEquals(500_000L, parse("mua nồi chiên nửa củ").first().value)
        assertEquals(50_000L, parse("uống trà cúc năm chục").first().value)
        assertEquals(30_000L, parse("rửa xe máy ba chục").first().value)
        assertEquals(100_000L, parse("vé xem phim một trăm").first().value)
        assertEquals(150_000L, parse("mua thuốc một trăm rưỡi").first().value)
        assertEquals(250_000L, parse("mua chuột máy tính hai trăm rưỡi").first().value)
    }

    @Test
    fun parseAll_noiseNumbers_ignoresQuantityAndAddress() {
        val res = parse("mua 2 cốc trà sữa 60k ở 123 Lê Lợi")
        assertEquals(1, res.size)
        assertEquals(60_000L, res.first().value)
    }

    @Test
    fun parseAll_spelledOutThousandsAndCurrencySuffix() {
        assertEquals(10_000L, parse("mười nghìn đồng").first().value)
        assertEquals(2_000L, parse("hai nghìn").first().value)
        assertEquals(5_000L, parse("năm ngàn").first().value)
        assertEquals(2_500L, parse("hai nghìn rưỡi").first().value)
        assertEquals(10_000L, parse("10 nghìn đồng").first().value)
    }

    @Test
    fun amountParser_accuracyOn200DatasetCases_reachesGate1Threshold() {
        val inputStream = javaClass.classLoader.getResourceAsStream("test_cases.json")
        val content = inputStream!!.bufferedReader().use { it.readText() }
        val dataset = json.decodeFromString<DatasetDto>(content)

        var correctCount = 0
        val totalCount = dataset.testCases.size
        val failedCases = mutableListOf<String>()

        for (tc in dataset.testCases) {
            val expectedAmounts = tc.expected.map { it.amount }
            val normalized = VietnameseNormalizer.normalize(tc.input)
            val actualAmounts = AmountParser.parseAll(normalized).map { it.value }

            if (actualAmounts == expectedAmounts) {
                correctCount++
            } else {
                failedCases.add(
                    "${tc.id}: input='${tc.input}', expected=$expectedAmounts, actual=$actualAmounts",
                )
            }
        }

        val accuracy = (correctCount.toDouble() / totalCount) * 100.0
        val accuracyStr = "%.1f".format(accuracy)
        assertTrue(
            "Độ chính xác số tiền phải đạt >= 95% (Cổng 1). Thực tế: $correctCount/$totalCount ($accuracyStr%)\n" +
                failedCases.joinToString("\n"),
            accuracy >= 95.0,
        )
    }

    @Test
    fun parseAll_unseenReviewCases_handlesCorrectly() {
        // Bắt buộc 1: Số lượng phía sau không dính liền thì không bị gộp
        assertEquals(45_000L, parse("trà sữa 45k 2 ly").first().value)
        assertEquals(40_000L, parse("phở 40k 2 người").first().value)

        // Bắt buộc 2: Phân biệt số thập phân đơn lẻ vs phân cách hàng nghìn
        val thit = parse("mua 1.5 kg thịt 120k")
        assertEquals(1, thit.size)
        assertEquals(120_000L, thit.first().value)

        val ngay = parse("ngày 15.10 ăn 50k")
        assertEquals(1, ngay.size)
        assertEquals(50_000L, ngay.first().value)

        assertEquals(1_500_000L, parse("1.500k").first().value)

        // Nên sửa 1 & 2: Loại bỏ 'm', số năm, số điện thoại
        val vai = parse("mua 3m vải 120k")
        assertEquals(1, vai.size)
        assertEquals(120_000L, vai.first().value)

        val hocPhi = parse("học phí năm 2026 5tr")
        assertEquals(1, hocPhi.size)
        assertEquals(5_000_000L, hocPhi.first().value)

        val ship = parse("gọi 0987654321 ship 30k")
        assertEquals(1, ship.size)
        assertEquals(30_000L, ship.first().value)

        // Nên sửa 3: Số đọc ghép
        assertEquals(20_000L, parse("hai mươi nghìn").first().value)
        assertEquals(15_000L, parse("mười lăm nghìn").first().value)

        // Gợi ý: 3 chữ số sau tr và số 0
        assertEquals(1_250_000L, parse("1tr250").first().value)
        assertTrue(parse("0.0005k").isEmpty())
    }
}
