package hung.lct.chitieu.core.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class KeywordRuleTest {

    @Test
    fun create_validRule_succeeds() {
        val rule = KeywordRule(
            keyword = "cà phê",
            categoryId = 1L,
        )
        assertEquals("cà phê", rule.keyword)
    }

    @Test
    fun create_blankKeyword_throwsException() {
        assertThrows(IllegalArgumentException::class.java) {
            KeywordRule(
                keyword = "   ",
                categoryId = 1L,
            )
        }
    }

    @Test
    fun create_hitCountLessThanOne_throwsException() {
        assertThrows(IllegalArgumentException::class.java) {
            KeywordRule(
                keyword = "cà phê",
                categoryId = 1L,
                hitCount = 0,
            )
        }
    }
}
