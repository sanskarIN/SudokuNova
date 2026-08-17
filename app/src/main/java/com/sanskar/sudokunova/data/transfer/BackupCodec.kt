package com.sanskar.sudokunova.data.transfer

import com.sanskar.sudokunova.data.InputMode
import com.sanskar.sudokunova.data.UserSettings
import com.sanskar.sudokunova.data.challenge.ChallengeType
import com.sanskar.sudokunova.engine.Difficulty
import com.sanskar.sudokunova.engine.SudokuBoard
import com.sanskar.sudokunova.ui.theme.AppTheme
import java.nio.charset.StandardCharsets
import java.util.Base64
import java.util.Locale
import java.util.zip.CRC32

object BackupCodec {
    private const val HEADER = "SNB1"
    private const val FOOTER = "Z"
    const val MAX_BACKUP_BYTES = 2 * 1024 * 1024
    const val MAX_HISTORY_RECORDS = 5_000
    const val MAX_SAVED_PUZZLES = 2_000
    const val MAX_CHALLENGE_RESULTS = 2_000
    private const val MAX_LINES = 10_005
    private const val MAX_TEXT_FIELD_BYTES = 512
    private const val MAX_COUNTER = 1_000_000
    private const val MAX_ELAPSED_SECONDS = 315_576_000L
    private const val MAX_EPOCH_MILLIS = 32_503_680_000_000L

    fun encode(backup: SudokuNovaBackup): String {
        require(backup.history.size <= MAX_HISTORY_RECORDS)
        require(backup.savedPuzzles.size <= MAX_SAVED_PUZZLES)
        require(backup.challengeResults.size <= MAX_CHALLENGE_RESULTS)

        val lines = buildList {
            add(HEADER)
            add(encodeSettings(backup.settings))
            backup.history.forEach { add(encodeHistory(it)) }
            backup.savedPuzzles.forEach { add(encodeSavedPuzzle(it)) }
            backup.challengeResults.forEach { add(encodeChallenge(it)) }
        }
        val body = lines.joinToString("\n")
        val encoded = "$body\n$FOOTER|${checksum(body)}"
        require(encoded.toByteArray(StandardCharsets.UTF_8).size <= MAX_BACKUP_BYTES)
        return encoded
    }

    fun decode(raw: String): SudokuNovaBackup? = runCatching {
        val bytes = raw.toByteArray(StandardCharsets.UTF_8)
        require(bytes.isNotEmpty() && bytes.size <= MAX_BACKUP_BYTES)
        require(!raw.contains('\u0000'))

        val lines = raw.trim().lines()
        require(lines.size in 3..MAX_LINES)
        require(lines.first() == HEADER)
        val footer = lines.last().split('|')
        require(footer.size == 2 && footer[0] == FOOTER)
        val body = lines.dropLast(1).joinToString("\n")
        require(footer[1].uppercase(Locale.ROOT) == checksum(body))

        var settings: UserSettings? = null
        val history = ArrayList<BackupHistoryRecord>()
        val saved = ArrayList<BackupSavedPuzzleRecord>()
        val challenges = ArrayList<BackupChallengeRecord>()

        for (line in lines.drop(1).dropLast(1)) {
            val fields = line.split('|')
            when (fields.firstOrNull()) {
                "S" -> {
                    require(settings == null)
                    settings = decodeSettings(fields)
                }
                "H" -> {
                    require(history.size < MAX_HISTORY_RECORDS)
                    history += decodeHistory(fields)
                }
                "P" -> {
                    require(saved.size < MAX_SAVED_PUZZLES)
                    saved += decodeSavedPuzzle(fields)
                }
                "C" -> {
                    require(challenges.size < MAX_CHALLENGE_RESULTS)
                    challenges += decodeChallenge(fields)
                }
                else -> error("Unsupported backup record type")
            }
        }

        SudokuNovaBackup(
            settings = requireNotNull(settings),
            history = history,
            savedPuzzles = saved,
            challengeResults = challenges,
        )
    }.getOrNull()

    private fun encodeSettings(settings: UserSettings): String = listOf(
        "S",
        settings.theme.name,
        settings.dynamicColor.toString(),
        settings.inputMode.name,
        settings.highlightPeers.toString(),
        settings.highlightSameNumbers.toString(),
        settings.autoCheckMistakes.toString(),
        settings.autoRemoveNotes.toString(),
        settings.showTimer.toString(),
        settings.haptics.toString(),
        settings.sounds.toString(),
        settings.reducedMotion.toString(),
        settings.highContrast.toString(),
        settings.mistakeLimit.toString(),
    ).joinToString("|")

