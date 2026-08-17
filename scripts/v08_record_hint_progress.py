from pathlib import Path

path = Path("app/src/main/java/com/sanskar/sudokunova/ui/game/GameViewModel.kt")
text = path.read_text()

if "import com.sanskar.sudokunova.data.learning.LearningProgressRepository" not in text:
    text = text.replace(
        "import com.sanskar.sudokunova.data.UserSettings\n",
        "import com.sanskar.sudokunova.data.UserSettings\n"
        "import com.sanskar.sudokunova.data.learning.LearningProgressRepository\n",
    )

repo_marker = """    private val historyRepository = HistoryRepository(application.applicationContext)
    private val challengeRepository = ChallengeRepository(application.applicationContext)
"""
repo_replacement = """    private val historyRepository = HistoryRepository(application.applicationContext)
    private val challengeRepository = ChallengeRepository(application.applicationContext)
    private val learningProgressRepository = LearningProgressRepository(application.applicationContext)
"""
if repo_marker not in text:
    raise SystemExit("GameViewModel repository marker not found")
text = text.replace(repo_marker, repo_replacement)

hint_marker = """        if (teachingHint != null) {
            _pendingTeachingHint.value = teachingHint
            _pendingHint.value = null
            mutateGame { it.copy(selectedIndex = teachingHint.placement.cellIndex) }
            return
        }
"""
hint_replacement = """        if (teachingHint != null) {
            _pendingTeachingHint.value = teachingHint
            _pendingHint.value = null
            viewModelScope.launch {
                teachingHint.techniques.toSet().forEach { technique ->
                    learningProgressRepository.recordHintViewed(technique)
                }
            }
            mutateGame { it.copy(selectedIndex = teachingHint.placement.cellIndex) }
            return
        }
"""
if hint_marker not in text:
    raise SystemExit("GameViewModel teaching hint marker not found")
text = text.replace(hint_marker, hint_replacement)

path.write_text(text)
