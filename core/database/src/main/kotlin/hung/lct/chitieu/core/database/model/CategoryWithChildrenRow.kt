package hung.lct.chitieu.core.database.model

import androidx.room.Embedded
import androidx.room.Relation
import hung.lct.chitieu.core.model.CategoryNode

data class CategoryWithChildrenRow(
    @Embedded
    val category: CategoryEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "parent_id",
    )
    val children: List<CategoryEntity>,
)

fun CategoryWithChildrenRow.asExternalModel(): CategoryNode = CategoryNode(
    category = category.asExternalModel(),
    children = children.sortedWith(compareBy(CategoryEntity::sortOrder, CategoryEntity::id))
        .map { it.asExternalModel() },
)
