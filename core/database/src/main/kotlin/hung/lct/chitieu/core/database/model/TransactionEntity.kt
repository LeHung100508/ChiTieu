package hung.lct.chitieu.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import hung.lct.chitieu.core.model.Money
import hung.lct.chitieu.core.model.Transaction
import hung.lct.chitieu.core.model.TransactionSource
import hung.lct.chitieu.core.model.TransactionType
import java.time.Instant

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = WalletEntity::class,
            parentColumns = ["id"],
            childColumns = ["wallet_id"],
            onDelete = ForeignKey.RESTRICT,
        ),
        ForeignKey(
            entity = WalletEntity::class,
            parentColumns = ["id"],
            childColumns = ["to_wallet_id"],
            onDelete = ForeignKey.RESTRICT,
        ),
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.RESTRICT,
        ),
    ],
    indices = [
        Index(value = ["wallet_id", "occurred_at"]),
        Index(value = ["occurred_at"]),
        Index(value = ["category_id", "occurred_at"]),
        Index(value = ["to_wallet_id"]),
    ],
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val type: TransactionType,
    val amount: Long,
    @ColumnInfo(name = "occurred_at")
    val occurredAt: Instant,
    val note: String? = null,
    @ColumnInfo(name = "category_id")
    val categoryId: Long? = null,
    @ColumnInfo(name = "wallet_id")
    val walletId: Long,
    @ColumnInfo(name = "to_wallet_id")
    val toWalletId: Long? = null,
    @ColumnInfo(name = "raw_input")
    val rawInput: String? = null,
    val source: TransactionSource,
    @ColumnInfo(name = "created_at")
    val createdAt: Instant,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Instant,
    @ColumnInfo("deleted_at")
    val deletedAt: Instant? = null,
)

fun TransactionEntity.asExternalModel(): Transaction = Transaction(
    id = id,
    type = type,
    amount = Money(amount),
    occurredAt = occurredAt,
    note = note,
    categoryId = categoryId,
    walletId = walletId,
    toWalletId = toWalletId,
    rawInput = rawInput,
    source = source,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun Transaction.asEntity(): TransactionEntity = TransactionEntity(
    id = id,
    type = type,
    amount = amount.amount,
    occurredAt = occurredAt,
    note = note,
    categoryId = categoryId,
    walletId = walletId,
    toWalletId = toWalletId,
    rawInput = rawInput,
    source = source,
    createdAt = createdAt,
    updatedAt = updatedAt,
    deletedAt = null,
)
