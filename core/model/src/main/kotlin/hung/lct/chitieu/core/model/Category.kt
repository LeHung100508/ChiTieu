package hung.lct.chitieu.core.model

data class Category(
    val id: Long = 0L,
    val seedKey: String? = null,
    val name: String? = null,
    val type: CategoryType,
    val icon: String,
    val color: Long,
    val parentId: Long? = null,
    val isHidden: Boolean = false,
    val sortOrder: Int = 0,
) {
    init {
        require((!seedKey.isNullOrBlank()) xor (!name.isNullOrBlank())) {
            "Danh mục phải có đúng một trong hai: seedKey hoặc name"
        }
    }
}
