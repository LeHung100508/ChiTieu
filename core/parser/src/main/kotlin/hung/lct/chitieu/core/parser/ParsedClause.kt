package hung.lct.chitieu.core.parser

/**
 * Đại diện cho một giao dịch nháp được bóc tách từ một mệnh đề của câu nhập (FR-01, FR-07).
 *
 * @property amount Thông tin số tiền được trích xuất (FR-02).
 * @property dateTime Mốc thời gian được xác định cho mệnh đề này (FR-03).
 * @property category Danh mục được phân loại cùng điểm tin cậy (FR-04).
 * @property note Nội dung mô tả / tên món chi tiêu (hoặc null nếu không có mô tả).
 * @property tokenRange Dải chỉ số token của mệnh đề trong danh sách token ban đầu của cả câu.
 */
data class ParsedClause(
    val amount: ParsedAmount,
    val dateTime: ParsedDateTime,
    val category: ParsedCategory,
    val note: String?,
    val tokenRange: IntRange,
)
