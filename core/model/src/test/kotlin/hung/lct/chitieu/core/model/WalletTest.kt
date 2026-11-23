package hung.lct.chitieu.core.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class WalletTest {

    @Test
    fun create_withValidName_succeeds() {
        val wallet = Wallet(
            name = "Tiền mặt",
            icon = "cash",
            color = 0xFF1B6B4AL,
        )
        assertEquals("Tiền mặt", wallet.name)
    }

    @Test
    fun create_withEmptyName_throwsException() {
        assertThrows(IllegalArgumentException::class.java) {
            Wallet(
                name = "",
                icon = "cash",
                color = 0xFF1B6B4AL,
            )
        }
    }

    @Test
    fun create_withBlankName_throwsException() {
        assertThrows(IllegalArgumentException::class.java) {
            Wallet(
                name = "   ",
                icon = "cash",
                color = 0xFF1B6B4AL,
            )
        }
    }
}
