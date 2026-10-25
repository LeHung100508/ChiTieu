package hung.lct.chitieu.core.parser

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.text.Normalizer

class VietnameseNormalizerTest {

    @Test
    fun removeDiacritics_removesVietnameseMarksCorrectly() {
        assertEquals("pho bo", VietnameseNormalizer.removeDiacritics("phở bò"))
        assertEquals("do xang", VietnameseNormalizer.removeDiacritics("đổ xăng"))
        assertEquals("di cho", VietnameseNormalizer.removeDiacritics("đi chợ"))
        assertEquals("Da Lat", VietnameseNormalizer.removeDiacritics("Đà Lạt"))
        assertEquals("tra sua", VietnameseNormalizer.removeDiacritics("trà sữa"))
    }

    @Test
    fun normalize_numberAndUnits_tokenizesNumbersAndUnitsCorrectly() {
        // Kiểm tra số dính chữ đơn vị: 45k -> 45 (NUMBER), k (WORD)
        val res45k = VietnameseNormalizer.normalize("45k")
        assertEquals(2, res45k.tokens.size)
        assertEquals("45", res45k.tokens[0].text)
        assertEquals(TokenType.NUMBER, res45k.tokens[0].type)
        assertEquals("k", res45k.tokens[1].text)
        assertEquals(TokenType.WORD, res45k.tokens[1].type)

        // Kiểm tra số - chữ - số xen kẽ: 1tr2 -> 1, tr, 2
        val res1tr2 = VietnameseNormalizer.normalize("1tr2")
        assertEquals(listOf("1", "tr", "2"), res1tr2.tokens.map { it.text })
        assertEquals(listOf(TokenType.NUMBER, TokenType.WORD, TokenType.NUMBER), res1tr2.tokens.map { it.type })

        // Kiểm tra số thập phân và phân cách hàng nghìn
        val resDecDot = VietnameseNormalizer.normalize("1.5tr")
        assertEquals(listOf("1.5", "tr"), resDecDot.tokens.map { it.text })
        assertEquals(TokenType.NUMBER, resDecDot.tokens[0].type)

        val resDecComma = VietnameseNormalizer.normalize("1,5tr")
        assertEquals(listOf("1,5", "tr"), resDecComma.tokens.map { it.text })
        assertEquals(TokenType.NUMBER, resDecComma.tokens[0].type)

        val resThousand = VietnameseNormalizer.normalize("45.000")
        assertEquals(1, resThousand.tokens.size)
        assertEquals("45.000", resThousand.tokens[0].text)
        assertEquals(TokenType.NUMBER, resThousand.tokens[0].type)
    }

    @Test
    fun normalize_fullSentence_createsExpectedTokensAndText() {
        val input = "  Sáng Ăn Phở Bò 45k  "
        val result = VietnameseNormalizer.normalize(input)

        assertEquals(input, result.raw)
        assertEquals("sáng ăn phở bò 45 k", result.text)
        assertEquals("sang an pho bo 45 k", result.unaccentedText)
        assertEquals(6, result.tokens.size)

        assertEquals("sáng", result.tokens[0].text)
        assertEquals("sang", result.tokens[0].unaccented)
        assertEquals("45", result.tokens[4].text)
        assertEquals("k", result.tokens[5].text)
    }

    @Test
    fun normalize_emptyOrBlankInput_returnsEmptyTokens() {
        val emptyResult = VietnameseNormalizer.normalize("")
        assertTrue(emptyResult.tokens.isEmpty())
        assertEquals("", emptyResult.text)

        val blankResult = VietnameseNormalizer.normalize("   ")
        assertTrue(blankResult.tokens.isEmpty())
        assertEquals("", blankResult.text)
    }

    @Test
    fun normalize_withComma_separatesPunctuationAndPreservesIndices() {
        val input = "sáng phở 45k, trưa cơm 35k"
        val result = VietnameseNormalizer.normalize(input)

        // Dấu phẩy là token riêng loại PUNCTUATION
        val commaToken = result.tokens.first { it.text == "," }
        assertEquals(TokenType.PUNCTUATION, commaToken.type)

        // Cắt theo vị trí token 'trưa' phải khớp chuỗi gốc
        val truaToken = result.tokens.first { it.text == "trưa" }
        assertEquals("trưa", result.raw.substring(truaToken.startIndex, truaToken.endIndex))

        // Token cuối cùng không bị vượt quá độ dài chuỗi gốc
        val lastKToken = result.tokens.last()
        assertEquals("k", result.raw.substring(lastKToken.startIndex, lastKToken.endIndex))
        assertEquals(input.length, lastKToken.endIndex)
    }

    @Test
    fun normalize_withNewline_preservesNewlineAsSeparateToken() {
        val input = "phở 40k\ncafe 25k"
        val result = VietnameseNormalizer.normalize(input)

        val newlineToken = result.tokens.first { it.text == "\n" }
        assertEquals(TokenType.NEWLINE, newlineToken.type)
        assertEquals("\n", result.raw.substring(newlineToken.startIndex, newlineToken.endIndex))
    }

    @Test
    fun normalize_nfdInput_convertsToNfcAndAlignsLengths() {
        val nfdInput = Normalizer.normalize("phở bò 45k", Normalizer.Form.NFD)
        val result = VietnameseNormalizer.normalize(nfdInput)

        val phoToken = result.tokens.first()
        assertEquals("phở", phoToken.text)
        assertEquals(3, phoToken.text.length) // Chuẩn NFC 3 ký tự, không phải 5 ký tự NFD
        assertEquals("phở", result.raw.substring(phoToken.startIndex, phoToken.endIndex))
    }

    @Test
    fun normalize_uppercaseInput_slicesOriginalCasingFromRaw() {
        val input = "Grab về nhà 62k"
        val result = VietnameseNormalizer.normalize(input)

        val grabToken = result.tokens.first()
        assertEquals("grab", grabToken.text)
        assertEquals("Grab", result.raw.substring(grabToken.startIndex, grabToken.endIndex))

        val kToken = result.tokens.last()
        assertEquals(input.length, kToken.endIndex)
    }
}
