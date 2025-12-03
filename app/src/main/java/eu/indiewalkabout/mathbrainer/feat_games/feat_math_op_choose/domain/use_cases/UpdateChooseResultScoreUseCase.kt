package eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_choose.domain.use_cases

import eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_choose.domain.model.ChooseResultScoreCategory
import eu.indiewalkabout.mathbrainer.feat_statistics.domain.model.GameScores.Companion.emptyScores
import eu.indiewalkabout.mathbrainer.feat_statistics.domain.repository.MathBrainerRepository
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import kotlin.math.max

class UpdateChooseResultScoreUseCase @Inject constructor(
    private val repository: MathBrainerRepository
) {
    suspend operator fun invoke(category: ChooseResultScoreCategory, sessionScore: Int) {
        val currentScores = repository.observeGameScores().firstOrNull() ?: emptyScores
        val updatedScores = when (category) {
            ChooseResultScoreCategory.SUM -> currentScores.copy(
                sum_choose_result_game_score = max(currentScores.sum_choose_result_game_score, sessionScore)
            )

            ChooseResultScoreCategory.DIFFERENCE -> currentScores.copy(
                diff_choose_result_game_score = max(currentScores.diff_choose_result_game_score, sessionScore)
            )

            ChooseResultScoreCategory.MULTIPLICATION -> currentScores.copy(
                mult_choose_result_game_score = max(currentScores.mult_choose_result_game_score, sessionScore)
            )

            ChooseResultScoreCategory.DIVISION -> currentScores.copy(
                div_choose_result_game_score = max(currentScores.div_choose_result_game_score, sessionScore)
            )

            ChooseResultScoreCategory.MIX -> currentScores.copy(
                mix_choose_result_game_score = max(currentScores.mix_choose_result_game_score, sessionScore)
            )
        }

        val finalScores = updatedScores.copy(
            id = 0,
            global_score = currentScores.global_score + sessionScore
        )
        repository.insertGameScores(finalScores)
    }
}