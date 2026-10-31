package hung.lct.chitieu.core.parser

import java.time.Clock
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import java.time.ZoneId

private const val MORNING_HOUR = 8
private const val NOON_HOUR = 12
private const val AFTERNOON_HOUR = 15
private const val EVENING_HOUR = 20
private const val NIGHT_HOUR = 22
private const val DAYS_IN_WEEK = 7L
private const val ONE_DAY = 1L
private const val TWO_DAYS = 2L
private const val MONDAY_DAY_OF_WEEK_VALUE = 1
private const val SUNDAY_DAY_OF_WEEK_OFFSET = 6L
private const val MAX_MONTHS_IN_YEAR = 12
private const val TOKEN_COUNT_FOUR = 4
private const val TOKEN_COUNT_THREE = 3
private const val TOKEN_COUNT_TWO = 2
private const val TOKEN_COUNT_ONE = 1
private const val OFFSET_ONE = 1
private const val OFFSET_TWO = 2
private const val OFFSET_THREE = 3

private val MORNING_TIME = LocalTime.of(MORNING_HOUR, 0, 0)
private val NOON_TIME = LocalTime.of(NOON_HOUR, 0, 0)
private val AFTERNOON_TIME = LocalTime.of(AFTERNOON_HOUR, 0, 0)
private val EVENING_TIME = LocalTime.of(EVENING_HOUR, 0, 0)
private val NIGHT_TIME = LocalTime.of(NIGHT_HOUR, 0, 0)

private val dayOfWeekMap = mapOf(
    "2" to DayOfWeek.MONDAY,
    "hai" to DayOfWeek.MONDAY,
    "3" to DayOfWeek.TUESDAY,
    "ba" to DayOfWeek.TUESDAY,
    "4" to DayOfWeek.WEDNESDAY,
    "tu" to DayOfWeek.WEDNESDAY,
    "5" to DayOfWeek.THURSDAY,
    "nam" to DayOfWeek.THURSDAY,
    "6" to DayOfWeek.FRIDAY,
    "sau" to DayOfWeek.FRIDAY,
    "7" to DayOfWeek.SATURDAY,
    "bay" to DayOfWeek.SATURDAY,
    "cn" to DayOfWeek.SUNDAY,
)

private val mealNoiseWords = setOf("an", "com", "cf", "cafe", "uong", "mon", "bua")
private val clauseDelimiters = setOf(",", ".", ";", "\n")
private val measurementUnits = setOf(
    "kg", "g", "l", "lit", "ml", "lon", "ly", "chai", "hop", "bat", "dia", "goi", "qua", "trai", "m", "met",
)
private val sessionTimes = mapOf(
    "sang" to MORNING_TIME,
    "trua" to NOON_TIME,
    "chieu" to AFTERNOON_TIME,
    "toi" to EVENING_TIME,
    "dem" to NIGHT_TIME,
)

private data class DateTimeMatch(
    val localDateTime: LocalDateTime,
    val consumedCount: Int,
)

/**
 * Bộ phân tích mốc thời gian tự nhiên từ câu nhập (FR-03).
 */
object DateTimeParser {

    val DEFAULT_ZONE_ID: ZoneId = ZoneId.of("Asia/Ho_Chi_Minh")

    /**
     * Trích xuất toàn bộ các mốc thời gian được nhắc tới trong câu.
     */
    fun parseAll(
        sentence: NormalizedSentence,
        clock: Clock = Clock.systemDefaultZone(),
        zoneId: ZoneId = DEFAULT_ZONE_ID,
    ): List<ParsedDateTime> {
        val tokens = sentence.tokens
        val zonedClock = clock.withZone(zoneId)
        val today = LocalDate.now(zonedClock)
        val currentTime = LocalTime.now(zonedClock)
        val result = mutableListOf<ParsedDateTime>()
        var i = 0

        while (i < tokens.size) {
            val match = DateMatcher.match(tokens, i, today, currentTime)
                ?: SessionMatcher.match(tokens, i, today, currentTime)
            if (match != null) {
                val tokenStart = i
                val tokenEnd = i + match.consumedCount - 1
                result.add(
                    ParsedDateTime(
                        localDateTime = match.localDateTime,
                        startIndex = tokens[tokenStart].startIndex,
                        endIndex = tokens[tokenEnd].endIndex,
                        tokenRange = tokenStart..tokenEnd,
                    ),
                )
                i += match.consumedCount
            } else {
                i++
            }
        }
        return result
    }

