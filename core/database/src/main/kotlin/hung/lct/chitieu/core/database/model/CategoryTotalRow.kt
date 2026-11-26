package hung.lct.chitieu.core.database.model

import androidx.room.ColumnInfo
import hung.lct.chitieu.core.model.CategoryTotal
import hung.lct.chitieu.core.model.Money

data class CategoryTotalRow(
    @ColumnInfo(name = "category_id")
    val categoryId: Long?,
    val total: Long,
)

fun CategoryTotalRow.asExternalModel(): CategoryTotal = CategoryTotal(
    categoryId = categoryId,
    total = Money(total),
)
