package hung.lct.chitieu.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import hung.lct.chitieu.core.database.model.BudgetEntity
import hung.lct.chitieu.core.model.BudgetPeriod
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {

    @Query("SELECT * FROM budgets ORDER BY category_id ASC")
    fun observeAll(): Flow<List<BudgetEntity>>

    @Query("SELECT * FROM budgets WHERE category_id IS NULL AND period = :period LIMIT 1")
    fun observeTotalBudget(period: BudgetPeriod = BudgetPeriod.MONTHLY): Flow<BudgetEntity?>

    @Query("SELECT * FROM budgets WHERE category_id IS NULL AND period = :period LIMIT 1")
    suspend fun getTotalBudget(period: BudgetPeriod): BudgetEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(budget: BudgetEntity): Long

    @Update
    suspend fun update(budget: BudgetEntity)

    @Transaction
    suspend fun upsertTotalBudget(amount: Long, period: BudgetPeriod = BudgetPeriod.MONTHLY) {
        val totalBudget = getTotalBudget(period)
        if (totalBudget != null) {
            update(totalBudget.copy(amount = amount))
        } else {
            insert(BudgetEntity(categoryId = null, amount = amount, period = period))
        }
    }

    @Query("SELECT * FROM budgets WHERE category_id = :categoryId AND period = :period LIMIT 1")
    suspend fun getCategoryBudget(categoryId: Long, period: BudgetPeriod): BudgetEntity?

    @Transaction
    suspend fun upsertCategoryBudget(
        categoryId: Long,
        amount: Long,
        period: BudgetPeriod = BudgetPeriod.MONTHLY,
    ) {
        val existing = getCategoryBudget(categoryId, period)
        if (existing != null) {
            update(existing.copy(amount = amount))
        } else {
            insert(BudgetEntity(categoryId = categoryId, amount = amount, period = period))
        }
    }

    @Query("DELETE FROM budgets WHERE id = :id")
    suspend fun deleteById(id: Long)
}
