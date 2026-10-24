package hung.lct.chitieu.core.parser

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.OffsetDateTime

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

class TestCaseDatasetTest {

    private val json = Json {
        ignoreUnknownKeys = false
        isLenient = false
    }

    @Test
    fun loadDataset_readsAll200CasesSuccessfully() {
        val inputStream = javaClass.classLoader.getResourceAsStream("test_cases.json")
        assertNotNull("Không tìm thấy file test_cases.json trong test resources", inputStream)

        val content = inputStream!!.bufferedReader().use { it.readText() }
        val dataset = json.decodeFromString<DatasetDto>(content)

        assertEquals("Version phải là 1", 1, dataset.version)
        assertEquals("referenceTime phải đúng mốc quy chiếu", "2026-10-20T10:00:00+07:00", dataset.referenceTime)
        assertEquals("timezone phải là Asia/Ho_Chi_Minh", "Asia/Ho_Chi_Minh", dataset.timezone)
        assertEquals("Bộ câu mẫu phải có đúng 200 câu", 200, dataset.testCases.size)
        assertEquals("totalCases phải bằng 200", 200, dataset.totalCases)
        assertTrue("mockWallets phải có dữ liệu", dataset.mockWallets.isNotEmpty())

        val validWalletKeys = dataset.mockWallets.map { it.key }.toSet()
        val validTypes = listOf("EXPENSE", "INCOME", "TRANSFER")

        val idSet = mutableSetOf<String>()
        dataset.testCases.forEachIndexed { index, tc ->
            val expectedId = "tc_%03d".format(index + 1)
            assertEquals("ID phải theo thứ tự", expectedId, tc.id)
            assertTrue("ID không được trùng lặp: ${tc.id}", idSet.add(tc.id))
            assertTrue("Input không được để trống ở câu ${tc.id}", tc.input.isNotBlank())

            tc.expected.forEach { item ->
                assertTrue("Số tiền phải lớn hơn 0 ở câu ${tc.id}", item.amount > 0)
                assertTrue("Loại giao dịch phải hợp lệ ở câu ${tc.id}", item.type in validTypes)

                // Kiểm tra datetime chuẩn ISO-8601
                assertNotNull("Datetime phải hợp lệ ở câu ${tc.id}", OffsetDateTime.parse(item.datetime))

                // Kiểm tra khóa ví nằm trong mockWallets
                if (item.wallet != null) {
                    assertTrue("Ví ${item.wallet} phải có trong mockWallets", item.wallet in validWalletKeys)
                }

                // Kiểm tra nghiệp vụ chuyển khoản
                if (item.type == "TRANSFER") {
                    assertNull("categoryKey của TRANSFER phải là null ở câu ${tc.id}", item.categoryKey)
                    assertTrue("sourceWallet phải hợp lệ ở câu ${tc.id}", item.sourceWallet in validWalletKeys)
                    assertTrue("targetWallet phải hợp lệ ở câu ${tc.id}", item.targetWallet in validWalletKeys)
                }
            }
        }
    }
}
