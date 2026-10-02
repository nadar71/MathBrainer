package eu.indiewalkabout.mathbrainer.feat_games.feat_math_random_operation.domain.use_cases

import eu.indiewalkabout.mathbrainer.feat_games.feat_math_random_operation.domain.model.RandomOperationChallenge
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_random_operation.domain.model.RandomOperationConfig
import javax.inject.Inject
import kotlin.random.Random

class GenerateRandomOperationChallengeUseCase @Inject constructor() {
    suspend operator fun invoke(config: RandomOperationConfig): RandomOperationChallenge {
        val operation = OPERATIONS.random()
        return when (operation) {
            '+' -> buildSum(config)
            '-' -> buildDifference(config)
            '*' -> buildMultiplication(config)
            '/' -> buildDivision(config)
            else -> buildSum(config)
        }
    }

    private fun buildSum(config: RandomOperationConfig): RandomOperationChallenge {
        val first = Random.nextInt(config.min, config.max + 1)
        val second = Random.nextInt(config.min, config.max + 1)
        return RandomOperationChallenge(first, second, first + second, '+')
    }

    private fun buildDifference(config: RandomOperationConfig): RandomOperationChallenge {
        val first = Random.nextInt(config.min, config.max + 1)
        val second = Random.nextInt(config.min, first + 1)
        return RandomOperationChallenge(first, second, first - second, '-')
    }

    private fun buildMultiplication(config: RandomOperationConfig): RandomOperationChallenge {
        val highFirst = Random.nextInt(config.multMin, config.multHighMax + 1)
        val lowSecond = Random.nextInt(config.multMin, config.multLowMax + 1)
        return RandomOperationChallenge(highFirst, lowSecond, highFirst * lowSecond, '*')
    }

    private fun buildDivision(config: RandomOperationConfig): RandomOperationChallenge {
        val divisor = Random.nextInt(config.divMin, config.divLowMax + 1)
        val quotient = Random.nextInt(config.divMin, config.divHighMax + 1)
        val dividend = divisor * quotient
        return RandomOperationChallenge(dividend, divisor, quotient, '/')
    }

    companion object {
        private val OPERATIONS = listOf('+', '-', '*', '/')
    }
}