    /**
     * Trích xuất mốc thời gian đầu tiên trong câu hoặc trả về thời gian hiện tại nếu không có (FR-03).
     */
    fun parseOrDefault(
        sentence: NormalizedSentence,
        clock: Clock = Clock.systemDefaultZone(),
        zoneId: ZoneId = DEFAULT_ZONE_ID,
    ): LocalDateTime = parseAll(sentence, clock, zoneId).firstOrNull()?.localDateTime
        ?: LocalDateTime.now(clock.withZone(zoneId))
}

private object DateMatcher {

    fun match(
        tokens: List<Token>,
        index: Int,
        today: LocalDate,
        currentTime: LocalTime,
    ): DateTimeMatch? = matchDayOfWeekLastWeek(tokens, index, today, currentTime)
        ?: matchDateAndMonth(tokens, index, today, currentTime)
        ?: matchDayOfMonth(tokens, index, today, currentTime)

    private fun resolveSafeDate(year: Int, month: Int, day: Int, today: LocalDate): LocalDate? {
        val isValid = month in 1..MAX_MONTHS_IN_YEAR && YearMonth.of(year, month).isValidDay(day)
        if (!isValid) return null

        val candidate = LocalDate.of(year, month, day)
        return if (candidate > today) {
            val prevYear = year - 1
            if (YearMonth.of(prevYear, month).isValidDay(day)) LocalDate.of(prevYear, month, day) else null
        } else {
            candidate
        }
    }

    private fun matchDayOfWeekLastWeek(
        tokens: List<Token>,
        index: Int,
        today: LocalDate,
        currentTime: LocalTime,
    ): DateTimeMatch? {
        val t0 = tokens.getOrNull(index)?.unaccented
        val t1 = tokens.getOrNull(index + OFFSET_ONE)?.unaccented
        val t2 = tokens.getOrNull(index + OFFSET_TWO)?.unaccented
        val t3 = tokens.getOrNull(index + OFFSET_THREE)?.unaccented

        val isLastWeek = t2 == "tuan" && t3 == "truoc"
        if (!isLastWeek || t0 == null || t1 == null) return null

        val currentMonday =
            today.minusDays((today.dayOfWeek.value - MONDAY_DAY_OF_WEEK_VALUE).toLong())
        val targetMonday = currentMonday.minusDays(DAYS_IN_WEEK)

        return when {
            t0 == "thu" && dayOfWeekMap.containsKey(t1) -> {
                val targetDay = dayOfWeekMap.getValue(t1)
                val targetDate =
                    targetMonday.plusDays((targetDay.value - MONDAY_DAY_OF_WEEK_VALUE).toLong())
                DateTimeMatch(targetDate.atTime(currentTime), consumedCount = TOKEN_COUNT_FOUR)
            }

            t0 == "chu" && t1 == "nhat" -> {
                val targetDate = targetMonday.plusDays(SUNDAY_DAY_OF_WEEK_OFFSET)
                DateTimeMatch(targetDate.atTime(currentTime), consumedCount = TOKEN_COUNT_FOUR)
            }

            else -> null
        }
    }

    private fun matchDateAndMonth(
        tokens: List<Token>,
        index: Int,
        today: LocalDate,
        currentTime: LocalTime,
    ): DateTimeMatch? = matchSpelledOutDateAndMonth(tokens, index, today, currentTime)
        ?: matchHomSlashDate(tokens, index, today, currentTime)
        ?: matchDirectSlashDate(tokens, index, today, currentTime)

    private fun matchSpelledOutDateAndMonth(
        tokens: List<Token>,
        index: Int,
        today: LocalDate,
        currentTime: LocalTime,
    ): DateTimeMatch? {
        val t0 = tokens.getOrNull(index)
        val t1 = tokens.getOrNull(index + OFFSET_ONE)
        val t2 = tokens.getOrNull(index + OFFSET_TWO)
        val t3 = tokens.getOrNull(index + OFFSET_THREE)

        val isPattern = t0?.unaccented == "ngay" && t2?.unaccented == "thang" && t3 != null
        val d = if (isPattern) t1?.text?.toIntOrNull() else null
        val m = if (isPattern) t3?.text?.toIntOrNull() else null

        val resolvedDate = if (d != null && m != null) {
            resolveSafeDate(today.year, m, d, today)
        } else {
            null
        }
        return resolvedDate?.let { DateTimeMatch(it.atTime(currentTime), TOKEN_COUNT_FOUR) }
    }

