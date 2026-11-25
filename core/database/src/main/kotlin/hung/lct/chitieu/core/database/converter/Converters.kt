package hung.lct.chitieu.core.database.converter

import androidx.room.TypeConverter
import kotlinx.serialization.json.Json
import java.time.Instant

class Converters {

    @TypeConverter
    fun fromEpochMillis(value: Long?): Instant? = value?.let(Instant::ofEpochMilli)

    @TypeConverter
    fun toEpochMillis(instant: Instant?): Long? = instant?.toEpochMilli()

    @TypeConverter
    fun fromStringList(list: List<String>?): String = Json.encodeToString(list ?: emptyList())

    @TypeConverter
    fun toStringList(value: String?): List<String> {
        if (value.isNullOrBlank()) return emptyList()
        return Json.decodeFromString(value)
    }
}
