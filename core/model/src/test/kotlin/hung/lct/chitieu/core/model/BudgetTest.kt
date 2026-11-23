package hung.lct.chitieu.core.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test

class BudgetTest {

    @Test
    fun create_totalBudget_succeeds() {
        val budget = Budget(
            categoryId = null,
            amount = Money(5_000_000L),
        )
        assertNull(budget.categoryId)
        assertEquals(Money(5_000_000L), budget.amount)
    }

    @Test
    fun create_categoryBudget_succeeds() {
        val budget = Budget(
            categoryId = 10L,
            amount = Money(2_000_000L),
        )
        assertEquals(10L, budget.categoryId)
    }

    @Test
    fun create_zeroAmount_throwsException() {
        assertThrows(IllegalArgumentException::class.java) {
            Budget(
                amount = Money.ZERO,
            )
        }
    }

    @Test
    fun create_negativeAmount_throwsException() {
        assertThrows(IllegalArgumentException::class.java) {
            Budget(
                amount = Money(-1_000_000L), // Ngân sách âm
            )
        }
    }
}
