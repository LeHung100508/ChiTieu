package hung.lct.chitieu.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4

@Entity(tableName = "transactions_fts")
@Fts4(tokenizer = "unicode61", contentEntity = TransactionEntity::class)
data class TransactionFtsEntity(
    val note: String?,
    @ColumnInfo(name = "raw_input")
    val rawInput: String?,
)
