from pathlib import Path


def update_models() -> None:
    path = Path("app/src/main/java/com/sanskar/sudokunova/data/transfer/BackupModels.kt")
    text = path.read_text()
    old = """    val isDailyChallenge: Boolean,
    val isPerfect: Boolean,
    val isFavorite: Boolean,
)
"""
    new = """    val isDailyChallenge: Boolean,
    val isPerfect: Boolean,
    val isFavorite: Boolean,
    val isReplay: Boolean = false,
)
"""
    if old not in text:
        raise SystemExit("BackupHistoryRecord marker not found")
    path.write_text(text.replace(old, new))


def update_codec() -> None:
    path = Path("app/src/main/java/com/sanskar/sudokunova/data/transfer/BackupCodec.kt")
    text = path.read_text()
    old_encode = """            record.isDailyChallenge.toString(),
            record.isPerfect.toString(),
            record.isFavorite.toString(),
        ).joinToString("|")
"""
    new_encode = """            record.isDailyChallenge.toString(),
            record.isPerfect.toString(),
            record.isFavorite.toString(),
            record.isReplay.toString(),
        ).joinToString("|")
"""
    if old_encode not in text:
        raise SystemExit("History encode marker not found")
    text = text.replace(old_encode, new_encode)
    text = text.replace("        require(fields.size == 13)\n", "        require(fields.size == 14)\n", 1)
    old_ctor = """            isDailyChallenge = fields[10].toBooleanStrict(),
            isPerfect = perfect,
            isFavorite = fields[12].toBooleanStrict(),
        )
"""
    new_ctor = """            isDailyChallenge = fields[10].toBooleanStrict(),
            isPerfect = perfect,
            isFavorite = fields[12].toBooleanStrict(),
            isReplay = fields[13].toBooleanStrict(),
        )
"""
    if old_ctor not in text:
        raise SystemExit("History decode constructor marker not found")
    path.write_text(text.replace(old_ctor, new_ctor))


def update_repository() -> None:
    path = Path("app/src/main/java/com/sanskar/sudokunova/data/transfer/BackupRepository.kt")
    text = path.read_text()
    if "private const val IMPORTED_REPLAY_SENTINEL" not in text:
        text = text.replace(
            "import kotlinx.coroutines.flow.first\n\n",
            "import kotlinx.coroutines.flow.first\n\nprivate const val IMPORTED_REPLAY_SENTINEL = 0L\n\n",
        )
    old_export = """                isDailyChallenge = entity.isDailyChallenge,
                isPerfect = entity.isPerfect,
                isFavorite = entity.isFavorite,
            )
"""
    new_export = """                isDailyChallenge = entity.isDailyChallenge,
                isPerfect = entity.isPerfect,
                isFavorite = entity.isFavorite,
                isReplay = entity.replayOfHistoryId != null,
            )
"""
    if old_export not in text:
        raise SystemExit("History export marker not found")
    text = text.replace(old_export, new_export)
    text = text.replace(
        "                    replayOfHistoryId = null,\n",
        "                    replayOfHistoryId = if (record.isReplay) IMPORTED_REPLAY_SENTINEL else null,\n",
        1,
    )
    old_signature = """        entity.isDailyChallenge,
        entity.isPerfect,
    ).joinToString("|")
"""
    new_signature = """        entity.isDailyChallenge,
        entity.isPerfect,
        entity.replayOfHistoryId != null,
    ).joinToString("|")
"""
    if old_signature not in text:
        raise SystemExit("History signature marker not found")
    path.write_text(text.replace(old_signature, new_signature))


def update_codec_test() -> None:
    path = Path("app/src/test/java/com/sanskar/sudokunova/data/transfer/BackupCodecTest.kt")
    text = path.read_text()
    old = """                isDailyChallenge = false,
                isPerfect = false,
                isFavorite = true,
            ),
"""
    new = """                isDailyChallenge = false,
                isPerfect = false,
                isFavorite = true,
                isReplay = true,
            ),
"""
    if old not in text:
        raise SystemExit("Codec sample history marker not found")
    path.write_text(text.replace(old, new, 1))


def update_repository_test() -> None:
    path = Path("app/src/androidTest/java/com/sanskar/sudokunova/data/transfer/BackupRepositoryTest.kt")
    text = path.read_text()
    marker = """    @Test
    fun restoreAppliesValidatedSettings() = runBlocking {
"""
    new_test = """    @Test
    fun restorePreservesReplayProvenanceAndExcludesReplayFromSummaries() = runBlocking {
        val replayBackup = sampleBackup().copy(
            history = sampleBackup().history.map { it.copy(isReplay = true) },
            savedPuzzles = emptyList(),
            challengeResults = emptyList(),
        )

        repository.importBackup(replayBackup)

        val restored = database.gameHistoryDao().observeAll().first().single()
        assertEquals(true, restored.replayOfHistoryId != null)
        assertEquals(true, database.gameHistoryDao().observeDifficultySummaries().first().isEmpty())
    }

"""
    if marker not in text:
        raise SystemExit("Repository test insertion marker not found")
    path.write_text(text.replace(marker, new_test + marker))


def update_docs() -> None:
    path = Path("docs/TRANSFER_BACKUP_V07.md")
    text = path.read_text()
    text = text.replace(
        "- replay source IDs,\n",
        "- replay source IDs (only a replay/non-replay provenance flag is preserved),\n",
    )
    old = """- exact/natural duplicate history records are skipped,
- duplicate saved puzzles are skipped by the existing unique-puzzle constraint,
"""
    new = """- exact/natural duplicate history records are skipped while backed-up Favorite state can promote an existing record,
- replay/non-replay provenance is preserved without trusting or restoring exported source IDs,
- duplicate saved puzzles are skipped by the existing unique-puzzle constraint while backed-up Favorite state can promote an existing puzzle,
"""
    if old not in text:
        raise SystemExit("Restore behavior docs marker not found")
    path.write_text(text.replace(old, new))


update_models()
update_codec()
update_repository()
update_codec_test()
update_repository_test()
update_docs()
