package eu.indiewalkabout.mathbrainer.feat_games.feat_falling_op.domain.use_cases

import eu.indiewalkabout.mathbrainer.feat_games.feat_falling_op.domain.model.FallingOperationDefinition
import javax.inject.Inject
import kotlin.math.max
import kotlin.random.Random

class GenerateFallingOperationUseCase @Inject constructor() {
    operator fun invoke(level: Int, maxOperand: Int): FallingOperationDefinition {
        val operators = when {
            level <= 1 -> listOf('+')
            level == 2 -> listOf('+', '-')
            level == 3 -> listOf('+', '-', '*')
            else -> listOf('+', '-', '*', '/')
        }
        val operator = operators.random()
        val maxValue = max(1, maxOperand)

        return when (operator) {
            '+' -> {
                val left = Random.nextInt(1, maxValue + 1)
                val right = Random.nextInt(1, maxValue + 1)
                FallingOperationDefinition(left, right, operator, left + right)
            }

            '-' -> {
                val left = Random.nextInt(1, maxValue + 1)
                val right = Random.nextInt(1, left + 1)
                FallingOperationDefinition(left, right, operator, left - right)
            }

            '*' -> {
                val left = Random.nextInt(1, maxValue + 1)
                val right = Random.nextInt(1, maxValue + 1)
                FallingOperationDefinition(left, right, operator, left * right)
            }

            '/' -> {
                val divisor = Random.nextInt(1, maxValue + 1)
                val maxQuotient = max(1, maxValue / divisor)
                val quotient = Random.nextInt(1, maxQuotient + 1)
                val dividend = divisor * quotient
                FallingOperationDefinition(dividend, divisor, operator, quotient)
            }

            else -> {
                val left = Random.nextInt(1, maxValue + 1)
                val right = Random.nextInt(1, maxValue + 1)
                FallingOperationDefinition(left, right, '+', left + right)
            }
        }
    }
}
