package eu.indiewalkabout.mathbrainer.feat_home.domain.use_cases

import android.util.Log
import eu.indiewalkabout.mathbrainer.feat_statistics.domain.model.GameScores
import eu.indiewalkabout.mathbrainer.feat_statistics.domain.repository.MathBrainerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetGameScoresUseCase @Inject constructor(
    private val repository: MathBrainerRepository
) {
    private val emptyScores = GameScores(
        global_score = 0,
        doublenumber_game_score = 0,
        sum_choose_result_game_score = 0,
        diff_choose_result_game_score = 0,
        mult_choose_result_game_score = 0,
        div_choose_result_game_score = 0,
        mix_choose_result_game_score = 0,
        sum_write_result_game_score = 0,
        diff_write_result_game_score = 0,
        mult_write_result_game_score = 0,
        div_write_result_game_score = 0,
        mix_write_result_game_score = 0,
        random_op_game_score = 0,
        count_objects_game_score = 0,
        number_order_game_score = 0
    )

    suspend operator fun invoke(): Flow<GameScores> {
        Log.d("GetGameScoresUseCase", "Starting to observe game scores")
        return repository.observeGameScores()
            .map { scores ->
                if (scores == null) {
                    Log.d("GetGameScoresUseCase", "No scores found in database, initializing with empty scores")
                    repository.insertGameScores(emptyScores)
                    emptyScores
                } else {
                    Log.d("GetGameScoresUseCase", "Retrieved scores from database: $scores")
                    scores
                }
            }
            .catch { e ->
                Log.e("GetGameScoresUseCase", "Error while getting game scores", e)
                emit(emptyScores)
            }
    }
}
