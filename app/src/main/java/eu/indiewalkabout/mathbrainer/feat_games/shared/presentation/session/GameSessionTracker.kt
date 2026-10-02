package eu.indiewalkabout.mathbrainer.feat_games.shared.presentation.session

import eu.indiewalkabout.mathbrainer.feat_statistics.domain.model.GameStats

data class GameSessionPersistRequest(
    val previousStats: GameStats?,
    val updatedStats: GameStats,
    val finalScore: Int
)

class GameSessionTracker(
    private val defaultGameId: String
) {
    private var currentGameId: String = defaultGameId
    private var previousStats: GameStats? = null
    private var isScorePersisted = false
    private var challengesPlayed = 0
    private var challengesWon = 0
    private var challengesLost = 0
    private var lastLevel = 1

    var highScore: Int = 0
        private set

    suspend fun loadStats(
        gameId: String,
        fallbackHighScore: Int,
        loadStats: suspend (String) -> GameStats?
    ) {
        val stats = loadStats(gameId)
        currentGameId = gameId
        previousStats = stats
        highScore = stats?.highScore ?: fallbackHighScore
        challengesPlayed = stats?.challengesPlayed ?: 0
        challengesWon = stats?.challengesWon ?: 0
        challengesLost = stats?.challengesLost ?: 0
        lastLevel = stats?.lastLevel?.takeIf { it > 0 } ?: 1
    }

    fun beginSession() {
        isScorePersisted = false
    }

    fun sessionHighScoreOr(fallbackHighScore: Int = 0): Int? {
        return highScore.takeIf { it > 0 } ?: fallbackHighScore.takeIf { it > 0 }
    }

    fun recordSuccess(newScore: Int, levelReached: Int) {
        challengesPlayed += 1
        challengesWon += 1
        highScore = maxOf(highScore, newScore)
        lastLevel = maxOf(lastLevel, levelReached)
    }

    fun recordFailure(levelReached: Int) {
        challengesPlayed += 1
        challengesLost += 1
        lastLevel = maxOf(lastLevel, levelReached)
    }

    fun recordProgress(levelReached: Int) {
        lastLevel = maxOf(lastLevel, levelReached)
    }

    fun buildPersistRequest(
        finalScore: Int,
        currentLevel: Int
    ): GameSessionPersistRequest? {
        if (isScorePersisted) return null
        isScorePersisted = true
        highScore = maxOf(highScore, finalScore)
        lastLevel = maxOf(lastLevel, currentLevel)

        val updatedStats = GameStats(
            gameId = currentGameId,
            highScore = highScore,
            challengesPlayed = challengesPlayed,
            challengesWon = challengesWon,
            challengesLost = challengesLost,
            lastLevel = lastLevel
        )
        val request = GameSessionPersistRequest(
            previousStats = previousStats,
            updatedStats = updatedStats,
            finalScore = finalScore
        )
        previousStats = updatedStats
        return request
    }
}
