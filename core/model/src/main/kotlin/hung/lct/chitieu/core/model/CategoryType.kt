package hung.lct.chitieu.core.model

enum class CategoryType {
    EXPENSE,
    INCOME,
}

fun CategoryType.toTransactionType(): TransactionType = when (this) {
    CategoryType.EXPENSE -> TransactionType.EXPENSE
    CategoryType.INCOME -> TransactionType.INCOME
}

fun TransactionType.toCategoryType(): CategoryType? = when (this) {
    TransactionType.EXPENSE -> CategoryType.EXPENSE
    TransactionType.INCOME -> CategoryType.INCOME
    TransactionType.TRANSFER -> null
}