    private fun decodeSettings(fields: List<String>): UserSettings {
        require(fields.size == 14)
        val mistakeLimit = fields[13].toInt()
        require(mistakeLimit in 0..100)
        return UserSettings(
            theme = AppTheme.valueOf(fields[1]),
            dynamicColor = fields[2].toBooleanStrict(),
            inputMode = InputMode.valueOf(fields[3]),
            highlightPeers = fields[4].toBooleanStrict(),
            highlightSameNumbers = fields[5].toBooleanStrict(),
            autoCheckMistakes = fields[6].toBooleanStrict(),
            autoRemoveNotes = fields[7].toBooleanStrict(),
            showTimer = fields[8].toBooleanStrict(),
            haptics = fields[9].toBooleanStrict(),
            sounds = fields[10].toBooleanStrict(),
            reducedMotion = fields[11].toBooleanStrict(),
            highContrast = fields[12].toBooleanStrict(),
            mistakeLimit = mistakeLimit,
        )
    }

    private fun encodeHistory(record: BackupHistoryRecord): String {
        validatePuzzleAndSolution(record.puzzle, record.solution)
        validateCounters(record.elapsedSeconds, record.mistakes, record.hintsUsed)
        require(record.startedAtEpochMillis in 0..MAX_EPOCH_MILLIS)
        require(record.completedAtEpochMillis == null || record.completedAtEpochMillis in record.startedAtEpochMillis..MAX_EPOCH_MILLIS)
        require(record.isPerfect == (record.mistakes == 0 && record.hintsUsed == 0))
        Difficulty.valueOf(record.difficulty)
        return listOf(
            "H",
            record.puzzle,
            record.solution,
            record.difficulty,
            record.completed.toString(),
            record.elapsedSeconds.toString(),
            record.mistakes.toString(),
            record.hintsUsed.toString(),
            record.startedAtEpochMillis.toString(),
            record.completedAtEpochMillis?.toString().orEmpty(),
            record.isDailyChallenge.toString(),
            record.isPerfect.toString(),
            record.isFavorite.toString(),
        ).joinToString("|")
    }

    private fun decodeHistory(fields: List<String>): BackupHistoryRecord {
        require(fields.size == 13)
        validatePuzzleAndSolution(fields[1], fields[2])
        Difficulty.valueOf(fields[3])
        val elapsed = fields[5].toLong()
        val mistakes = fields[6].toInt()
        val hints = fields[7].toInt()
        validateCounters(elapsed, mistakes, hints)
        val started = fields[8].toLong().also { require(it in 0..MAX_EPOCH_MILLIS) }
        val completedAt = fields[9].takeIf(String::isNotBlank)?.toLong()?.also { require(it in started..MAX_EPOCH_MILLIS) }
        val perfect = fields[11].toBooleanStrict()
        require(perfect == (mistakes == 0 && hints == 0))
        return BackupHistoryRecord(
            puzzle = fields[1],
            solution = fields[2],
            difficulty = fields[3],
            completed = fields[4].toBooleanStrict(),
            elapsedSeconds = elapsed,
            mistakes = mistakes,
            hintsUsed = hints,
            startedAtEpochMillis = started,
            completedAtEpochMillis = completedAt,
            isDailyChallenge = fields[10].toBooleanStrict(),
            isPerfect = perfect,
            isFavorite = fields[12].toBooleanStrict(),
        )
    }

    private fun encodeSavedPuzzle(record: BackupSavedPuzzleRecord): String {
        validatePuzzle(record.puzzle)
        record.solution?.let { validatePuzzleAndSolution(record.puzzle, it) }
        Difficulty.valueOf(record.difficulty)
        require(record.createdAtEpochMillis in 0..MAX_EPOCH_MILLIS)
        return listOf(
            "P",
            record.puzzle,
            encodeOptional(record.solution),
            encodeOptional(record.title),
            record.difficulty,
            encodeRequired(record.source),
            record.createdAtEpochMillis.toString(),
            record.isFavorite.toString(),
        ).joinToString("|")
    }

