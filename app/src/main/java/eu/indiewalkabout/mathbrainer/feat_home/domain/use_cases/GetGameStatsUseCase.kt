package eu.indiewalkabout.mathbrainer.feat_home.domain.use_cases

import eu.indiewalkabout.mathbrainer.feat_home.domain.model.GameTypes
import eu.indiewalkabout.mathbrainer.feat_statistics.domain.model.GameStats
import eu.indiewalkabout.mathbrainer.feat_statistics.domain.repository.MathBrainerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetGameStatsUseCase @Inject constructor(
    private val repository: MathBrainerRepository
) {
    private val supportedGameIds = GameTypes.entries.map { it.id }

    operator fun invoke(): Flow<Map<String, GameStats>> {
        return repository.observeGameStats()
            .map { stats ->
                val statsMap = stats.associateBy { it.gameId }.toMutableMap()
                val missingGames = supportedGameIds.filterNot { statsMap.containsKey(it) }
                missingGames.forEach { gameId ->
                    val defaultStats = GameStats(gameId = gameId)
                    repository.insertGameStats(defaultStats)
                    statsMap[gameId] = defaultStats
                }
                statsMap.toMap()
            }
            .catch {
                emit(supportedGameIds.associateWith { GameStats(gameId = it) })
            }
    }
}
