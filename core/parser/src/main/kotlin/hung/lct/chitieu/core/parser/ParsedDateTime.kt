package hung.lct.chitieu.core.parser

import java.time.LocalDateTime

/**
 * Kết quả bóc tách một mốc thời gian từ câu nhập.
 *
 * @property localDateTime Mốc thời gian đã nhận diện được.
 * @property startIndex Chỉ số ký tự bắt đầu trong chuỗi raw (NFC).
 * @property endIndex Chỉ số ký tự kết thúc trong chuỗi raw (NFC).
 * @property tokenRange Dải chỉ số các token trong danh sách tokens đã được tiêu thụ.
 */
data class ParsedDateTime(
    val localDateTime: LocalDateTime,
    val startIndex: Int,
    val endIndex: Int,
    val tokenRange: IntRange,
)
