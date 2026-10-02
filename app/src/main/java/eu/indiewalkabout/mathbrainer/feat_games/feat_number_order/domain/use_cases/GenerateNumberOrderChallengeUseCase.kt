package eu.indiewalkabout.mathbrainer.feat_games.feat_number_order.domain.use_cases

import eu.indiewalkabout.mathbrainer.feat_games.feat_number_order.domain.model.NumberOrderChallenge
import eu.indiewalkabout.mathbrainer.feat_games.feat_number_order.domain.model.NumberOrderConfig
import javax.inject.Inject
import kotlin.math.ceil
import kotlin.random.Random

class GenerateNumberOrderChallengeUseCase @Inject constructor() {

    operator fun invoke(config: NumberOrderConfig): NumberOrderChallenge {
        val minItems = ceil(config.maxItemsToCount * config.minRatio).toInt().coerceAtLeast(1)
        val itemCount = Random.nextInt(minItems, config.maxItemsToCount.toInt() + 1)
        val placementSeed = Random.nextInt()
        return NumberOrderChallenge(
            itemCount = itemCount,
            placementSeed = placementSeed
        )
    }
}