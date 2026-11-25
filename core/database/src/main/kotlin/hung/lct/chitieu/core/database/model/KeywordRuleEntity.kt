package hung.lct.chitieu.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import hung.lct.chitieu.core.model.KeywordRule

@Entity(
    tableName = "keyword_rules",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = WalletEntity::class,
            parentColumns = ["id"],
            childColumns = ["wallet_id"],
            onDelete = ForeignKey.SET_NULL,
        ),
    ],
    indices = [
        Index(value = ["keyword"], unique = true),
        Index(value = ["category_id"]),
        Index(value = ["wallet_id"]),
    ],
)
data class KeywordRuleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val keyword: String,
    @ColumnInfo(name = "category_id")
    val categoryId: Long,
    @ColumnInfo(name = "wallet_id")
    val walletId: Long? = null,
    @ColumnInfo(name = "hit_count")
    val hitCount: Int = 1,
)

fun KeywordRuleEntity.asExternalModel(): KeywordRule = KeywordRule(
    id = id,
    keyword = keyword,
    categoryId = categoryId,
    walletId = walletId,
    hitCount = hitCount,
)

fun KeywordRule.asEntity(): KeywordRuleEntity = KeywordRuleEntity(
    id = id,
    keyword = keyword,
    categoryId = categoryId,
    walletId = walletId,
    hitCount = hitCount,
)
