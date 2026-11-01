package hung.lct.chitieu.core.parser

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class ParsedItemDto(
    val type: String,
    val amount: Long,
    val categoryKey: String? = null,
    val datetime: String,
    val wallet: String? = null,
    val sourceWallet: String? = null,
    val targetWallet: String? = null,
    val note: String? = null,
)

@Serializable
data class TestCaseDto(
    val id: String,
    val input: String,
    val tags: List<String> = emptyList(),
    val expected: List<ParsedItemDto> = emptyList(),
)

@Serializable
data class MockWalletDto(
    val key: String,
    val name: String,
    val aliases: List<String> = emptyList(),
)

@Serializable
data class DatasetDto(
    val version: Int,
    val referenceTime: String,
    val timezone: String,
    val description: String,
    val totalCases: Int,
    val mockWallets: List<MockWalletDto> = emptyList(),
    val testCases: List<TestCaseDto>,
)

/**
 * Đối tượng tiện ích hỗ trợ đọc và giải mã file test_cases.json dùng chung cho các bộ kiểm thử.
 */
object TestDatasetLoader {

    private val strictJson = Json {
        ignoreUnknownKeys = false
        isLenient = false
    }

    val dataset: DatasetDto by lazy {
        val stream = javaClass.classLoader.getResourceAsStream("test_cases.json")
            ?: error("Không tìm thấy file test_cases.json trong test resources")
        val content = stream.bufferedReader().use { it.readText() }
        strictJson.decodeFromString<DatasetDto>(content)
    }
}