    private fun decodeSavedPuzzle(fields: List<String>): BackupSavedPuzzleRecord {
        require(fields.size == 8)
        validatePuzzle(fields[1])
        val solution = decodeOptional(fields[2])
        solution?.let { validatePuzzleAndSolution(fields[1], it) }
        val title = decodeOptional(fields[3])
        Difficulty.valueOf(fields[4])
        val source = decodeRequired(fields[5])
        val created = fields[6].toLong().also { require(it in 0..MAX_EPOCH_MILLIS) }
        return BackupSavedPuzzleRecord(
            puzzle = fields[1],
            solution = solution,
            title = title,
            difficulty = fields[4],
            source = source,
            createdAtEpochMillis = created,
            isFavorite = fields[7].toBooleanStrict(),
        )
    }

    private fun encodeChallenge(record: BackupChallengeRecord): String {
        ChallengeType.valueOf(record.challengeType)
        Difficulty.valueOf(record.difficulty)
        validatePuzzle(record.puzzle)
        validateCounters(record.elapsedSeconds, record.mistakes, record.hintsUsed)
        require(record.challengeKey >= 0L)
        require(record.completedAtEpochMillis in 0..MAX_EPOCH_MILLIS)
        require(record.perfect == (record.mistakes == 0 && record.hintsUsed == 0))
        return listOf(
            "C",
            record.challengeType,
            record.challengeKey.toString(),
            record.difficulty,
            record.puzzle,
            record.elapsedSeconds.toString(),
            record.mistakes.toString(),
            record.hintsUsed.toString(),
            record.completedAtEpochMillis.toString(),
            record.perfect.toString(),
        ).joinToString("|")
    }

    private fun decodeChallenge(fields: List<String>): BackupChallengeRecord {
        require(fields.size == 10)
        ChallengeType.valueOf(fields[1])
        val key = fields[2].toLong().also { require(it >= 0L) }
        Difficulty.valueOf(fields[3])
        validatePuzzle(fields[4])
        val elapsed = fields[5].toLong()
        val mistakes = fields[6].toInt()
        val hints = fields[7].toInt()
        validateCounters(elapsed, mistakes, hints)
        val completedAt = fields[8].toLong().also { require(it in 0..MAX_EPOCH_MILLIS) }
        val perfect = fields[9].toBooleanStrict()
        require(perfect == (mistakes == 0 && hints == 0))
        return BackupChallengeRecord(
            challengeType = fields[1],
            challengeKey = key,
            difficulty = fields[3],
            puzzle = fields[4],
            elapsedSeconds = elapsed,
            mistakes = mistakes,
            hintsUsed = hints,
            completedAtEpochMillis = completedAt,
            perfect = perfect,
        )
    }

    private fun validatePuzzle(encoded: String): SudokuBoard {
        require(encoded.length == SudokuBoard.CELL_COUNT)
        require(encoded.all { it in '0'..'9' })
        return SudokuBoard.parse(encoded).also { require(it.isValid()) }
    }

    private fun validatePuzzleAndSolution(puzzleText: String, solutionText: String) {
        val puzzle = validatePuzzle(puzzleText)
        val solution = validatePuzzle(solutionText)
        require(solution.isComplete)
        for (index in 0 until SudokuBoard.CELL_COUNT) {
            val clue = puzzle.valueAt(index)
            if (clue != SudokuBoard.EMPTY) require(solution.valueAt(index) == clue)
        }
    }

    private fun validateCounters(elapsedSeconds: Long, mistakes: Int, hintsUsed: Int) {
        require(elapsedSeconds in 0..MAX_ELAPSED_SECONDS)
        require(mistakes in 0..MAX_COUNTER)
        require(hintsUsed in 0..MAX_COUNTER)
    }

    private fun encodeOptional(value: String?): String = value?.let(::encodeRequired).orEmpty()

    private fun decodeOptional(value: String): String? = value.takeIf(String::isNotBlank)?.let(::decodeRequired)

    private fun encodeRequired(value: String): String {
        val bytes = value.toByteArray(StandardCharsets.UTF_8)
        require(bytes.isNotEmpty() && bytes.size <= MAX_TEXT_FIELD_BYTES)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
    }

    private fun decodeRequired(value: String): String {
        require(value.isNotBlank() && value.length <= MAX_TEXT_FIELD_BYTES * 2)
        val bytes = Base64.getUrlDecoder().decode(value)
        require(bytes.isNotEmpty() && bytes.size <= MAX_TEXT_FIELD_BYTES)
        return String(bytes, StandardCharsets.UTF_8).also {
            require(!it.contains('\u0000') && !it.contains('\n') && !it.contains('\r'))
        }
    }

    private fun checksum(body: String): String {
        val crc = CRC32()
        crc.update(body.toByteArray(StandardCharsets.UTF_8))
        return "%08X".format(Locale.ROOT, crc.value)
    }
}
