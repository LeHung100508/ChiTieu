package hung.lct.chitieu.core.parser

/**
 * Kết quả phân loại danh mục từ câu hoặc mệnh đề (FR-04).
 *
 * @property categoryKey Khóa danh mục chuẩn (seedKey), ví dụ "food_dining", "transportation", hoặc "other_expense".
 * @property confidence Độ tin cậy từ 0.0 đến 1.0 (1.0 khi khớp từ khóa trong từ điển, 0.0 khi fallback).
 * @property matchedKeyword Từ khóa đã khớp trong từ điển, hoặc null nếu fallback.
 * @property tokenRange Dải chỉ số token đã khớp trong danh sách token, hoặc null nếu fallback.
 */
data class ParsedCategory(
    val categoryKey: String,
    val confidence: Double,
    val matchedKeyword: String? = null,
    val tokenRange: IntRange? = null,
)
