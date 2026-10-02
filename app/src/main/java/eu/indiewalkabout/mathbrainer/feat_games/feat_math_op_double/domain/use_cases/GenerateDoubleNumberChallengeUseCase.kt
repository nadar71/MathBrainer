package eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_double.domain.use_cases

import eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_double.domain.model.DoubleNumberChallenge
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_double.domain.model.DoubleNumberConfig
import javax.inject.Inject
import kotlin.random.Random

class GenerateDoubleNumberChallengeUseCase @Inject constructor() {
    suspend operator fun invoke(config: DoubleNumberConfig): DoubleNumberChallenge {
        val value = Random.nextInt(config.min, config.max + 1)
        return DoubleNumberChallenge(value = value, answer = value * 2)
    }
}