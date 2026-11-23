package hung.lct.chitieu.core.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test
import java.time.Instant

class TransactionTest {

    private val now = Instant.parse("2026-11-22T10:00:00Z")

    @Test
    fun create_validExpense_succeeds() {
        val tx = Transaction(
            type = TransactionType.EXPENSE,
            amount = Money(50_000L),
            occurredAt = now,
            categoryId = 1L,
            walletId = 1L,
            source = TransactionSource.MANUAL,
            createdAt = now,
        )
        assertEquals(Money(50_000L), tx.amount)
        assertEquals(1L, tx.categoryId)
        assertNull(tx.toWalletId)
    }

    @Test
    fun create_validTransfer_succeeds() {
        val tx = Transaction(
            type = TransactionType.TRANSFER,
            amount = Money(200_000L),
            occurredAt = now,
            walletId = 1L,
            toWalletId = 2L,
            source = TransactionSource.MANUAL,
            createdAt = now,
        )
        assertEquals(Money(200_000L), tx.amount)
        assertNull(tx.categoryId)
        assertEquals(2L, tx.toWalletId)
    }

    @Test
    fun create_zeroAmount_throwsException() {
        assertThrows(IllegalArgumentException::class.java) {
            Transaction(
                type = TransactionType.EXPENSE,
                amount = Money.ZERO,
                occurredAt = now,
                walletId = 1L,
                source = TransactionSource.MANUAL,
                createdAt = now,
            )
        }
    }

    @Test
    fun create_transferWithoutDestinationWallet_throwsException() {
        assertThrows(IllegalArgumentException::class.java) {
            Transaction(
                type = TransactionType.TRANSFER,
                amount = Money(100_000L),
                occurredAt = now,
                walletId = 1L,
                toWalletId = null,
                source = TransactionSource.MANUAL,
                createdAt = now,
            )
        }
    }

    @Test
    fun create_transferToSameWallet_throwsException() {
        assertThrows(IllegalArgumentException::class.java) {
            Transaction(
                type = TransactionType.TRANSFER,
                amount = Money(100_000L),
                occurredAt = now,
                walletId = 1L,
                toWalletId = 1L,
                source = TransactionSource.MANUAL,
                createdAt = now,
            )
        }
    }

    @Test
    fun create_transferWithCategory_throwsException() {
        assertThrows(IllegalArgumentException::class.java) {
            Transaction(
                type = TransactionType.TRANSFER,
                amount = Money(100_000L),
                occurredAt = now,
                categoryId = 2L,
                walletId = 1L,
                toWalletId = 2L,
                source = TransactionSource.MANUAL,
                createdAt = now,
            )
        }
    }

    @Test
    fun create_expenseWithDestinationWallet_throwsException() {
        assertThrows(IllegalArgumentException::class.java) {
            Transaction(
                type = TransactionType.EXPENSE,
                amount = Money(50_000L),
                occurredAt = now,
                walletId = 1L,
                toWalletId = 2L,
                source = TransactionSource.MANUAL,
                createdAt = now,
            )
        }
    }

    @Test
    fun create_negativeAmount_throwsException() {
        assertThrows(IllegalArgumentException::class.java) {
            Transaction(
                type = TransactionType.EXPENSE,
                amount = Money(-50_000L), // Số tiền âm
                occurredAt = now,
                walletId = 1L,
                source = TransactionSource.MANUAL,
                createdAt = now,
            )
        }
    }
}
