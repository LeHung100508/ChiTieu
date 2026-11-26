package hung.lct.chitieu.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import hung.lct.chitieu.core.database.model.CategoryTotalRow
import hung.lct.chitieu.core.model.TransactionType
import kotlinx.coroutines.flow.Flow
import java.time.Instant

@Dao
interface TransactionStatsDao {

    @Query(
        """
        SELECT category_id, COALESCE(SUM(amount), 0) AS total FROM transactions
            WHERE deleted_at IS NULL AND type = :type AND occurred_at >= :startAt AND occurred_at < :endAt
            GROUP BY category_id ORDER BY total DESC
        """,
    )
    fun observeTotalByCategory(
        type: TransactionType,
        startAt: Instant,
        endAt: Instant,
    ): Flow<List<CategoryTotalRow>>

    @Query(
        """
        SELECT COALESCE(SUM(amount), 0) FROM transactions
            WHERE deleted_at IS NULL AND type = :type AND occurred_at >= :startAt AND occurred_at < :endAt
        """,
    )
    fun observeTotalByType(type: TransactionType, startAt: Instant, endAt: Instant): Flow<Long>
}
