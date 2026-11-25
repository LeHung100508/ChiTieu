package hung.lct.chitieu.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import hung.lct.chitieu.core.model.Money
import hung.lct.chitieu.core.model.Wallet

@Entity(tableName = "wallets")
data class WalletEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    @ColumnInfo(name = "initial_balance")
    val initialBalance: Long,
    val icon: String,
    val color: Long,
    @ColumnInfo(name = "is_archived")
    val isArchived: Boolean = false,
    val aliases: List<String> = emptyList(),
)

fun WalletEntity.asExternalModel(): Wallet = Wallet(
    id = id,
    name = name,
    initialBalance = Money(initialBalance),
    icon = icon,
    color = color,
    isArchived = isArchived,
    aliases = aliases,
)

fun Wallet.asEntity(): WalletEntity = WalletEntity(
    id = id,
    name = name,
    initialBalance = initialBalance.amount,
    icon = icon,
    color = color,
    isArchived = isArchived,
    aliases = aliases,
)
