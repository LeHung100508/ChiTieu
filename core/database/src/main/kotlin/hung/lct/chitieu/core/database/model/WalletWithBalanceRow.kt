package hung.lct.chitieu.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import hung.lct.chitieu.core.model.Money
import hung.lct.chitieu.core.model.WalletWithBalance

data class WalletWithBalanceRow(
    @Embedded
    val wallet: WalletEntity,
    @ColumnInfo(name = "current_balance")
    val currentBalance: Long,
)

fun WalletWithBalanceRow.asExternalModel(): WalletWithBalance = WalletWithBalance(
    wallet = wallet.asExternalModel(),
    balance = Money(currentBalance),
)
