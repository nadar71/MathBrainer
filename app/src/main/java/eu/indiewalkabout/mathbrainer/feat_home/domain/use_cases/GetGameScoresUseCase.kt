package eu.indiewalkabout.mathbrainer.feat_home.domain.use_cases

import eu.indiewalkabout.mathbrainer.feat_statistics.domain.model.GameScores
import eu.indiewalkabout.mathbrainer.feat_statistics.domain.repository.MathBrainerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetGameScoresUseCase @Inject constructor(
    private val repository: MathBrainerRepository
) {
    private val emptyScores = GameScores.emptyScores

    operator fun invoke(): Flow<GameScores> {
        return repository.observeGameScores()
            .map { scores ->
                if (scores == null) {
                    repository.insertGameScores(emptyScores)
                    emptyScores
                } else {
                    scores
                }
            }
            .catch {
                emit(emptyScores)
            }
    }
}
