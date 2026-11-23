package hung.lct.chitieu.core.model

data class Wallet(
    val id: Long = 0L,
    val name: String,
    val initialBalance: Money = Money.ZERO,
    val icon: String,
    val color: Long,
    val isArchived: Boolean = false,
    val aliases: List<String> = emptyList(),
) {
    init {
        require(name.isNotBlank()) {
            "Tên không được để trống hoặc khoảng trắng"
        }
    }
}
