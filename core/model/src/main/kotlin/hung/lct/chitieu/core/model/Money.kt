package hung.lct.chitieu.core.model

@JvmInline
value class Money(val amount: Long) : Comparable<Money> {

    override fun compareTo(other: Money): Int = amount.compareTo(other.amount)

    operator fun plus(other: Money): Money = Money(amount + other.amount)

    operator fun minus(other: Money): Money = Money(amount - other.amount)

    operator fun unaryMinus(): Money = Money(-amount)

    companion object {
        val ZERO: Money = Money(0L)
    }
}

fun Iterable<Money>.sum(): Money = Money(this.sumOf { it.amount })
