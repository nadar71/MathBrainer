package eu.indiewalkabout.mathbrainer.feat_home.domain.use_cases

import android.util.Log
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_write.domain.model.MathWriteGameStats
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_write.domain.model.WriteResultScoreCategory
import eu.indiewalkabout.mathbrainer.feat_statistics.domain.repository.MathBrainerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetMathWriteGameStatsUseCase @Inject constructor(
    private val repository: MathBrainerRepository
) {
    private val supportedOperations = WriteResultScoreCategory.values().map { it.operationCode }

    suspend operator fun invoke(): Flow<Map<String, MathWriteGameStats>> {
        return repository.observeMathWriteGameStats()
            .map { stats ->
                val statsMap = stats.associateBy { it.operationId }.toMutableMap()
                val missingOperations = supportedOperations.filterNot { statsMap.containsKey(it) }
                missingOperations.forEach { operation ->
                    val defaultStats = MathWriteGameStats(operationId = operation)
                    repository.insertMathWriteGameStats(defaultStats)
                    statsMap[operation] = defaultStats
                }
                statsMap.toMap()
            }
            .catch { throwable ->
                Log.e("GetMathWriteGameStatsUseCase", "Error while getting math write stats", throwable)
                emit(supportedOperations.associateWith { MathWriteGameStats(operationId = it) })
            }
    }
}