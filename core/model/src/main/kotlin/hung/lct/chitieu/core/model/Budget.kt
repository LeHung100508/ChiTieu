package hung.lct.chitieu.core.model

data class Budget(
    val id: Long = 0L,
    val categoryId: Long? = null,
    val amount: Money,
    val period: BudgetPeriod = BudgetPeriod.MONTHLY,
) {
    init {
        require(amount > Money.ZERO) {
            "Hạn mức ngân sách phải lớn hơn 0"
        }
    }
}
