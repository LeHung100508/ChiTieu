package hung.lct.chitieu.core.parser

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

private const val FULL_CONFIDENCE = 1.0
private const val ACCENTLESS_CONFIDENCE = 0.8
private const val ZERO_CONFIDENCE = 0.0
private const val FALLBACK_CATEGORY = "other_expense"
private val WHITESPACE_REGEX = Regex("\\s+")

/**
 * Các từ đơn không dấu dễ xung đột ngữ cảnh trong tiếng Việt (ví dụ: cho/chợ, be/bé, ca/cá).
 * Các từ này chỉ được chấp nhận phân loại khi khớp chính xác có dấu.
 */
private val sensitiveSingleWords = setOf("cho", "be", "ca", "tao", "quan", "dep", "nho", "oc")

@Serializable
private data class CategoryDictionary(
    val version: Int = 1,
    val categories: Map<String, List<String>> = emptyMap(),
)

private data class ParserKeywordRule(
    val words: List<String>,
    val unaccentedWords: List<String>,
    val categoryKey: String,
)

/**
 * Bộ phân loại danh mục chi tiêu/thu nhập từ từ khóa (FR-04).
 */
object CategoryClassifier {

    private val json = Json { ignoreUnknownKeys = true }

    private val defaultRules: List<ParserKeywordRule> by lazy {
        val stream = javaClass.classLoader.getResourceAsStream("category_keywords.json")
            ?: error("Không tìm thấy file category_keywords.json trong resources")
        val text = stream.bufferedReader().use { it.readText() }
        val dictionary = json.decodeFromString<CategoryDictionary>(text)
        val rules = mutableListOf<ParserKeywordRule>()
        for ((catKey, phrases) in dictionary.categories) {
            for (phrase in phrases) {
                val words = phrase.trim().lowercase().split(WHITESPACE_REGEX).filter { it.isNotEmpty() }
                if (words.isNotEmpty()) {
                    val unaccented = words.map { VietnameseNormalizer.removeDiacritics(it) }
                    rules.add(ParserKeywordRule(words = words, unaccentedWords = unaccented, categoryKey = catKey))
                }
            }
        }
        sortRules(rules)
    }

    /**
     * Phân loại danh mục cho một danh sách token.
     *
     * Ưu tiên từ điển cá nhân trước, sau đó tới từ điển mặc định (ưu tiên cụm dài nhất).
     */
    fun classify(
        tokens: List<Token>,
        userKeywords: Map<String, String> = emptyMap(),
    ): ParsedCategory {
        val userRules = if (userKeywords.isNotEmpty()) {
            val rules = mutableListOf<ParserKeywordRule>()
            for ((phrase, catKey) in userKeywords) {
                val words = phrase.trim().lowercase().split(WHITESPACE_REGEX).filter { it.isNotEmpty() }
                if (words.isNotEmpty()) {
                    val unaccented = words.map { VietnameseNormalizer.removeDiacritics(it) }
                    rules.add(ParserKeywordRule(words = words, unaccentedWords = unaccented, categoryKey = catKey))
                }
            }
            sortRules(rules)
        } else {
            emptyList()
        }

        val allRules = userRules + defaultRules
        val accentedMatch = findFirstAccentedMatch(tokens, allRules)
        if (accentedMatch != null) return accentedMatch

        val unaccentedMatch = findFirstUnaccentedMatch(tokens, allRules)
        return unaccentedMatch ?: ParsedCategory(
            categoryKey = FALLBACK_CATEGORY,
            confidence = ZERO_CONFIDENCE,
            matchedKeyword = null,
            tokenRange = null,
        )
    }

    private fun sortRules(rules: List<ParserKeywordRule>): List<ParserKeywordRule> = rules.sortedWith(
        compareByDescending<ParserKeywordRule> { it.words.size }
            .thenByDescending { it.words.joinToString(" ").length },
    )

    private fun findFirstAccentedMatch(tokens: List<Token>, rules: List<ParserKeywordRule>): ParsedCategory? {
        for (rule in rules) {
            val k = rule.words.size
            if (k > tokens.size) continue
            for (i in 0..(tokens.size - k)) {
                if (isAccentedMatch(tokens, i, rule.words)) {
                    val phrase = rule.words.joinToString(" ")
                    return ParsedCategory(
                        categoryKey = rule.categoryKey,
                        confidence = FULL_CONFIDENCE,
                        matchedKeyword = phrase,
                        tokenRange = i until (i + k),
                    )
                }
            }
        }
        return null
    }

    private fun findFirstUnaccentedMatch(tokens: List<Token>, rules: List<ParserKeywordRule>): ParsedCategory? {
        for (rule in rules) {
            val k = rule.unaccentedWords.size
            val isIgnoredSingleWord = k == 1 && rule.unaccentedWords[0] in sensitiveSingleWords
            if (k > tokens.size || isIgnoredSingleWord) continue

            for (i in 0..(tokens.size - k)) {
                if (isUnaccentedMatch(tokens, i, rule.unaccentedWords)) {
                    val phrase = rule.words.joinToString(" ")
                    return ParsedCategory(
                        categoryKey = rule.categoryKey,
                        confidence = ACCENTLESS_CONFIDENCE,
                        matchedKeyword = phrase,
                        tokenRange = i until (i + k),
                    )
                }
            }
        }
        return null
    }

    private fun isAccentedMatch(tokens: List<Token>, startIndex: Int, words: List<String>): Boolean {
        for (j in words.indices) {
            if (tokens[startIndex + j].text.lowercase() != words[j]) return false
        }
        return true
    }

    private fun isUnaccentedMatch(tokens: List<Token>, startIndex: Int, words: List<String>): Boolean {
        for (j in words.indices) {
            if (tokens[startIndex + j].unaccented != words[j]) return false
        }
        return true
    }
}
