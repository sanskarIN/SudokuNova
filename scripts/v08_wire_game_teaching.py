from pathlib import Path


def patch_view_model() -> None:
    path = Path("app/src/main/java/com/sanskar/sudokunova/ui/game/GameViewModel.kt")
    text = path.read_text()

    if "import com.sanskar.sudokunova.engine.TeachingHintSequence" not in text:
        text = text.replace(
            "import com.sanskar.sudokunova.engine.SudokuSolver\n",
            "import com.sanskar.sudokunova.engine.SudokuSolver\n"
            "import com.sanskar.sudokunova.engine.TeachingHintSequence\n",
        )

    state_marker = """    private val _pendingHint = MutableStateFlow<SudokuHint?>(null)
    val pendingHint: StateFlow<SudokuHint?> = _pendingHint.asStateFlow()
"""
    state_replacement = """    private val _pendingHint = MutableStateFlow<SudokuHint?>(null)
    val pendingHint: StateFlow<SudokuHint?> = _pendingHint.asStateFlow()

    private val _pendingTeachingHint = MutableStateFlow<TeachingHintSequence?>(null)
    val pendingTeachingHint: StateFlow<TeachingHintSequence?> = _pendingTeachingHint.asStateFlow()
"""
    if state_marker not in text:
        raise SystemExit("GameViewModel pending hint state marker not found")
    text = text.replace(state_marker, state_replacement)

    old_request = """    fun requestHint() {
        val state = currentGame() ?: return
        if (state.isPaused || state.status != GameStatus.PLAYING) return
        val hint = hintEngine.nextHint(state.board)
        _pendingHint.value = hint
        if (hint != null) {
            mutateGame { it.copy(selectedIndex = hint.cellIndex) }
        }
    }

    fun dismissHint() {
        _pendingHint.value = null
    }

    fun applyHint() {
        val hint = _pendingHint.value ?: return
        val state = currentGame() ?: return
        if (state.isOriginal(hint.cellIndex) || state.status != GameStatus.PLAYING) return
        pushUndo(state)
        _pendingHint.value = null
        placeValue(
            state = state.copy(hintsUsed = state.hintsUsed + 1),
            index = hint.cellIndex,
            value = hint.value,
            countMistake = false,
            alreadyAddedToUndo = true,
        )
    }
"""
    new_request = """    fun requestHint() {
        val state = currentGame() ?: return
        if (state.isPaused || state.status != GameStatus.PLAYING) return

        val teachingHint = hintEngine.nextTeachingHint(state.board)
        if (teachingHint != null) {
            _pendingTeachingHint.value = teachingHint
            _pendingHint.value = null
            mutateGame { it.copy(selectedIndex = teachingHint.placement.cellIndex) }
            return
        }

        val hint = hintEngine.nextHint(state.board)
        _pendingHint.value = hint
        _pendingTeachingHint.value = null
        if (hint != null) {
            mutateGame { it.copy(selectedIndex = hint.cellIndex) }
        }
    }

    fun dismissHint() {
        _pendingHint.value = null
        _pendingTeachingHint.value = null
    }

    fun applyHint() {
        val state = currentGame() ?: return
        val teachingPlacement = _pendingTeachingHint.value?.placement
        val legacyHint = _pendingHint.value
        val index = teachingPlacement?.cellIndex ?: legacyHint?.cellIndex ?: return
        val value = teachingPlacement?.value ?: legacyHint?.value ?: return

        if (state.isOriginal(index) || state.status != GameStatus.PLAYING) return
        pushUndo(state)
        _pendingHint.value = null
        _pendingTeachingHint.value = null
        placeValue(
            state = state.copy(hintsUsed = state.hintsUsed + 1),
            index = index,
            value = value,
            countMistake = false,
            alreadyAddedToUndo = true,
        )
    }
"""
    if old_request not in text:
        raise SystemExit("GameViewModel request/apply hint block not found")
    text = text.replace(old_request, new_request)

    restart_marker = """    fun restart() {
        val state = currentGame() ?: return
        pushUndo(state)
        completionRecorded = false
"""
    restart_replacement = """    fun restart() {
        val state = currentGame() ?: return
        pushUndo(state)
        completionRecorded = false
        _pendingHint.value = null
        _pendingTeachingHint.value = null
"""
    if restart_marker not in text:
        raise SystemExit("GameViewModel restart marker not found")
    text = text.replace(restart_marker, restart_replacement)

    abandon_marker = """        _pendingHint.value = null
        if (state.status == GameStatus.COMPLETED) completionRecorded = true
"""
    abandon_replacement = """        _pendingHint.value = null
        _pendingTeachingHint.value = null
        if (state.status == GameStatus.COMPLETED) completionRecorded = true
"""
    if abandon_marker not in text:
        raise SystemExit("GameViewModel abandon marker not found")
    text = text.replace(abandon_marker, abandon_replacement)

    path.write_text(text)


