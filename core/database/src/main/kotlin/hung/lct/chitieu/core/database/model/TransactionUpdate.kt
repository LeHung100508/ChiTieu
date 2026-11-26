package hung.lct.chitieu.core.database.model

import androidx.room.ColumnInfo
import hung.lct.chitieu.core.model.TransactionType
import java.time.Instant

data class TransactionUpdate(
    val id: Long,
    val type: TransactionType,
    val amount: Long,
    @ColumnInfo(name = "occurred_at") val occurredAt: Instant,
    val note: String?,
    @ColumnInfo(name = "category_id") val categoryId: Long?,
    @ColumnInfo(name = "wallet_id") val walletId: Long,
    @ColumnInfo(name = "to_wallet_id") val toWalletId: Long?,
    @ColumnInfo(name = "updated_at") val updatedAt: Instant,
)
