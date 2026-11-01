package hung.lct.chitieu.core.parser

import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import kotlin.math.ceil
import kotlin.system.measureNanoTime

private const val WARMUP_ROUNDS = 5
private const val MEASUREMENT_ROUNDS = 10
private const val PERCENTILE_50 = 0.50
private const val PERCENTILE_90 = 0.90
private const val PERCENTILE_95 = 0.95
private const val PERCENTILE_99 = 0.99
private const val NFR02_MAX_P95_MS = 50.0
private const val NANOS_PER_MILLI = 1_000_000.0
private const val MIN_MULTI_CLAUSE_COUNT = 2

private data class BenchmarkStats(
    val totalRuns: Int,
    val minMs: Double,
    val meanMs: Double,
    val p50Ms: Double,
    val p90Ms: Double,
    val p95Ms: Double,
    val p99Ms: Double,
    val maxMs: Double,
)

/**
 * Kiểm thử hiệu năng sớm của bộ phân tích câu tự nhiên trên JVM theo NFR-02.
 *
 * Yêu cầu: Phân vị 95 (p95) của thời gian phân tích câu phải đạt dưới 50 ms.
 */
class TransactionParserBenchmarkTest {

    private val zoneId = ZoneId.of("Asia/Ho_Chi_Minh")
    private val clock = Clock.fixed(Instant.parse("2026-10-20T03:00:00Z"), zoneId)

    private lateinit var sampleInputs: List<String>
    private lateinit var multiClauseInputs: List<String>
    private lateinit var walletAliases: List<String>

    @Before
    fun setUp() {
        val dataset = TestDatasetLoader.dataset
        sampleInputs = dataset.testCases.map { it.input }
        multiClauseInputs = dataset.testCases
            .filter { it.expected.size >= MIN_MULTI_CLAUSE_COUNT }
            .map { it.input }
        walletAliases = dataset.mockWallets.flatMap { it.aliases }
    }

    @Test
    fun benchmarkColdStart_measuresInitialCallLatency() {
        // Đo lường lần gọi đầu tiên để phát hiện rủi ro trễ khi nạp class và giải mã từ điển (Nên sửa 1)
        val firstInput = sampleInputs.first()
        var coldClauseCount = 0
        val coldNanos = measureNanoTime {
            val normalized = VietnameseNormalizer.normalize(firstInput)
            val clauses = TransactionParser.parse(normalized, clock, zoneId, walletAliases = walletAliases)
            coldClauseCount = clauses.size
        }
        val coldMs = coldNanos / NANOS_PER_MILLI
        assertTrue("Kết quả cold call phải bóc tách được ít nhất một mệnh đề", coldClauseCount > 0)

        println("=== ĐO LƯỜNG LẦN GỌI ĐẦU TIÊN (COLD START) ===")
        println("- Thời gian phân tích câu đầu tiên (test run này): ${"%.3f".format(coldMs)} ms")
        println("- LƯU Ý: Nếu chạy cùng cả test suite, JVM đã nạp class từ trước nên kết quả sẽ bị ấm.")
        println("- Để đo Cold Start thực tế trên JVM mới độc lập, hãy chạy lệnh:")
        println(
            "  ./gradlew :core:parser:test --tests \"*TransactionParserBenchmarkTest.benchmarkColdStart*\"" +
                " --rerun-tasks",
        )
        println("- Khuyến nghị S2-05: Nạp sẵn từ điển ngoài luồng chính khi mở màn Nhập nhanh (FR-01).")
        println("===============================================")
    }

    @Test
    fun benchmarkPipeline_p95UnderFiftyMillisecondsAcrossAllSampleSentences() {
        // 1. Warmup JVM: Chạy nhiều vòng lặp trước khi đo để JIT tối ưu hóa mã bytecode
        warmupJvm(sampleInputs, walletAliases)

        // 2. Đo lường: Thu thập thời gian xử lý kèm biến cộng dồn chống Dead Code Elimination (Nên sửa 2)
        val durationsMs = collectDurations(sampleInputs, walletAliases)

        // 3. Tính toán các chỉ số thống kê hiệu năng theo chuẩn nearest-rank
        val stats = calculateStats(durationsMs)

        // 4. Đo lường riêng cho nhóm câu nhiều khoản (Gợi ý 2)
        val multiStats = calculateStats(collectDurations(multiClauseInputs, walletAliases))

        // 5. In báo cáo thống kê ra log
        printBenchmarkReport(stats, multiStats)

        // 6. Kiểm tra ràng buộc NFR-02: p95 < 50 ms
        assertTrue(
            "Hiệu năng parser không đạt NFR-02: p95 = ${"%.3f".format(stats.p95Ms)} ms (ngưỡng: $NFR02_MAX_P95_MS ms)",
            stats.p95Ms < NFR02_MAX_P95_MS,
        )
    }

