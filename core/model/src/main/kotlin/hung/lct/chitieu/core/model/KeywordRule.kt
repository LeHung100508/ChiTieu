package hung.lct.chitieu.core.model

data class KeywordRule(
    val id: Long = 0L,
    val keyword: String,
    val categoryId: Long,
    val walletId: Long? = null,
    val hitCount: Int = 1,
) {
    init {
        require(keyword.isNotBlank()) { "Keyword không được để trống" }
        require(hitCount >= 1) { "hitCount phải >= 1" }
    }
}
