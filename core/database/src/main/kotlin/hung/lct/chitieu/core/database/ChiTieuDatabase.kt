package hung.lct.chitieu.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import hung.lct.chitieu.core.database.converter.Converters
import hung.lct.chitieu.core.database.model.BudgetEntity
import hung.lct.chitieu.core.database.model.CategoryEntity
import hung.lct.chitieu.core.database.model.KeywordRuleEntity
import hung.lct.chitieu.core.database.model.TransactionEntity
import hung.lct.chitieu.core.database.model.TransactionFtsEntity
import hung.lct.chitieu.core.database.model.WalletEntity

@Database(
    entities = [
        WalletEntity::class, CategoryEntity::class, TransactionEntity::class,
        TransactionFtsEntity::class, BudgetEntity::class, KeywordRuleEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class ChiTieuDatabase : RoomDatabase()