def patch_game_screen() -> None:
    path = Path("app/src/main/java/com/sanskar/sudokunova/ui/game/GameScreen.kt")
    text = path.read_text()

    if "import com.sanskar.sudokunova.ui.common.localizedTeachingExplanation" not in text:
        text = text.replace(
            "import com.sanskar.sudokunova.ui.common.localizedDifficultyLabel\n",
            "import com.sanskar.sudokunova.ui.common.localizedDifficultyLabel\n"
            "import com.sanskar.sudokunova.ui.common.localizedTeachingExplanation\n"
            "import com.sanskar.sudokunova.ui.common.localizedTeachingTechnique\n",
        )

    collect_marker = """    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val hint by viewModel.pendingHint.collectAsStateWithLifecycle()
"""
    collect_replacement = """    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val hint by viewModel.pendingHint.collectAsStateWithLifecycle()
    val teachingHint by viewModel.pendingTeachingHint.collectAsStateWithLifecycle()
"""
    if collect_marker not in text:
        raise SystemExit("GameScreen hint collection marker not found")
    text = text.replace(collect_marker, collect_replacement)

    old_dialog = """    if (hint != null) {
        AlertDialog(
            onDismissRequest = viewModel::dismissHint,
            title = { Text(hint!!.technique.displayName) },
            text = { Text(hint!!.explanation) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.applyHint()
                        playFeedback()
                    },
                ) { Text(stringResource(R.string.v04_apply_hint)) }
            },
            dismissButton = {
                TextButton(onClick = viewModel::dismissHint) { Text(stringResource(R.string.v04_not_now)) }
            },
        )
    }
"""
    new_dialog = """    if (teachingHint != null) {
        val sequence = teachingHint!!
        AlertDialog(
            onDismissRequest = viewModel::dismissHint,
            title = { Text(stringResource(R.string.v08_teaching_hint)) },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    sequence.steps.forEach { step ->
                        Text(
                            localizedTeachingTechnique(step.technique),
                            style = MaterialTheme.typography.titleSmall,
                        )
                        Text(
                            localizedTeachingExplanation(step),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.applyHint()
                        playFeedback()
                    },
                ) { Text(stringResource(R.string.v04_apply_hint)) }
            },
            dismissButton = {
                TextButton(onClick = viewModel::dismissHint) { Text(stringResource(R.string.v04_not_now)) }
            },
        )
    } else if (hint != null) {
        val reveal = hint!!
        val revealRow = reveal.cellIndex / 9 + 1
        val revealColumn = reveal.cellIndex % 9 + 1
        AlertDialog(
            onDismissRequest = viewModel::dismissHint,
            title = { Text(stringResource(R.string.v08_technique_reveal)) },
            text = {
                Text(
                    stringResource(
                        R.string.v08_explain_reveal,
                        revealRow,
                        revealColumn,
                        reveal.value,
                    ),
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.applyHint()
                        playFeedback()
                    },
                ) { Text(stringResource(R.string.v04_apply_hint)) }
            },
            dismissButton = {
                TextButton(onClick = viewModel::dismissHint) { Text(stringResource(R.string.v04_not_now)) }
            },
        )
    }
"""
    if old_dialog not in text:
        raise SystemExit("GameScreen legacy hint dialog block not found")
    text = text.replace(old_dialog, new_dialog)
    path.write_text(text)


patch_view_model()
patch_game_screen()
