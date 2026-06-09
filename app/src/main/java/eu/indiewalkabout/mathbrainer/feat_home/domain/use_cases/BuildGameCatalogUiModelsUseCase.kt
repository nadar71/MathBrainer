package eu.indiewalkabout.mathbrainer.feat_home.domain.use_cases

import eu.indiewalkabout.mathbrainer.feat_home.domain.model.GameDefinition
import eu.indiewalkabout.mathbrainer.feat_home.domain.model.GameTypes
import eu.indiewalkabout.mathbrainer.feat_home.domain.model.GameUiModel
import eu.indiewalkabout.mathbrainer.feat_statistics.domain.model.GameScores
import eu.indiewalkabout.mathbrainer.feat_statistics.domain.model.GameStats
import javax.inject.Inject

class BuildGameCatalogUiModelsUseCase @Inject constructor() {

    operator fun invoke(
        definitions: List<GameDefinition>,
        scores: GameScores,
        stats: Map<String, GameStats>
    ): List<GameUiModel> {
        return definitions.map { definition ->
            GameUiModel(
                definition = definition,
                highScore = GameTypes.fromId(definition.id)?.scoreField?.invoke(scores),
                gameStats = stats[definition.id] ?: GameStats(gameId = definition.id)
            )
        }
    }
}
