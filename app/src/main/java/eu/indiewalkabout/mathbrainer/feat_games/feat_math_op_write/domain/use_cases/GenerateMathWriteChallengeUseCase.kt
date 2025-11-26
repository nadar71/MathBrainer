package eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_write.domain.use_cases

import eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_write.domain.model.MathWriteChallenge
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_write.domain.model.MathWriteConfig
import javax.inject.Inject
import kotlin.random.Random

class GenerateMathWriteChallengeUseCase @Inject constructor() {
    suspend operator fun invoke(config: MathWriteConfig): MathWriteChallenge {
        val operation = config.symbols.random()
        return when (operation) {
            '+' -> buildSum(config, operation)
            '-' -> buildDifference(config, operation)
            '*' -> buildMultiplication(config, operation)
            '/' -> buildDivision(config, operation)
            else -> buildSum(config, '+')
        }
    }

    private fun buildSum(config: MathWriteConfig, operation: Char): MathWriteChallenge {
        val first = Random.nextInt(config.min, config.max + 1)
        val second = Random.nextInt(config.min, config.max + 1)
        return MathWriteChallenge(first, second, operation, first + second)
    }

    private fun buildDifference(config: MathWriteConfig, operation: Char): MathWriteChallenge {
        val first = Random.nextInt(config.min, config.max + 1)
        val second = Random.nextInt(config.min, first + 1)
        return MathWriteChallenge(first, second, operation, first - second)
    }

    private fun buildMultiplication(config: MathWriteConfig, operation: Char): MathWriteChallenge {
        val highFirst = Random.nextInt(config.multMin, config.multHighMax + 1)
        val lowSecond = Random.nextInt(config.multMin, config.multLowMax + 1)
        return MathWriteChallenge(highFirst, lowSecond, operation, highFirst * lowSecond)
    }

    private fun buildDivision(config: MathWriteConfig, operation: Char): MathWriteChallenge {
        val divisor = Random.nextInt(config.divMin, config.divLowMax + 1)
        val quotient = Random.nextInt(config.divMin, config.divHighMax + 1)
        val dividend = divisor * quotient
        return MathWriteChallenge(dividend, divisor, operation, quotient)
    }
}

