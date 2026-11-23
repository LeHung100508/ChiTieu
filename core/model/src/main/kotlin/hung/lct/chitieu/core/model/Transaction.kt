package hung.lct.chitieu.core.model

import java.time.Instant

data class Transaction(
    val id: Long = 0L,
    val type: TransactionType,
    val amount: Money,
    val occurredAt: Instant,
    val note: String? = null,
    val categoryId: Long? = null,
    val walletId: Long,
    val toWalletId: Long? = null,
    val rawInput: String? = null,
    val source: TransactionSource,
    val createdAt: Instant,
    val updatedAt: Instant = createdAt,
) {
    init {
        require(amount > Money.ZERO) {
            "Số tiền giao dịch phải lớn hơn 0"
        }
        if (type == TransactionType.TRANSFER) {
            require(toWalletId != null) {
                "Giao dịch chuyển khoản phải có ví đích"
            }
            require(walletId != toWalletId) {
                "Ví nguồn và ví đích không được trùng nhau"
            }
            require(categoryId == null) {
                "Giao dịch chuyển khoản không được có danh mục"
            }
        } else {
            require(toWalletId == null) {
                "Giao dịch thu/chi không được có ví đích"
            }
        }
    }
}
