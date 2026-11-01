package hung.lct.chitieu.core.parser

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CategoryClassifierTest {

    private fun normalize(input: String) = VietnameseNormalizer.normalize(input)

    @Test
    fun classify_foodAndDining_returnsCorrectCategoryAndConfidence() {
        val sentence = normalize("phở bò tái chín")
        val result = CategoryClassifier.classify(sentence.tokens)

        assertEquals("food_dining", result.categoryKey)
        assertEquals(1.0, result.confidence, 0.001)
        assertEquals("phở bò", result.matchedKeyword)
    }

    @Test
    fun classify_transportation_returnsCorrectCategory() {
        val sentence = normalize("đổ xăng xe wave")
        val result = CategoryClassifier.classify(sentence.tokens)

        assertEquals("transportation", result.categoryKey)
        assertEquals(1.0, result.confidence, 0.001)
    }

    @Test
    fun classify_unaccentedInput_matchesWithLowerConfidence() {
        // Khớp không dấu trả về confidence = 0.8
        val sentence = normalize("do xang 50k")
        val result = CategoryClassifier.classify(sentence.tokens)

        assertEquals("transportation", result.categoryKey)
        assertEquals(0.8, result.confidence, 0.001)
    }

    @Test
    fun classify_accentedInput_matchesWithFullConfidence() {
        // Khớp có dấu trả về confidence = 1.0
        val sentence = normalize("đổ xăng 50k")
        val result = CategoryClassifier.classify(sentence.tokens)

        assertEquals("transportation", result.categoryKey)
        assertEquals(1.0, result.confidence, 0.001)
    }

    @Test
    fun classify_sensitiveWords_doNotMatchUnaccentedToPreventCollisions() {
        // "cho" trong "đồ chơi cho bé" không được khớp với "chợ" (groceries)
        val sentence = normalize("đồ chơi cho bé 150k")
        val result = CategoryClassifier.classify(sentence.tokens)

        assertEquals("family", result.categoryKey)
        assertEquals(1.0, result.confidence, 0.001)

        // "đi chợ" có dấu thì khớp groceries bình thường
        val diCho = CategoryClassifier.classify(normalize("đi chợ 100k").tokens)
        assertEquals("groceries", diCho.categoryKey)
        assertEquals(1.0, diCho.confidence, 0.001)
    }

    @Test
    fun classify_longestMatchPriority_prefersSpecificCategory() {
        // "ăn cưới" phải vào family, không được nhận nhầm thành food_dining do từ "ăn"
        val sentence = normalize("đi ăn cưới bạn")
        val result = CategoryClassifier.classify(sentence.tokens)

        assertEquals("family", result.categoryKey)
        assertEquals(1.0, result.confidence, 0.001)
    }

    @Test
    fun classify_billsCategory_matchesMultipleBillsKeywords() {
        val electric = CategoryClassifier.classify(normalize("tiền điện tháng này").tokens)
        assertEquals("bills", electric.categoryKey)

        val internet = CategoryClassifier.classify(normalize("tiền mạng ba tháng").tokens)
        assertEquals("bills", internet.categoryKey)
        assertEquals("tiền mạng ba tháng", internet.matchedKeyword)
    }

    @Test
    fun classify_noMatchingKeyword_returnsFallbackWithZeroConfidence() {
        val sentence = normalize("chi tiêu cái gì đó lạ")
        val result = CategoryClassifier.classify(sentence.tokens)

        assertEquals("other_expense", result.categoryKey)
        assertEquals(0.0, result.confidence, 0.001)
        assertNull(result.matchedKeyword)
        assertNull(result.tokenRange)
    }

    @Test
    fun classify_userKeywordsOverridesDefaultDictionary() {
        // Người dùng gán từ "cf" vào danh mục riêng "entertainment"
        val userDict = mapOf("cf" to "entertainment")
        val sentence = normalize("uống cf sáng")
        val result = CategoryClassifier.classify(sentence.tokens, userDict)

        assertEquals("entertainment", result.categoryKey)
        assertEquals(1.0, result.confidence, 0.001)
    }
}
