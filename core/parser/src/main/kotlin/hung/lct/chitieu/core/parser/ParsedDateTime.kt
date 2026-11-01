package hung.lct.chitieu.core.parser

import java.time.LocalDateTime

/**
 * Loại mốc thời gian được nhận diện.
 */
enum class DateTimeType {
    DATE_EXPLICIT,
    SESSION_WITH_MODIFIER,
    STANDALONE_SESSION,
    DEFAULT_CLOCK,
}

/**
 * Kết quả bóc tách một mốc thời gian từ câu nhập (FR-03).
 *
 * @property localDateTime Mốc thời gian đã nhận diện được.
 * @property startIndex Chỉ số ký tự bắt đầu trong chuỗi raw (hoặc -1 nếu là mặc định).
 * @property endIndex Chỉ số ký tự kết thúc trong chuỗi raw (hoặc -1 nếu là mặc định).
 * @property tokenRange Dải chỉ số các token đã tiêu thụ (hoặc null nếu là mặc định).
 * @property confidence Độ tin cậy từ 0.0 đến 1.0 (1.0 nếu người dùng chỉ định rõ, 0.5 nếu mặc định).
 * @property isDefault True nếu mốc thời gian là mặc định (không được người dùng nhắc trong câu).
 * @property type Phân loại mốc thời gian để hỗ trợ lan ngữ cảnh trong câu đa khoản (FR-07).
 */
data class ParsedDateTime(
    val localDateTime: LocalDateTime,
    val startIndex: Int,
    val endIndex: Int,
    val tokenRange: IntRange?,
    val confidence: Double = 1.0,
    val isDefault: Boolean = false,
    val type: DateTimeType = DateTimeType.DATE_EXPLICIT,
)
