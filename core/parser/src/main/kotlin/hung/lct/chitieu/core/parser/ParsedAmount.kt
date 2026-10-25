package hung.lct.chitieu.core.parser

/**
 * Kết quả bóc tách một số tiền từ câu nhập.
 *
 * @property value Giá trị số tiền tính theo đơn vị đồng (VND).
 * @property startIndex Chỉ số ký tự bắt đầu trong chuỗi raw (NFC).
 * @property endIndex Chỉ số ký tự kết thúc trong chuỗi raw (NFC).
 * @property tokenRange Dải chỉ số các token trong danh sách tokens đã được tiêu thụ.
 */
data class ParsedAmount(
    val value: Long,
    val startIndex: Int,
    val endIndex: Int,
    val tokenRange: IntRange,
)
