package hung.lct.chitieu.core.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CategoryTypeTest {

    @Test
    fun toTransactionType_mapsCorrectly() {
        assertEquals(TransactionType.EXPENSE, CategoryType.EXPENSE.toTransactionType())
        assertEquals(TransactionType.INCOME, CategoryType.INCOME.toTransactionType())
    }

    @Test
    fun toCategoryType_mapsCorrectly() {
        assertEquals(CategoryType.EXPENSE, TransactionType.EXPENSE.toCategoryType())
        assertEquals(CategoryType.INCOME, TransactionType.INCOME.toCategoryType())

        // Điểm mấu chốt: TRANSFER không thuộc danh mục nào nên phải ra null
        assertNull(TransactionType.TRANSFER.toCategoryType())
    }
}
