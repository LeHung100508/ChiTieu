package hung.lct.chitieu.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import hung.lct.chitieu.core.database.model.KeywordRuleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface KeywordRuleDao {

    @Query("SELECT * FROM keyword_rules ORDER BY hit_count DESC, id ASC")
    fun observeAll(): Flow<List<KeywordRuleEntity>>

    @Query("SELECT * FROM keyword_rules WHERE keyword = :keyword LIMIT 1")
    suspend fun findByKeyword(keyword: String): KeywordRuleEntity?

    @Query("SELECT * FROM keyword_rules WHERE hit_count >= :minHits ORDER BY hit_count DESC, id ASC")
    fun observeEffectiveRules(minHits: Int): Flow<List<KeywordRuleEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(rule: KeywordRuleEntity): Long

    @Update
    suspend fun update(rule: KeywordRuleEntity): Int

    @Query(
        """
        UPDATE keyword_rules
           SET hit_count = hit_count + 1, wallet_id = COALESCE(:walletId, wallet_id)
           WHERE keyword = :keyword
        """,
    )
    suspend fun incrementHitCount(keyword: String, walletId: Long?)

    @Transaction
    suspend fun upsertByKeyword(keyword: String, categoryId: Long, walletId: Long?) {
        val existing = findByKeyword(keyword)
        if (existing == null) {
            insert(KeywordRuleEntity(keyword = keyword, categoryId = categoryId, walletId = walletId, hitCount = 1))
            return
        }
        if (existing.categoryId == categoryId) {
            incrementHitCount(keyword, walletId)
            return
        }
        update(existing.copy(categoryId = categoryId, walletId = walletId, hitCount = 1))
    }

    @Query("DELETE FROM keyword_rules WHERE id = :id")
    suspend fun deleteById(id: Long)
}
