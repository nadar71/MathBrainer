package eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_write.domain.usecase

import eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_write.domain.model.WriteResultScoreCategory
import eu.indiewalkabout.mathbrainer.feat_statistics.domain.model.GameScores.Companion.emptyScores
import eu.indiewalkabout.mathbrainer.feat_statistics.domain.repository.MathBrainerRepository
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import kotlin.math.max

class UpdateWriteResultScoreUseCase @Inject constructor(
    private val repository: MathBrainerRepository
) {
    suspend operator fun invoke(category: WriteResultScoreCategory, sessionScore: Int) {
        val currentScores = repository.observeGameScores().firstOrNull() ?: emptyScores
        val updatedScores = when (category) {
            WriteResultScoreCategory.SUM -> currentScores.copy(
                sum_write_result_game_score = max(currentScores.sum_write_result_game_score, sessionScore)
            )

            WriteResultScoreCategory.DIFFERENCE -> currentScores.copy(
                diff_write_result_game_score = max(currentScores.diff_write_result_game_score, sessionScore)
            )

            WriteResultScoreCategory.MULTIPLICATION -> currentScores.copy(
                mult_write_result_game_score = max(currentScores.mult_write_result_game_score, sessionScore)
            )

            WriteResultScoreCategory.DIVISION -> currentScores.copy(
                div_write_result_game_score = max(currentScores.div_write_result_game_score, sessionScore)
            )

            WriteResultScoreCategory.MIX -> currentScores.copy(
                mix_write_result_game_score = max(currentScores.mix_write_result_game_score, sessionScore)
            )
        }

        val finalScores = updatedScores.copy(
            id = 0,
            global_score = currentScores.global_score + sessionScore
        )
        repository.insertGameScores(finalScores)
    }

}