package hung.lct.chitieu.core.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MoneyTest {

    @Test
    fun plus_twoAmounts_returnsSum() {
        val money1 = Money(10)
        val money2 = Money(20)
        assertEquals(Money(30), money1 + money2)
    }

    @Test
    fun minus_twoAmounts_returnsDifference() {
        val money1 = Money(10)
        val money2 = Money(20)
        assertEquals(Money(10), money2 - money1)
    }

    @Test
    fun unaryMinus_amount_returnsNegated() {
        val money = Money(10)
        assertEquals(Money(-10), -money)
    }

    @Test
    fun compareTo_variousAmounts_comparesCorrectly() {
        val money1 = Money(10)
        val money2 = Money(20)
        assertTrue(money1 < money2)
        assertTrue(money2 > money1)
        assertTrue(money1 <= money2)
        assertTrue(money2 >= money1)
    }

    @Test
    fun zero_hasZeroAmount() {
        val zero = Money.ZERO
        assertEquals(0L, zero.amount)
    }

    @Test
    fun sum_multipleAmounts_returnsSum() {
        val amounts = listOf(Money(10), Money(20), Money(30))
        assertEquals(Money(60), amounts.sum())

        val unaryAmounts = listOf(Money(10), -Money(20), Money(30))
        assertEquals(Money(20), unaryAmounts.sum())

        val zeroAmounts = listOf(-Money(20), Money(20))
        assertEquals(Money.ZERO, zeroAmounts.sum())

        val emptyAmounts = emptyList<Money>()
        assertEquals(Money.ZERO, emptyAmounts.sum())
    }
}
