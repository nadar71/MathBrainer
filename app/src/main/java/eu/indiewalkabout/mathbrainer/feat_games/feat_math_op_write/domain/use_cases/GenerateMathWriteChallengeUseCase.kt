package eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_write.domain.use_cases

import eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_write.domain.model.MathWriteChallenge
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_write.domain.model.MathWriteConfig
import javax.inject.Inject
import kotlin.random.Random

class GenerateMathWriteChallengeUseCase @Inject constructor() {
    suspend operator fun invoke(config: MathWriteConfig): MathWriteChallenge {
        return when (config.operationCode) {
            "sum_write"  -> buildSum(config)
            "diff_write" -> buildDifference(config)
            "mult_write" -> buildMultiplication(config)
            "div_write"  -> buildDivision(config)
            else -> buildRandomOperation(config)
        }
    }

    private fun buildSum(config: MathWriteConfig): MathWriteChallenge {
        val first = Random.nextInt(config.min, config.max + 1)
        val second = Random.nextInt(config.min, config.max + 1)
        return MathWriteChallenge(first, second, '+', first + second)
    }

    private fun buildDifference(config: MathWriteConfig): MathWriteChallenge {
        val first = Random.nextInt(config.min, config.max + 1)
        val second = Random.nextInt(config.min, first + 1)
        return MathWriteChallenge(first, second, '-', first - second)
    }

    private fun buildMultiplication(config: MathWriteConfig): MathWriteChallenge {
        val highFirst = Random.nextInt(config.multMin, config.multHighMax + 1)
        val lowSecond = Random.nextInt(config.multMin, config.multLowMax + 1)
        return MathWriteChallenge(highFirst, lowSecond, '*', highFirst * lowSecond)
    }

    private fun buildDivision(config: MathWriteConfig): MathWriteChallenge {
        val divisor = Random.nextInt(config.divMin, config.divLowMax + 1)
        val quotient = Random.nextInt(config.divMin, config.divHighMax + 1)
        val dividend = divisor * quotient
        return MathWriteChallenge(dividend, divisor, '/', quotient)
    }

    private fun buildRandomOperation(config: MathWriteConfig): MathWriteChallenge {
        val operations = listOf(
            { buildSum(config) },
            { buildDifference(config) },
            { buildMultiplication(config) },
            { buildDivision(config) }
        )
        return operations.random()()
    }

    /*private fun buildSum(config: MathWriteConfig, operation: Char): MathWriteChallenge {
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
    }*/
}