    private fun matchHomSlashDate(
        tokens: List<Token>,
        index: Int,
        today: LocalDate,
        currentTime: LocalTime,
    ): DateTimeMatch? {
        val t0 = tokens.getOrNull(index)
        val t1 = tokens.getOrNull(index + OFFSET_ONE)
        val t2 = tokens.getOrNull(index + OFFSET_TWO)
        val t3 = tokens.getOrNull(index + OFFSET_THREE)

        val isHomSlash = t0?.unaccented == "hom" && t2?.text == "/" && t3 != null
        val d = if (isHomSlash) t1?.text?.toIntOrNull() else null
        val m = if (isHomSlash) t3?.text?.toIntOrNull() else null

        val resolvedDate = if (d != null && m != null) {
            resolveSafeDate(today.year, m, d, today)
        } else {
            null
        }
        return resolvedDate?.let { DateTimeMatch(it.atTime(currentTime), TOKEN_COUNT_FOUR) }
    }

    private fun matchDirectSlashDate(
        tokens: List<Token>,
        index: Int,
        today: LocalDate,
        currentTime: LocalTime,
    ): DateTimeMatch? {
        val t0 = tokens.getOrNull(index)
        val t1 = tokens.getOrNull(index + OFFSET_ONE)
        val t2 = tokens.getOrNull(index + OFFSET_TWO)
        val nextAfter = tokens.getOrNull(index + OFFSET_THREE)?.unaccented

        val isDirectSlash = t1?.text == "/" && t2 != null && t0 != null && nextAfter !in measurementUnits
        val d = if (isDirectSlash) t0.text.toIntOrNull() else null
        val m = if (isDirectSlash) t2.text.toIntOrNull() else null

        val resolvedDate = if (d != null && m != null) {
            resolveSafeDate(today.year, m, d, today)
        } else {
            null
        }
        return resolvedDate?.let { DateTimeMatch(it.atTime(currentTime), TOKEN_COUNT_THREE) }
    }

    private fun matchDayOfMonth(
        tokens: List<Token>,
        index: Int,
        today: LocalDate,
        currentTime: LocalTime,
    ): DateTimeMatch? {
        val t0 = tokens.getOrNull(index)?.unaccented
        val t1 = tokens.getOrNull(index + OFFSET_ONE)
        val isDayPrefix = (t0 == "ngay" || t0 == "mung") && t1 != null
        val day = if (isDayPrefix) t1!!.text.toIntOrNull() else null
        if (day == null) return null

        val targetMonth = if (day > today.dayOfMonth) today.minusMonths(1) else today
        val isValid = YearMonth.from(targetMonth).isValidDay(day)
        return if (isValid) {
            DateTimeMatch(
                targetMonth.withDayOfMonth(day).atTime(currentTime),
                consumedCount = TOKEN_COUNT_TWO,
            )
        } else {
            null
        }
    }
}

private object SessionMatcher {

    fun match(
        tokens: List<Token>,
        index: Int,
        today: LocalDate,
        currentTime: LocalTime,
    ): DateTimeMatch? = matchSessionWithThreeTokens(tokens, index, today)
        ?: matchSessionWithModifier(tokens, index, today, currentTime)
        ?: matchStandaloneSession(tokens, index, today)

    private fun matchSessionWithThreeTokens(tokens: List<Token>, index: Int, today: LocalDate): DateTimeMatch? {
        val t0 = tokens.getOrNull(index)?.unaccented
        val t1 = tokens.getOrNull(index + OFFSET_ONE)?.unaccented
        val t2 = tokens.getOrNull(index + OFFSET_TWO)?.unaccented

        val sessionTime = if (t1 == "hom" && t0 != null) sessionTimes[t0] else null
        if (sessionTime == null || t2 == null) return null

        return when (t2) {
            "qua" -> DateTimeMatch(today.minusDays(ONE_DAY).atTime(sessionTime), consumedCount = TOKEN_COUNT_THREE)
            "nay" -> DateTimeMatch(today.atTime(sessionTime), consumedCount = TOKEN_COUNT_THREE)
            else -> null
        }
    }

