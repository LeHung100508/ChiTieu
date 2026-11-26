package hung.lct.chitieu.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import hung.lct.chitieu.core.database.model.CategoryEntity
import hung.lct.chitieu.core.database.model.CategoryWithChildrenRow
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Query("SELECT * FROM categories WHERE is_hidden = 0 ORDER BY sort_order ASC, id ASC")
    fun observeActiveCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories ORDER BY sort_order ASC, id ASC")
    fun observeAllCategories(): Flow<List<CategoryEntity>>

    @Transaction
    @Query("SELECT * FROM categories WHERE parent_id IS NULL ORDER BY sort_order ASC, id ASC")
    fun observeCategoryTree(): Flow<List<CategoryWithChildrenRow>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllIfAbsent(categories: List<CategoryEntity>): List<Long>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(category: CategoryEntity): Long

    @Update
    suspend fun update(category: CategoryEntity): Int

    @Query("UPDATE categories SET is_hidden = :hidden WHERE id = :id")
    suspend fun setHidden(id: Long, hidden: Boolean)

    @Query("SELECT EXISTS(SELECT 1 FROM transactions WHERE category_id = :categoryId)")
    suspend fun hasTransactions(categoryId: Long): Boolean

    @Query("DELETE FROM categories WHERE id = :id")
    suspend fun deleteById(id: Long)
}
