package eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_choose.domain.use_cases

import eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_choose.domain.model.MathChooseChallenge
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_choose.domain.model.MathChooseConfig
import javax.inject.Inject
import kotlin.math.absoluteValue
import kotlin.random.Random

class GenerateMathChooseChallengeUseCase @Inject constructor() {
    operator fun invoke(config: MathChooseConfig, optionsCount: Int): MathChooseChallenge {
        val baseChallenge = when (config.operationCode) {
            "sum_choose" -> buildSum(config)
            "diff_choose" -> buildDifference(config)
            "mult_choose" -> buildMultiplication(config)
            "div_choose" -> buildDivision(config)
            else -> buildRandomOperation(config)
        }

        val options = buildOptions(baseChallenge.correctAnswer, optionsCount, config.optionOffset)
        return baseChallenge.copy(options = options)
    }

    private fun buildSum(config: MathChooseConfig): MathChooseChallenge {
        val first = Random.nextInt(config.min, config.max + 1)
        val second = Random.nextInt(config.min, config.max + 1)
        return MathChooseChallenge(first, second, '+', first + second, emptyList())
    }

    private fun buildDifference(config: MathChooseConfig): MathChooseChallenge {
        val first = Random.nextInt(config.min, config.max + 1)
        val second = Random.nextInt(config.min, first + 1)
        return MathChooseChallenge(first, second, '-', first - second, emptyList())
    }

    private fun buildMultiplication(config: MathChooseConfig): MathChooseChallenge {
        val highFirst = Random.nextInt(config.multMin, config.multHighMax + 1)
        val lowSecond = Random.nextInt(config.multMin, config.multLowMax + 1)
        return MathChooseChallenge(highFirst, lowSecond, '*', highFirst * lowSecond, emptyList())
    }

    private fun buildDivision(config: MathChooseConfig): MathChooseChallenge {
        val divisor = Random.nextInt(config.divMin, config.divLowMax + 1)
        val quotient = Random.nextInt(config.divMin, config.divHighMax + 1)
        val dividend = divisor * quotient
        return MathChooseChallenge(dividend, divisor, '/', quotient, emptyList())
    }

    private fun buildRandomOperation(config: MathChooseConfig): MathChooseChallenge {
        val operations = listOf(
            { buildSum(config) },
            { buildDifference(config) },
            { buildMultiplication(config) },
            { buildDivision(config) }
        )
        return operations.random()()
    }

    private fun buildOptions(correctAnswer: Int, optionsCount: Int, offset: Int): List<Int> {
        val answers = mutableSetOf(correctAnswer)
        while (answers.size < optionsCount) {
            val delta = Random.nextInt(-offset, offset + 1)
            val candidate = (correctAnswer + delta).absoluteValue
            if (candidate != correctAnswer) {
                answers.add(candidate)
            }
        }
        return answers.shuffled()
    }
}