    private fun matchSessionWithModifier(
        tokens: List<Token>,
        index: Int,
        today: LocalDate,
        currentTime: LocalTime,
    ): DateTimeMatch? {
        val t0 = tokens.getOrNull(index)?.unaccented
        val t1 = tokens.getOrNull(index + OFFSET_ONE)?.unaccented
        if (t0 == null || t1 == null) return null

        return when (t1) {
            "nay" -> matchNayModifier(t0, today, currentTime)

            "qua" -> matchQuaModifier(t0, today, currentTime)

            "kia" -> if (t0 == "hom") {
                DateTimeMatch(
                    today.minusDays(TWO_DAYS).atTime(currentTime),
                    consumedCount = TOKEN_COUNT_TWO,
                )
            } else {
                null
            }

            else -> null
        }
    }

    private fun matchNayModifier(
        t0: String,
        today: LocalDate,
        currentTime: LocalTime,
    ): DateTimeMatch? {
        val time = if (t0 == "hom") currentTime else sessionTimes[t0]
        return time?.let { DateTimeMatch(today.atTime(it), consumedCount = TOKEN_COUNT_TWO) }
    }

    private fun matchQuaModifier(t0: String, today: LocalDate, currentTime: LocalTime): DateTimeMatch? {
        val (time, offset) = when (t0) {
            "sang" -> Pair(MORNING_TIME, ONE_DAY)
            "trua" -> Pair(NOON_TIME, ONE_DAY)
            "chieu" -> Pair(AFTERNOON_TIME, ONE_DAY)
            "toi" -> Pair(EVENING_TIME, ONE_DAY)
            "dem" -> Pair(NIGHT_TIME, ONE_DAY)
            "hom" -> Pair(currentTime, ONE_DAY)
            else -> null
        } ?: return null

        return DateTimeMatch(today.minusDays(offset).atTime(time), consumedCount = TOKEN_COUNT_TWO)
    }

    private fun matchStandaloneSession(
        tokens: List<Token>,
        index: Int,
        today: LocalDate,
    ): DateTimeMatch? {
        val token = tokens.getOrNull(index)
        if (token == null || isIgnoredSessionContext(tokens, index)) return null

        val isClauseBoundary = index == 0 ||
            clauseDelimiters.contains(tokens[index - 1].text) ||
            tokens[index - 1].type == TokenType.NEWLINE

        val simpleTime = when (token.unaccented) {
            "sang" -> MORNING_TIME
            "chieu" -> AFTERNOON_TIME
            "toi" -> EVENING_TIME
            else -> null
        }

        return when {
            simpleTime != null && isClauseBoundary -> {
                DateTimeMatch(today.atTime(simpleTime), consumedCount = TOKEN_COUNT_ONE)
            }

            token.unaccented == "trua" -> {
                matchNoon(tokens, index, isClauseBoundary, today)
            }

            else -> null
        }
    }

    private fun isIgnoredSessionContext(tokens: List<Token>, index: Int): Boolean {
        if (index > 0 && mealNoiseWords.contains(tokens[index - 1].unaccented)) {
            return true
        }
        val token = tokens[index]
        val isTransferTo = token.text == "sang" && index > 0
        val isPrecededByTransferWord = index > 0 &&
            (tokens[index - 1].unaccented == "tu" || tokens[index - 1].unaccented == "chuyen")
        return isTransferTo && isPrecededByTransferWord
    }

    private fun matchNoon(
        tokens: List<Token>,
        index: Int,
        isClauseBoundary: Boolean,
        today: LocalDate,
    ): DateTimeMatch? {
        val prev = tokens.getOrNull(index - 1)
        val isAfterAmount = prev != null &&
            (prev.type == TokenType.NUMBER || prev.unaccented == "k" || prev.unaccented == "tr")
        val isMatch = isClauseBoundary || isAfterAmount
        return if (isMatch) {
            DateTimeMatch(today.atTime(NOON_TIME), consumedCount = TOKEN_COUNT_ONE)
        } else {
            null
        }
    }
}
