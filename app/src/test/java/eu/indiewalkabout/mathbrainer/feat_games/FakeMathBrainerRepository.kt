package eu.indiewalkabout.mathbrainer.feat_games

import eu.indiewalkabout.mathbrainer.feat_statistics.domain.model.GameScores
import eu.indiewalkabout.mathbrainer.feat_statistics.domain.model.GameStatistics
import eu.indiewalkabout.mathbrainer.feat_statistics.domain.model.GameStats
import eu.indiewalkabout.mathbrainer.feat_statistics.domain.repository.MathBrainerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeMathBrainerRepository(
    initialScores: GameScores = GameScores.emptyScores,
    initialStats: List<GameStats> = emptyList()
) : MathBrainerRepository {

    private val scoresFlow = MutableStateFlow<GameScores?>(initialScores)
    private val statsFlow = MutableStateFlow(initialStats)

    var insertedScoresCount = 0
        private set

    var insertedStatsCount = 0
        private set

    override fun observeGameScores(): Flow<GameScores?> = scoresFlow

    override fun observeGameStats(): Flow<List<GameStats>> = statsFlow

    override suspend fun getGameStats(gameId: String): GameStats? {
        return statsFlow.value.firstOrNull { it.gameId == gameId }
    }

    override suspend fun loadGameStatistics(): GameStatistics {
        return GameStatistics(
            operations_executed = 0,
            operations_ok = 0,
            operations_ko = 0,
            sums = 0,
            differences = 0,
            multiplications = 0,
            divisions = 0,
            doublings = 0,
            level_upgrades = 0,
            lives_missed = 0,
            objects_counted = 0,
            numbers_in_order = 0,
            games_played = 0,
            games_lose = 0
        )
    }

    override suspend fun insertGameScores(gameScores: GameScores) {
        insertedScoresCount += 1
        scoresFlow.value = gameScores
    }

    override suspend fun insertGameStatistics(gameStatistics: GameStatistics) = Unit

    override suspend fun insertGameStats(gameStats: GameStats) {
        insertedStatsCount += 1
        statsFlow.value = statsFlow.value
            .filterNot { it.gameId == gameStats.gameId } + gameStats
    }

    override suspend fun dropTableGameScores() {
        scoresFlow.value = null
    }

    override suspend fun dropTableGameStatistics() = Unit

    override suspend fun dropTableGameStats() {
        statsFlow.value = emptyList()
    }
}
