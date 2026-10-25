package hung.lct.chitieu.core.parser

/**
 * Đại diện cho một từ hoặc ký hiệu sau khi tách từ.
 */
data class Token(
    val text: String,
    val unaccented: String,
    val startIndex: Int,
    val endIndex: Int,
    val type: TokenType = TokenType.WORD,
)

/**
 * Kết quả chuẩn hóa của một câu người dùng nhập.
 *
 * @property raw Câu đã chuẩn hóa về dạng Unicode NFC (dùng để trích xuất note theo vị trí token).
 * @property text Các token chữ thường nối với nhau bằng khoảng trắng.
 * @property unaccentedText Các token không dấu nối với nhau bằng khoảng trắng (dùng để so khớp từ khóa không dấu).
 * @property tokens Danh sách các token; vị trí startIndex và endIndex được tính trực tiếp trên [raw].
 */
data class NormalizedSentence(
    val raw: String,
    val text: String,
    val unaccentedText: String,
    val tokens: List<Token>,
)
