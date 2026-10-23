package hung.lct.chitieu.core.designsystem.component

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Locale

class AmountTextTest {
    private val viLocale = Locale.forLanguageTag("vi-VN")
    private val usLocale = Locale.US

    @Test
    fun formatVnd_zeroAmount_returnsZeroVnd() {
        assertEquals("0 ₫", formatVnd(0L, locale = viLocale))
        assertEquals("0 ₫", formatVnd(0L, locale = usLocale))
    }

    @Test
    fun formatVnd_positiveAmounts_formatsWithCorrectSeparators() {
        assertEquals("45.000 ₫", formatVnd(45000L, locale = viLocale))
        assertEquals("1.250.000 ₫", formatVnd(1250000L, locale = viLocale))
        assertEquals("1,250,000 ₫", formatVnd(1250000L, locale = usLocale))
    }

    @Test
    fun formatVnd_negativeAmount_formatsAbsoluteValue() {
        assertEquals("45.000 ₫", formatVnd(-45000L, locale = viLocale))
        assertEquals("1.250.000 ₫", formatVnd(-1250000L, locale = viLocale))
    }

    @Test
    fun formatVnd_withPrefix_formatsCorrectPrefixPerAmountType() {
        // INCOME có tiền tố '+'
        assertEquals(
            "+45.000 ₫",
            formatVnd(45000L, includePrefix = true, type = AmountType.INCOME, locale = viLocale),
        )
        // EXPENSE có tiền tố '−' (U+2212)
        assertEquals(
            "−45.000 ₫",
            formatVnd(45000L, includePrefix = true, type = AmountType.EXPENSE, locale = viLocale),
        )
        // TRANSFER không có tiền tố
        assertEquals(
            "45.000 ₫",
            formatVnd(45000L, includePrefix = true, type = AmountType.TRANSFER, locale = viLocale),
        )
    }
}
