package hung.lct.chitieu.core.parser

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ClauseSplitterTest {

    private fun normalize(input: String) = VietnameseNormalizer.normalize(input)

    @Test
    fun split_noAmount_returnsEmptyList() {
        val clauses = ClauseSplitter.split(normalize("uống cà phê với bạn cuối tuần"))
        assertTrue(clauses.isEmpty())
    }

    @Test
    fun split_singleAmount_returnsOneClause() {
        val clauses = ClauseSplitter.split(normalize("sáng nay phở bò 45k"))
        assertEquals(1, clauses.size)
        assertEquals(45_000L, clauses[0].amount.value)
    }

    @Test
    fun split_commaSeparated_splitsIntoMultipleClauses() {
        val clauses = ClauseSplitter.split(normalize("ăn sáng 30k, đổ xăng 50k"))
        assertEquals(2, clauses.size)
        assertEquals(30_000L, clauses[0].amount.value)
        assertEquals(50_000L, clauses[1].amount.value)
    }

    @Test
    fun split_conjunctionVa_splitsIntoMultipleClauses() {
        val clauses = ClauseSplitter.split(normalize("mua bánh mì 20k và cà phê 25k"))
        assertEquals(2, clauses.size)
        assertEquals(20_000L, clauses[0].amount.value)
        assertEquals(25_000L, clauses[1].amount.value)
    }

    @Test
    fun split_conjunctionVoi_splitsIntoMultipleClauses() {
        val clauses = ClauseSplitter.split(normalize("mua áo 250k với mua quần 350k"))
        assertEquals(2, clauses.size)
        assertEquals(250_000L, clauses[0].amount.value)
        assertEquals(350_000L, clauses[1].amount.value)
    }

    @Test
    fun split_conjunctionInsideClause_isNotRemovedFromClauseTokens() {
        // "với" nằm bên trong khoản ("cà phê với bạn 30k") không được coi là ranh giới
        val clauses = ClauseSplitter.split(normalize("cà phê với bạn 30k, phở 40k"))
        assertEquals(2, clauses.size)
        val clause1Tokens = clauses[0].tokens.map { it.text }
        assertTrue("Từ 'với' bên trong khoản không được bị xóa", clause1Tokens.contains("với"))
    }

    @Test
    fun split_newlineSeparated_splitsIntoMultipleClauses() {
        val input = "phở 40k\ncafe 25k"
        val clauses = ClauseSplitter.split(normalize(input))
        assertEquals(2, clauses.size)
        assertEquals(40_000L, clauses[0].amount.value)
        assertEquals(25_000L, clauses[1].amount.value)
    }

    @Test
    fun split_threeClausesMixedDelimiters_splitsAccurately() {
        val clauses = ClauseSplitter.split(normalize("cơm tấm 40k, trà tắc 15k và gửi xe 5k"))
        assertEquals(3, clauses.size)
        assertEquals(40_000L, clauses[0].amount.value)
        assertEquals(15_000L, clauses[1].amount.value)
        assertEquals(5_000L, clauses[2].amount.value)
    }

    @Test
    fun split_vaInSingleAmount_doesNotSplit() {
        // Câu chỉ có 1 số tiền, "và" nằm giữa 2 danh từ món ăn
        val clauses = ClauseSplitter.split(normalize("mua rau và thịt 80k"))
        assertEquals(1, clauses.size)
        assertEquals(80_000L, clauses[0].amount.value)
    }
}
