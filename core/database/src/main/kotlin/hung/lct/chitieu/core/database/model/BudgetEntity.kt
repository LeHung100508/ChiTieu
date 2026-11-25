package hung.lct.chitieu.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import hung.lct.chitieu.core.model.Budget
import hung.lct.chitieu.core.model.BudgetPeriod
import hung.lct.chitieu.core.model.Money

@Entity(
    tableName = "budgets",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["category_id", "period"], unique = true),
    ],
)
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    @ColumnInfo(name = "category_id")
    val categoryId: Long? = null,
    val amount: Long,
    val period: BudgetPeriod = BudgetPeriod.MONTHLY,
)

fun BudgetEntity.asExternalModel(): Budget = Budget(
    id = id,
    categoryId = categoryId,
    amount = Money(amount),
    period = period,
)

fun Budget.asEntity(): BudgetEntity = BudgetEntity(
    id = id,
    categoryId = categoryId,
    amount = amount.amount,
    period = period,
)
