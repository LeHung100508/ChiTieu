package hung.lct.chitieu.core.parser

/**
 * Kết quả bóc tách một số tiền từ câu nhập (FR-02).
 *
 * @property value Giá trị số tiền tính theo đơn vị đồng (VND).
 * @property startIndex Chỉ số ký tự bắt đầu trong chuỗi raw (NFC).
 * @property endIndex Chỉ số ký tự kết thúc trong chuỗi raw (NFC).
 * @property tokenRange Dải chỉ số các token trong danh sách tokens đã được tiêu thụ.
 * @property confidence Độ tin cậy từ 0.0 đến 1.0 (1.0 khi có đơn vị rõ ràng, 0.8 cho số trần không đơn vị).
 */
data class ParsedAmount(
    val value: Long,
    val startIndex: Int,
    val endIndex: Int,
    val tokenRange: IntRange,
    val confidence: Double = 1.0,
)
