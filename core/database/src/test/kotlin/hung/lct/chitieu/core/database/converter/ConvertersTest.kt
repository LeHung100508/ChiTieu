package hung.lct.chitieu.core.database.converter

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.Instant

class ConvertersTest {
    private val converters = Converters()

    @Test
    fun instantToEpochMillis_roundTrip_matches() {
        val now = Instant.parse("2026-11-23T10:00:00Z")
        val millis = converters.toEpochMillis(now)
        assertEquals(now, converters.fromEpochMillis(millis))
        assertNull(converters.toEpochMillis(null))
        assertNull(converters.fromEpochMillis(null))
    }

    @Test
    fun stringList_roundTrip_matches() {
        val list = listOf("vcb", "vietcombank", "ngân hàng vcb")
        val encoded = converters.fromStringList(list)
        assertEquals(list, converters.toStringList(encoded))
    }

    @Test
    fun stringList_emptyAndNull_returnsEmptyList() {
        val emptyEncoded = converters.fromStringList(emptyList())
        assertEquals(emptyList<String>(), converters.toStringList(emptyEncoded))
        assertEquals(emptyList<String>(), converters.toStringList(null))
        assertEquals(emptyList<String>(), converters.toStringList(""))
    }

    @Test
    fun stringList_specialCharacters_preservesExactValues() {
        val list = listOf("ví, tiền", " vcb ", "")
        val encoded = converters.fromStringList(list)
        assertEquals(list, converters.toStringList(encoded))
    }
}
