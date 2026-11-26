package hung.lct.chitieu.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import hung.lct.chitieu.core.database.model.WalletEntity
import hung.lct.chitieu.core.database.model.WalletWithBalanceRow
import kotlinx.coroutines.flow.Flow

@Dao
interface WalletDao {

    @Query(
        """
        SELECT w.*,
            (w.initial_balance
             + COALESCE((SELECT SUM(t.amount) FROM transactions t WHERE t.wallet_id = w.id AND t.type = 'INCOME' AND t.deleted_at IS NULL), 0)
             - COALESCE((SELECT SUM(t.amount) FROM transactions t WHERE t.wallet_id = w.id AND t.type = 'EXPENSE' AND t.deleted_at IS NULL), 0)
             + COALESCE((SELECT SUM(t.amount) FROM transactions t WHERE t.to_wallet_id = w.id AND t.type = 'TRANSFER' AND t.deleted_at IS NULL), 0)
             - COALESCE((SELECT SUM(t.amount) FROM transactions t WHERE t.wallet_id = w.id AND t.type = 'TRANSFER' AND t.deleted_at IS NULL), 0)
            ) AS current_balance
        FROM wallets w
        WHERE (:includeArchived = 1 OR w.is_archived = 0)
        ORDER BY w.id ASC
        """,
    )
    fun observeWalletsWithBalance(includeArchived: Boolean = false): Flow<List<WalletWithBalanceRow>>

    @Query("SELECT * FROM wallets WHERE id = :id")
    fun observeWalletById(id: Long): Flow<WalletEntity?>

    @Upsert
    suspend fun upsert(wallet: WalletEntity): Long

    @Query("UPDATE wallets SET is_archived = :archived WHERE id = :id")
    suspend fun setArchived(id: Long, archived: Boolean)
}
