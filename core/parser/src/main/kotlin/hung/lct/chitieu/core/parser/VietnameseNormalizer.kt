package hung.lct.chitieu.core.parser

import java.text.Normalizer
import java.util.Locale

object VietnameseNormalizer {

    private val vietnameseLocale = Locale.forLanguageTag("vi-VN")
    private val diacriticsRegex = "\\p{InCombiningDiacriticalMarks}+".toRegex()

    // Regex quét trực tiếp 4 loại token trên chuỗi gốc
    private val tokenRegex = Regex("""\d+(?:[.,]\d+)*|\p{L}+|\r?\n|[^\s\p{L}\d]""")

    /**
     * Bỏ dấu tiếng Việt, chuyển chữ 'đ'/'Đ' thành 'd'/'D'.
     */
    internal fun removeDiacritics(input: String): String {
        if (input.isBlank()) return input
        val withoutD = input.replace('đ', 'd').replace('Đ', 'D')
        val nfd = Normalizer.normalize(withoutD, Normalizer.Form.NFD)
        return diacriticsRegex.replace(nfd, "")
    }

    /**
     * Chuẩn hóa toàn bộ câu và cắt thành các Token.
     */
    fun normalize(rawInput: String): NormalizedSentence {
        // Bước 1: Chuẩn hóa NFC
        val nfcInput = Normalizer.normalize(rawInput, Normalizer.Form.NFC)
        if (nfcInput.isBlank()) {
            return NormalizedSentence(raw = nfcInput, text = "", unaccentedText = "", tokens = emptyList())
        }
        // Bước 2: Quét Regex và chuyển đổi thành List<Token>
        val tokens = tokenRegex.findAll(nfcInput).map { match ->
            val rawToken = match.value
            val tokenType = when {
                rawToken.startsWith("\n") || rawToken.startsWith("\r") -> TokenType.NEWLINE
                rawToken.first().isDigit() -> TokenType.NUMBER
                rawToken.first().isLetter() -> TokenType.WORD
                else -> TokenType.PUNCTUATION
            }
            val lowerText = rawToken.lowercase(vietnameseLocale)
            Token(
                text = lowerText,
                unaccented = removeDiacritics(lowerText),
                startIndex = match.range.first,
                endIndex = match.range.last + 1,
                type = tokenType,
            )
        }.toList()
        // Bước 3: Ghép text và unaccentedText từ danh sách tokens
        return NormalizedSentence(
            raw = nfcInput,
            text = tokens.joinToString(" ") { it.text },
            unaccentedText = tokens.joinToString(" ") { it.unaccented },
            tokens = tokens,
        )
    }
}
