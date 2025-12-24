package eu.indiewalkabout.mathbrainer.feat_games.feat_math_random_operation.domain.use_cases

import eu.indiewalkabout.mathbrainer.feat_games.feat_math_random_operation.domain.model.ExpressionGrouping
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_random_operation.domain.model.RandomOperationChallenge
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_random_operation.domain.model.RandomOperationConfig
import javax.inject.Inject
import kotlin.random.Random

class GenerateRandomOperationChallengeUseCase @Inject constructor() {
    suspend operator fun invoke(config: RandomOperationConfig): RandomOperationChallenge {
        val useMultiStep = shouldUseMultiStep(config.level)
        return if (useMultiStep) {
            buildMultiStep(config)
        } else {
            buildSimple(config)
        }
    }

    private fun buildSimple(config: RandomOperationConfig): RandomOperationChallenge {
        val operation = OPERATIONS.random()
        val operationResult = buildBinaryOperation(config, operation)
        return RandomOperationChallenge(
            operands = listOf(operationResult.leftOperand, operationResult.rightOperand),
            operations = listOf(operation),
            hiddenOperationIndex = 0,
            result = operationResult.result,
            grouping = ExpressionGrouping.NONE
        )
    }

    private fun buildMultiStep(config: RandomOperationConfig): RandomOperationChallenge {
        repeat(MAX_ATTEMPTS) {
            val grouping = if (Random.nextBoolean()) ExpressionGrouping.LEFT else ExpressionGrouping.RIGHT
            val op1 = OPERATIONS.random()
            val op2 = OPERATIONS.random()
            val hiddenIndex = if (config.level >= 4) Random.nextInt(0, 2) else 0
            if (grouping == ExpressionGrouping.LEFT) {
                val inner = buildBinaryOperation(config, op1)
                val outer = buildOuterWithLeftOperand(inner.result, op2, config) ?: return@repeat
                return RandomOperationChallenge(
                    operands = listOf(inner.leftOperand, inner.rightOperand, outer.otherOperand),
                    operations = listOf(op1, op2),
                    hiddenOperationIndex = hiddenIndex,
                    result = outer.result,
                    grouping = grouping
                )
            } else {
                val inner = buildBinaryOperation(config, op2)
                val outer = buildOuterWithRightOperand(inner.result, op1, config) ?: return@repeat
                return RandomOperationChallenge(
                    operands = listOf(outer.otherOperand, inner.leftOperand, inner.rightOperand),
                    operations = listOf(op1, op2),
                    hiddenOperationIndex = hiddenIndex,
                    result = outer.result,
                    grouping = grouping
                )
            }
        }

        return buildSimple(config)
    }

    private fun shouldUseMultiStep(level: Int): Boolean {
        val multiStepChance = when {
            level <= 2 -> 0.0
            level <= 4 -> 0.35
            level <= 6 -> 0.6
            else -> 0.8
        }
        return Random.nextDouble() < multiStepChance
    }

    private fun buildBinaryOperation(config: RandomOperationConfig, operation: Char): OperationResult {
        return when (operation) {
            '+' -> buildSum(config)
            '-' -> buildDifference(config)
            '*' -> buildMultiplication(config)
            '/' -> buildDivision(config)
            else -> buildSum(config)
        }
    }

    private fun buildSum(config: RandomOperationConfig): OperationResult {
        val first = Random.nextInt(config.min, config.max + 1)
        val second = Random.nextInt(config.min, config.max + 1)
        return OperationResult(first, second, first + second)
    }

    private fun buildDifference(config: RandomOperationConfig): OperationResult {
        val first = Random.nextInt(config.min, config.max + 1)
        val second = Random.nextInt(config.min, first + 1)
        return OperationResult(first, second, first - second)
    }

    private fun buildMultiplication(config: RandomOperationConfig): OperationResult {
        val highFirst = Random.nextInt(config.multMin, config.multHighMax + 1)
        val lowSecond = Random.nextInt(config.multMin, config.multLowMax + 1)
        return OperationResult(highFirst, lowSecond, highFirst * lowSecond)
    }

    private fun buildDivision(config: RandomOperationConfig): OperationResult {
        val divisor = Random.nextInt(config.divMin, config.divLowMax + 1)
        val quotient = Random.nextInt(config.divMin, config.divHighMax + 1)
        val dividend = divisor * quotient
        return OperationResult(dividend, divisor, quotient)
    }

    private fun buildOuterWithLeftOperand(
        leftOperand: Int,
        operation: Char,
        config: RandomOperationConfig
    ): OuterOperationResult? {
        return when (operation) {
            '+' -> {
                val right = Random.nextInt(config.min, config.max + 1)
                OuterOperationResult(right, leftOperand + right)
            }

            '-' -> {
                if (leftOperand < config.min) return null
                val maxRight = minOf(config.max, leftOperand)
                if (maxRight < config.min) return null
                val right = Random.nextInt(config.min, maxRight + 1)
                OuterOperationResult(right, leftOperand - right)
            }

            '*' -> {
                val right = Random.nextInt(config.multMin, config.multLowMax + 1)
                OuterOperationResult(right, leftOperand * right)
            }

            '/' -> {
                val divisor = pickDivisor(leftOperand, config) ?: return null
                OuterOperationResult(divisor, leftOperand / divisor)
            }

            else -> null
        }
    }

    private fun buildOuterWithRightOperand(
        rightOperand: Int,
        operation: Char,
        config: RandomOperationConfig
    ): OuterOperationResult? {
        return when (operation) {
            '+' -> {
                val left = Random.nextInt(config.min, config.max + 1)
                OuterOperationResult(left, left + rightOperand)
            }

            '-' -> {
                val delta = Random.nextInt(config.min, config.max + 1)
                val left = rightOperand + delta
                OuterOperationResult(left, left - rightOperand)
            }

            '*' -> {
                val left = Random.nextInt(config.multMin, config.multLowMax + 1)
                OuterOperationResult(left, left * rightOperand)
            }

            '/' -> {
                val quotient = Random.nextInt(config.divMin, config.divHighMax + 1)
                val left = rightOperand * quotient
                OuterOperationResult(left, left / rightOperand)
            }

            else -> null
        }
    }

    private fun pickDivisor(value: Int, config: RandomOperationConfig): Int? {
        if (value == 0) return null
        val candidates = (config.divMin..config.divLowMax).filter { divisor -> value % divisor == 0 }
        return candidates.randomOrNull()
    }

    companion object {
        private val OPERATIONS = listOf('+', '-', '*', '/')
        private const val MAX_ATTEMPTS = 25
    }

    private data class OperationResult(
        val leftOperand: Int,
        val rightOperand: Int,
        val result: Int
    )

    private data class OuterOperationResult(
        val otherOperand: Int,
        val result: Int
    )
}
