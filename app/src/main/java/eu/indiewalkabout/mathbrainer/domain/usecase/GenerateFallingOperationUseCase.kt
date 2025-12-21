package eu.indiewalkabout.mathbrainer.domain.usecase

import kotlin.math.min
import kotlin.random.Random

class GenerateFallingOperationUseCase(
    private val random: Random = Random
) {
    data class OperationData(
        val expression: String,
        val result: Int,
        val operator: Char
    )

    fun execute(level: Int): OperationData {
        val maxOperand = maxOperandForLevel(level)
        val operator = OPERATORS[random.nextInt(OPERATORS.size)]

        return when (operator) {
            '+' -> {
                val left = randomOperand(maxOperand)
                val right = randomOperand(maxOperand)
                OperationData("$left + $right", left + right, operator)
            }
            '-' -> {
                val left = randomOperand(maxOperand)
                val right = randomOperand(left.coerceAtLeast(1))
                OperationData("$left - $right", left - right, operator)
            }
            '*' -> {
                val left = randomOperand(maxOperand)
                val right = randomOperand(maxOperand)
                OperationData("$left × $right", left * right, operator)
            }
            '/' -> {
                val divisor = randomOperand(maxOperand.coerceAtLeast(1))
                val quotient = randomOperand(maxOperand)
                val dividend = divisor * quotient
                OperationData("$dividend ÷ $divisor", quotient, operator)
            }
            else -> {
                val left = randomOperand(maxOperand)
                val right = randomOperand(maxOperand)
                OperationData("$left + $right", left + right, '+')
            }
        }
    }

    fun requiredExplosionsForLevel(level: Int): Int {
        return 6 + (level - 1) * 2
    }

    fun fallSpeedForLevel(level: Int): Float {
        return min(3.5f + level * 0.55f, 12f)
    }

    fun spawnIntervalForLevel(level: Int): Long {
        return maxOf(350L, 1200L - level * 60L)
    }

    fun maxOperandForLevel(level: Int): Int {
        return min(9 + level * 4, 99)
    }

    private fun randomOperand(maxOperand: Int): Int {
        return random.nextInt(1, maxOperand + 1)
    }

    companion object {
        private val OPERATORS = charArrayOf('+', '-', '*', '/')
    }
}
