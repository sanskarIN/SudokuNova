from pathlib import Path


def update_transfer_screen() -> None:
    path = Path("app/src/main/java/com/sanskar/sudokunova/ui/transfer/TransferScreen.kt")
    text = path.read_text()
    if "import androidx.compose.runtime.rememberCoroutineScope" not in text:
        text = text.replace(
            "import androidx.compose.runtime.remember\n",
            "import androidx.compose.runtime.remember\nimport androidx.compose.runtime.rememberCoroutineScope\n",
        )
    if "import kotlinx.coroutines.Dispatchers" not in text:
        text = text.replace(
            "import com.sanskar.sudokunova.ui.common.localizedDifficultyLabel\n",
            "import com.sanskar.sudokunova.ui.common.localizedDifficultyLabel\n"
            "import kotlinx.coroutines.Dispatchers\n"
            "import kotlinx.coroutines.launch\n"
            "import kotlinx.coroutines.withContext\n",
        )
    if "val coroutineScope = rememberCoroutineScope()" not in text:
        text = text.replace(
            "    val context = LocalContext.current\n",
            "    val context = LocalContext.current\n    val coroutineScope = rememberCoroutineScope()\n",
        )
    old_export = """        if (uri != null && textToWrite != null) {
            transientMessage = if (BackupFileIo.write(context, uri, textToWrite)) exportSuccess else exportFailed
        }
"""
    new_export = """        if (uri != null && textToWrite != null) {
            coroutineScope.launch {
                val written = withContext(Dispatchers.IO) {
                    BackupFileIo.write(context, uri, textToWrite)
                }
                transientMessage = if (written) exportSuccess else exportFailed
            }
        }
"""
    if old_export not in text:
        raise SystemExit("Expected export callback block not found")
    text = text.replace(old_export, new_export)
    old_import = """        if (uri != null) {
            val imported = BackupFileIo.read(context, uri)
            if (imported == null) transientMessage = importFailed else pendingRestore = imported
        }
"""
    new_import = """        if (uri != null) {
            coroutineScope.launch {
                val imported = withContext(Dispatchers.IO) {
                    BackupFileIo.read(context, uri)
                }
                if (imported == null) transientMessage = importFailed else pendingRestore = imported
            }
        }
"""
    if old_import not in text:
        raise SystemExit("Expected import callback block not found")
    path.write_text(text.replace(old_import, new_import))


def update_codec() -> None:
    path = Path("app/src/main/java/com/sanskar/sudokunova/data/transfer/BackupCodec.kt")
    text = path.read_text()
    text = text.replace(
        "        require(record.createdAtEpochMillis >= 0L)\n",
        "        require(record.createdAtEpochMillis in 0..MAX_EPOCH_MILLIS)\n",
    )
    text = text.replace(
        "        val created = fields[6].toLong().also { require(it >= 0L) }\n",
        "        val created = fields[6].toLong().also { require(it in 0..MAX_EPOCH_MILLIS) }\n",
    )
    path.write_text(text)


