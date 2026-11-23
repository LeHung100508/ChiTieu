package hung.lct.chitieu.core.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test

class CategoryTest {

    @Test
    fun create_withBothSeedKeyAndName_throwsException() {
        assertThrows(IllegalArgumentException::class.java) {
            Category(
                seedKey = "food_dining",
                name = "Ăn vặt",
                type = CategoryType.EXPENSE,
                icon = "food",
                color = 0xFF1B6B4AL,
            )
        }
    }

    @Test
    fun create_withNeitherSeedKeyNorName_throwsException() {
        assertThrows(IllegalArgumentException::class.java) {
            Category(
                seedKey = null,
                name = null,
                type = CategoryType.EXPENSE,
                icon = "food",
                color = 0xFF1B6B4AL,
            )
        }
    }

    @Test
    fun create_withBlankName_throwsException() {
        assertThrows(IllegalArgumentException::class.java) {
            Category(
                seedKey = null,
                name = "   ",
                type = CategoryType.EXPENSE,
                icon = "food",
                color = 0xFF1B6B4AL,
            )
        }
    }

    @Test
    fun create_withSeedKeyOnly_succeeds() {
        val category = Category(
            seedKey = "food_dining",
            name = null,
            type = CategoryType.EXPENSE,
            icon = "food",
            color = 0xFF1B6B4AL,
        )
        assertEquals("food_dining", category.seedKey)
        assertNull(category.name)
    }

    @Test
    fun create_withNameOnly_succeeds() {
        val category = Category(
            seedKey = null,
            name = "Ăn vặt",
            type = CategoryType.EXPENSE,
            icon = "food",
            color = 0xFF1B6B4AL,
        )
        assertEquals("Ăn vặt", category.name)
        assertNull(category.seedKey)
    }

    @Test
    fun create_withBlankSeedKey_throwsException() {
        assertThrows(IllegalArgumentException::class.java) {
            Category(
                seedKey = "   ",
                name = null,
                type = CategoryType.EXPENSE,
                icon = "food",
                color = 0xFF1B6B4AL,
            )
        }
    }
}
