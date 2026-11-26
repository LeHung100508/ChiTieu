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

    @Test
    fun walletWithBalance_asExternalModel_mapsBalance() {
        val walletEntity = WalletEntity(
            id = 1L,
            name = "Ví chính",
            initialBalance = 0L,
            icon = "wallet",
            color = 0xFF1B6B4AL,
        )
        val row = WalletWithBalanceRow(
            wallet = walletEntity,
            currentBalance = -500_000L,
        )
        val result = row.asExternalModel()
        assertEquals(walletEntity.asExternalModel(), result.wallet)
        assertEquals(Money(-500_000L), result.balance)
    }

    @Test
    fun categoryWithChildren_asExternalModel_sortsAndMapsTree() {
        val parent = CategoryEntity(
            id = 1L,
            seedKey = "food_dining",
            type = CategoryType.EXPENSE,
            icon = "food",
            color = 0xFF1B6B4AL,
        )
        val child2 = CategoryEntity(
            id = 2L,
            name = "Cà phê",
            type = CategoryType.EXPENSE,
            icon = "coffee",
            color = 0xFF1B6B4AL,
            parentId = 1L,
            sortOrder = 2,
        )
        val child1 = CategoryEntity(
            id = 3L,
            name = "Ăn sáng",
            type = CategoryType.EXPENSE,
            icon = "breakfast",
            color = 0xFF1B6B4AL,
            parentId = 1L,
            sortOrder = 1,
        )
        val row = CategoryWithChildrenRow(
            category = parent,
            children = listOf(child2, child1),
        )
        val result = row.asExternalModel()
        assertEquals(parent.asExternalModel(), result.category)
        assertEquals(listOf(child1.asExternalModel(), child2.asExternalModel()), result.children)
    }

    @Test
    fun categoryTotalRow_asExternalModel_mapsMoney() {
        val row = CategoryTotalRow(categoryId = 1L, total = 150_000L)
        val result = row.asExternalModel()
        assertEquals(1L, result.categoryId)
        assertEquals(Money(150_000L), result.total)
    }
}
