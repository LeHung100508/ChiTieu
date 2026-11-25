package hung.lct.chitieu.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import hung.lct.chitieu.core.model.Category
import hung.lct.chitieu.core.model.CategoryType

@Entity(
    tableName = "categories",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["parent_id"],
            onDelete = ForeignKey.RESTRICT,
        ),
    ],
    indices = [
        Index(value = ["seed_key"], unique = true),
        Index(value = ["parent_id"]),
    ],
)
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    @ColumnInfo(name = "seed_key")
    val seedKey: String? = null,
    val name: String? = null,
    val type: CategoryType, // Room mặc định hỗ trợ enum (lưu String tên enum)
    val icon: String,
    val color: Long,
    @ColumnInfo(name = "parent_id")
    val parentId: Long? = null,
    @ColumnInfo(name = "is_hidden")
    val isHidden: Boolean = false,
    @ColumnInfo(name = "sort_order")
    val sortOrder: Int = 0,
)

fun CategoryEntity.asExternalModel(): Category = Category(
    id = id,
    seedKey = seedKey,
    name = name,
    type = type,
    icon = icon,
    color = color,
    parentId = parentId,
    isHidden = isHidden,
    sortOrder = sortOrder,
)

fun Category.asEntity(): CategoryEntity = CategoryEntity(
    id = id,
    seedKey = seedKey,
    name = name,
    type = type,
    icon = icon,
    color = color,
    parentId = parentId,
    isHidden = isHidden,
    sortOrder = sortOrder,
)
