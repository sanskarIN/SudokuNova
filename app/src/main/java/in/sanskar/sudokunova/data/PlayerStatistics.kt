package in.sanskar.sudokunova.data

data class PlayerStatistics(
    val gamesStarted: Int = 0,
    val gamesCompleted: Int = 0,
    val gamesAbandoned: Int = 0,
    val totalPlaySeconds: Long = 0,
    val bestTimeSeconds: Long? = null,
    val totalMistakes: Int = 0,
    val totalHints: Int = 0,
    val perfectGames: Int = 0,
    val noHintGames: Int = 0,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
) {
    val completionRate: Int
        get() = if (gamesStarted == 0) 0 else ((gamesCompleted * 100.0) / gamesStarted).toInt()
}