    @Test
    fun benchmarkComplexSentence_worstCaseUnderThreshold() {
        val complexInput = "hôm qua sáng ăn bún chả 45k quẹt thẻ vcb, trưa mua sách 120k và chiều đổ xăng 50k"

        // Warmup riêng cho câu phức tạp
        repeat(WARMUP_ROUNDS * MEASUREMENT_ROUNDS) {
            val normalized = VietnameseNormalizer.normalize(complexInput)
            TransactionParser.parse(normalized, clock, zoneId, walletAliases = walletAliases)
        }

        val iterations = 100
        val durationsMs = mutableListOf<Double>()
        var blackholeSum = 0L

        repeat(iterations) {
            val elapsedNanos = measureNanoTime {
                val normalized = VietnameseNormalizer.normalize(complexInput)
                val clauses = TransactionParser.parse(normalized, clock, zoneId, walletAliases = walletAliases)
                blackholeSum += clauses.sumOf { it.amount.value }
            }
            durationsMs.add(elapsedNanos / NANOS_PER_MILLI)
        }
        assertTrue("Blackhole sum phải được tính toán", blackholeSum > 0)

        val stats = calculateStats(durationsMs)
        println("Đo lường câu phức tạp nhiều khoản (worst-case across $iterations runs):")
        println("- Input: \"$complexInput\"")
        println("- Min: ${"%.3f".format(stats.minMs)} ms | Mean: ${"%.3f".format(stats.meanMs)} ms")
        println("- p95: ${"%.3f".format(stats.p95Ms)} ms | Max: ${"%.3f".format(stats.maxMs)} ms")

        assertTrue(
            "Câu phức tạp vượt quá ngưỡng $NFR02_MAX_P95_MS ms (p95 = ${stats.p95Ms} ms)",
            stats.p95Ms < NFR02_MAX_P95_MS,
        )
    }

    private fun warmupJvm(inputs: List<String>, aliases: List<String>) {
        repeat(WARMUP_ROUNDS) {
            for (input in inputs) {
                val normalized = VietnameseNormalizer.normalize(input)
                TransactionParser.parse(normalized, clock, zoneId, walletAliases = aliases)
            }
        }
    }

    private fun collectDurations(inputs: List<String>, aliases: List<String>): List<Double> {
        val durations = ArrayList<Double>(inputs.size * MEASUREMENT_ROUNDS)
        var blackholeCount = 0

        repeat(MEASUREMENT_ROUNDS) {
            for (input in inputs) {
                val elapsedNanos = measureNanoTime {
                    val normalized = VietnameseNormalizer.normalize(input)
                    val clauses = TransactionParser.parse(normalized, clock, zoneId, walletAliases = aliases)
                    blackholeCount += clauses.size
                }
                durations.add(elapsedNanos / NANOS_PER_MILLI)
            }
        }
        assertTrue("Blackhole count phải được cập nhật qua các vòng lặp", blackholeCount > 0)
        return durations
    }

    private fun calculateStats(durationsMs: List<Double>): BenchmarkStats {
        val sorted = durationsMs.sorted()
        return BenchmarkStats(
            totalRuns = sorted.size,
            minMs = sorted.first(),
            meanMs = sorted.average(),
            p50Ms = getPercentile(sorted, PERCENTILE_50),
            p90Ms = getPercentile(sorted, PERCENTILE_90),
            p95Ms = getPercentile(sorted, PERCENTILE_95),
            p99Ms = getPercentile(sorted, PERCENTILE_99),
            maxMs = sorted.last(),
        )
    }

    private fun getPercentile(sorted: List<Double>, percentile: Double): Double {
        val rank = ceil(sorted.size * percentile).toInt()
        val index = (rank - 1).coerceIn(0, sorted.size - 1)
        return sorted[index]
    }

    private fun printBenchmarkReport(stats: BenchmarkStats, multiStats: BenchmarkStats) {
        println("=== KẾT QUẢ BENCHMARK PARSER TRÊN JVM (NFR-02) ===")
        println("Tổng số lượt thực thi đo lường: ${stats.totalRuns} (200 câu x $MEASUREMENT_ROUNDS vòng)")
        println("Vòng khởi động máy ảo (Warmup): $WARMUP_ROUNDS vòng")
        println("--------------------------------------------------")
        println("Nhanh nhất (Min) : ${"%.4f".format(stats.minMs)} ms")
        println("Trung bình (Mean): ${"%.4f".format(stats.meanMs)} ms")
        println("Phân vị 50 (p50) : ${"%.4f".format(stats.p50Ms)} ms")
        println("Phân vị 90 (p90) : ${"%.4f".format(stats.p90Ms)} ms")
        println("Phân vị 95 (p95) : ${"%.4f".format(stats.p95Ms)} ms (Ngưỡng NFR-02: < $NFR02_MAX_P95_MS ms)")
        println("Phân vị 99 (p99) : ${"%.4f".format(stats.p99Ms)} ms")
        println("Chậm nhất (Max)  : ${"%.4f".format(stats.maxMs)} ms")
        println("--------------------------------------------------")
        println("Nhóm câu nhiều khoản (>= 2 khoản, ${multiStats.totalRuns} lượt đo):")
        println("- p95: ${"%.4f".format(multiStats.p95Ms)} ms | Max: ${"%.4f".format(multiStats.maxMs)} ms")
        println("==================================================")
    }
}
