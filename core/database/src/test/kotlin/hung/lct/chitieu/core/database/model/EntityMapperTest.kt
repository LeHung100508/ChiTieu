package hung.lct.chitieu.core.database.model

import hung.lct.chitieu.core.model.Category
import hung.lct.chitieu.core.model.CategoryType
import hung.lct.chitieu.core.model.Money
import hung.lct.chitieu.core.model.Wallet
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class EntityMapperTest {

    @Test
    fun wallet_roundTrip_matches() {
        val wallet = Wallet(
            id = 1L,
            name = "Tiền mặt",
            initialBalance = Money(500_000L),
            icon = "cash",
            color = 0xFF1B6B4AL,
            aliases = listOf("tien mat"),
        )
        val entity = wallet.asEntity()
        assertEquals(500_000L, entity.initialBalance)
        assertEquals(wallet, entity.asExternalModel())
    }

    @Test
    fun category_roundTrip_matches() {
        val category = Category(
            id = 2L,
            seedKey = "food_dining",
            type = CategoryType.EXPENSE,
            icon = "food",
            color = 0xFF1B6B4AL,
        )
        assertEquals(category, category.asEntity().asExternalModel())
    }

    @Test
    fun transaction_roundTrip_matches() {
        val now = java.time.Instant.parse("2026-11-25T10:00:00Z")
        val tx = hung.lct.chitieu.core.model.Transaction(
            id = 1L,
            type = hung.lct.chitieu.core.model.TransactionType.EXPENSE,
            amount = Money(45_000L),
            occurredAt = now,
            note = "Ăn phở",
            categoryId = 2L,
            walletId = 1L,
            source = hung.lct.chitieu.core.model.TransactionSource.TEXT,
            createdAt = now,
            updatedAt = now,
        )
        val entity = tx.asEntity()
        assertEquals(45_000L, entity.amount)
        assertEquals(tx, entity.asExternalModel())
    }

    @Test
    fun budget_roundTrip_matches() {
        val budget = hung.lct.chitieu.core.model.Budget(
            id = 1L,
            categoryId = null,
            amount = Money(5_000_000L),
        )
        assertEquals(budget, budget.asEntity().asExternalModel())
    }

    @Test
    fun keywordRule_roundTrip_matches() {
        val rule = hung.lct.chitieu.core.model.KeywordRule(
            id = 1L,
            keyword = "cà phê",
            categoryId = 10L,
            hitCount = 2,
        )
        assertEquals(rule, rule.asEntity().asExternalModel())
    }

    @Test
    fun transaction_transfer_roundTrip_matches() {
        val now = java.time.Instant.parse("2026-11-25T10:00:00Z")
        val tx = hung.lct.chitieu.core.model.Transaction(
            id = 2L,
            type = hung.lct.chitieu.core.model.TransactionType.TRANSFER,
            amount = Money(200_000L),
            occurredAt = now,
            note = "Rút tiền ATM",
            categoryId = null,
            walletId = 1L,
            toWalletId = 2L,
            source = hung.lct.chitieu.core.model.TransactionSource.TEXT,
            createdAt = now,
            updatedAt = now,
        )
        val entity = tx.asEntity()
        assertNull(entity.deletedAt)
        assertEquals(tx, entity.asExternalModel())
    }

    @Test
    fun category_custom_roundTrip_matches() {
        val category = Category(
            id = 3L,
            seedKey = null,
            name = "Ăn vặt",
            type = CategoryType.EXPENSE,
            icon = "snack",
            color = 0xFF1B6B4AL,
        )
        assertEquals(category, category.asEntity().asExternalModel())
    }

    @Test
    fun keywordRule_withWalletId_roundTrip_matches() {
        val rule = hung.lct.chitieu.core.model.KeywordRule(
            id = 2L,
            keyword = "đổ xăng",
            categoryId = 5L,
            walletId = 3L,
            hitCount = 3,
        )
        assertEquals(rule, rule.asEntity().asExternalModel())
    }
}