def update_repository() -> None:
    path = Path("app/src/main/java/com/sanskar/sudokunova/data/transfer/BackupRepository.kt")
    text = path.read_text()
    start = text.index("    suspend fun importBackup(backup: SudokuNovaBackup): BackupImportResult {")
    end = text.index("    private fun historySignature(entity: GameHistoryEntity): String")
    new_method = """    suspend fun importBackup(backup: SudokuNovaBackup): BackupImportResult {
        val existingHistoryEntities = historyDao.observeAll().first()
        val historySignatures = existingHistoryEntities.map(::historySignature).toMutableSet()
        val historyIds = existingHistoryEntities.associate { historySignature(it) to it.id }.toMutableMap()
        val historyFavorites = existingHistoryEntities.associate { historySignature(it) to it.isFavorite }.toMutableMap()
        val existingSavedByPuzzle = savedDao.observeAll().first().associateBy { it.puzzle }.toMutableMap()

        var historyImported = 0
        var historySkipped = 0
        var savedImported = 0
        var savedSkipped = 0
        var challengesImported = 0
        var challengesSkipped = 0

        database.withTransaction {
            backup.history.forEach { record ->
                val entity = GameHistoryEntity(
                    puzzle = record.puzzle,
                    solution = record.solution,
                    difficulty = record.difficulty,
                    completed = record.completed,
                    elapsedSeconds = record.elapsedSeconds,
                    mistakes = record.mistakes,
                    hintsUsed = record.hintsUsed,
                    startedAtEpochMillis = record.startedAtEpochMillis,
                    completedAtEpochMillis = record.completedAtEpochMillis,
                    isDailyChallenge = record.isDailyChallenge,
                    isPerfect = record.isPerfect,
                    isFavorite = record.isFavorite,
                    replayOfHistoryId = null,
                )
                val signature = historySignature(entity)
                if (!historySignatures.add(signature)) {
                    val existingId = historyIds[signature]
                    if (record.isFavorite && historyFavorites[signature] != true && existingId != null) {
                        historyDao.setFavorite(existingId, true)
                        historyFavorites[signature] = true
                    }
                    historySkipped++
                } else {
                    val id = historyDao.insert(entity)
                    historyIds[signature] = id
                    historyFavorites[signature] = record.isFavorite
                    historyImported++
                }
            }

            backup.savedPuzzles.forEach { record ->
                val existing = existingSavedByPuzzle[record.puzzle]
                if (existing != null) {
                    if (record.isFavorite && !existing.isFavorite) {
                        savedDao.setFavorite(existing.id, true)
                        existingSavedByPuzzle[record.puzzle] = existing.copy(isFavorite = true)
                    }
                    savedSkipped++
                } else {
                    val entity = SavedPuzzleEntity(
                        puzzle = record.puzzle,
                        solution = record.solution,
                        title = record.title,
                        difficulty = record.difficulty,
                        source = record.source,
                        createdAtEpochMillis = record.createdAtEpochMillis,
                        isFavorite = record.isFavorite,
                    )
                    val id = savedDao.insert(entity)
                    if (id > 0L) {
                        savedImported++
                        existingSavedByPuzzle[record.puzzle] = entity.copy(id = id)
                    } else {
                        val conflicted = savedDao.getByPuzzle(record.puzzle)
                        if (conflicted != null && record.isFavorite && !conflicted.isFavorite) {
                            savedDao.setFavorite(conflicted.id, true)
                        }
                        savedSkipped++
                    }
                }
            }

            backup.challengeResults.forEach { record ->
                val id = challengeDao.insert(
                    ChallengeResultEntity(
                        challengeType = record.challengeType,
                        challengeKey = record.challengeKey,
                        difficulty = record.difficulty,
                        puzzle = record.puzzle,
                        elapsedSeconds = record.elapsedSeconds,
                        mistakes = record.mistakes,
                        hintsUsed = record.hintsUsed,
                        completedAtEpochMillis = record.completedAtEpochMillis,
                        perfect = record.perfect,
                    ),
                )
                if (id > 0L) challengesImported++ else challengesSkipped++
            }
        }

        preferences.restoreSettings(backup.settings)

        return BackupImportResult(
            historyImported = historyImported,
            historySkipped = historySkipped,
            savedPuzzlesImported = savedImported,
            savedPuzzlesSkipped = savedSkipped,
            challengesImported = challengesImported,
            challengesSkipped = challengesSkipped,
            settingsApplied = true,
        )
    }

"""
    path.write_text(text[:start] + new_method + text[end:])


def update_repository_test() -> None:
    path = Path("app/src/androidTest/java/com/sanskar/sudokunova/data/transfer/BackupRepositoryTest.kt")
    text = path.read_text()
    marker = """    @Test
    fun restoreAppliesValidatedSettings() = runBlocking {
"""
    new_test = """    @Test
    fun restorePromotesFavoritesOnNaturalDuplicatesWithoutDuplicatingRows() = runBlocking {
        val withoutFavorites = sampleBackup().copy(
            history = sampleBackup().history.map { it.copy(isFavorite = false) },
            savedPuzzles = sampleBackup().savedPuzzles.map { it.copy(isFavorite = false) },
        )

        repository.importBackup(withoutFavorites)
        val second = repository.importBackup(sampleBackup())

        assertEquals(0, second.historyImported)
        assertEquals(1, second.historySkipped)
        assertEquals(0, second.savedPuzzlesImported)
        assertEquals(1, second.savedPuzzlesSkipped)
        assertEquals(true, database.gameHistoryDao().observeAll().first().single().isFavorite)
        assertEquals(true, database.savedPuzzleDao().observeAll().first().single().isFavorite)
    }

"""
    if marker not in text:
        raise SystemExit("Expected repository test marker not found")
    path.write_text(text.replace(marker, new_test + marker))


def update_hardening_test() -> None:
    path = Path("app/src/test/java/com/sanskar/sudokunova/data/transfer/BackupCodecHardeningTest.kt")
    text = path.read_text()
    marker = """    @Test
    fun modifiedBackupWithInvalidChronologyIsRejected() {
"""
    new_test = """    @Test
    fun savedPuzzleTimestampBeyondSupportedRangeCannotBeEncoded() {
        val backup = SudokuNovaBackup(
            settings = UserSettings(),
            history = emptyList(),
            savedPuzzles = listOf(
                BackupSavedPuzzleRecord(
                    puzzle = puzzle,
                    solution = solution,
                    title = "Future puzzle",
                    difficulty = "EASY",
                    source = "custom",
                    createdAtEpochMillis = Long.MAX_VALUE,
                    isFavorite = false,
                ),
            ),
            challengeResults = emptyList(),
        )

        val result = runCatching { BackupCodec.encode(backup) }
        check(result.isFailure)
    }

"""
    if marker not in text:
        raise SystemExit("Expected hardening test marker not found")
    path.write_text(text.replace(marker, new_test + marker))


update_transfer_screen()
update_codec()
update_repository()
update_repository_test()
update_hardening_test()
