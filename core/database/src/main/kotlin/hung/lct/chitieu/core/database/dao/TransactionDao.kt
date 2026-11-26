package hung.lct.chitieu.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import hung.lct.chitieu.core.database.model.TransactionEntity
import hung.lct.chitieu.core.database.model.TransactionUpdate
import kotlinx.coroutines.flow.Flow
import java.time.Instant

@Dao
interface TransactionDao {

    @Query(
        """
        SELECT * FROM transactions
            WHERE deleted_at IS NULL AND occurred_at >= :startAt AND occurred_at < :endAt
            ORDER BY occurred_at DESC, id DESC
        """,
    )
    fun observeByDateRange(startAt: Instant, endAt: Instant): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE deleted_at IS NULL ORDER BY occurred_at DESC, id DESC LIMIT :limit")
    fun observeRecent(limit: Int): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE id = :id AND deleted_at IS NULL")
    fun observeById(id: Long): Flow<TransactionEntity?>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(transaction: TransactionEntity): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(transactions: List<TransactionEntity>): List<Long>

    @Update(entity = TransactionEntity::class)
    suspend fun update(update: TransactionUpdate): Int

    @Query("UPDATE transactions SET deleted_at = :deletedAt WHERE id = :id AND deleted_at IS NULL")
    suspend fun markDeleted(id: Long, deletedAt: Instant)

    @Query("UPDATE transactions SET deleted_at = NULL WHERE id = :id")
    suspend fun restore(id: Long)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun hardDeleteById(id: Long)

    @Query("DELETE FROM transactions WHERE deleted_at IS NOT NULL AND deleted_at <= :cutoff")
    suspend fun purgeDeletedBefore(cutoff: Instant)
}
