package eu.indiewalkabout.mathbrainer.feat_games.feat_sequence_complete

import kotlin.random.Random

// Data classes and enums
data class SequenceConfig(
    val level: Int,
    val minStart: Int,
    val maxStart: Int,
    val maxStepMagnitude: Int,
    val length: Int,
    val maxValue: Int = 999
)

enum class SequenceOperation(val symbol: String) {
    ADD("+"),
    SUBTRACT("-"),
    MULTIPLY("×")
}

data class SequenceRuleStep(
    val operation: SequenceOperation,
    val value: Int,
    val alternateOperation: SequenceOperation? = null,
    val alternateValue: Int? = null
) {
    fun apply(to: Int, position: Int): Int {
        val useAlternate = alternateOperation != null && position % 2 == 1
        return when (if (useAlternate) alternateOperation else operation) {
            SequenceOperation.ADD -> to + (if (useAlternate) alternateValue!! else value)
            SequenceOperation.SUBTRACT -> to - (if (useAlternate) alternateValue!! else value)
            SequenceOperation.MULTIPLY -> to * (if (useAlternate) alternateValue!! else value)
            else -> to
        }
    }

    fun describe(): String {
        return if (alternateOperation != null && alternateValue != null) {
            "${operation.symbol}$value, ${alternateOperation.symbol}$alternateValue (alternating)"
        } else {
            "${operation.symbol}$value"
        }
    }
}

data class SequenceChallenge(
    val displaySequence: List<Int?>,
    val fullSequence: List<Int>,
    val ruleSteps: List<SequenceRuleStep>,
    val ruleDescription: String,
    val missingPosition: Int
) {
    val answer: Int = fullSequence[missingPosition]
}

// Main generator class
private class GenerateSequenceChallengeUseCase {
    private val MAX_ATTEMPTS = 50
    private val MAX_MULTIPLIER = 7

    operator fun invoke(config: SequenceConfig): SequenceChallenge {
        repeat(MAX_ATTEMPTS) {
            val steps = buildSteps(config)
            val start = Random.nextInt(config.minStart, config.maxStart.coerceAtLeast(config.minStart) + 1)
            val numbers = mutableListOf(start)

            var isValid = true
            while (numbers.size < config.length && isValid) {
                val step = steps[(numbers.size - 1) % steps.size]
                val next = step.apply(numbers.last(), numbers.size - 1)
                if (next <= 0 || next > config.maxValue) {
                    isValid = false
                } else {
                    numbers.add(next)
                }
            }

            if (isValid && numbers.size == config.length) {
                val ruleDescription = formatRuleDescription(steps)
                val missingPosition = Random.nextInt(1, numbers.size)
                val displaySequence = numbers.mapIndexed { index, value ->
                    if (index == missingPosition) null else value
                }
                return SequenceChallenge(
                    displaySequence = displaySequence,
                    fullSequence = numbers,
                    ruleSteps = steps,
                    ruleDescription = ruleDescription,
                    missingPosition = missingPosition
                )
            }
        }

        val fallbackNumbers = (config.minStart until (config.minStart + config.length)).toList()
        val fallbackSteps = listOf(SequenceRuleStep(SequenceOperation.ADD, 1))
        val missingPosition = fallbackNumbers.lastIndex
        return SequenceChallenge(
            displaySequence = fallbackNumbers.mapIndexed { index, value ->
                if (index == missingPosition) null else value
            },
            fullSequence = fallbackNumbers,
            ruleSteps = fallbackSteps,
            ruleDescription = formatRuleDescription(fallbackSteps),
            missingPosition = missingPosition
        )
    }

    private fun buildSteps(config: SequenceConfig): List<SequenceRuleStep> {
        val useAlternating = config.level >= 3 && Random.nextFloat() < 0.3f

        if (useAlternating) {
            val firstOp = when (Random.nextInt(3)) {
                0 -> SequenceOperation.ADD
                1 -> SequenceOperation.SUBTRACT
                else -> SequenceOperation.MULTIPLY
            }

            val secondOp = when (firstOp) {
                SequenceOperation.ADD ->
                    if (Random.nextBoolean()) SequenceOperation.SUBTRACT else SequenceOperation.MULTIPLY
                SequenceOperation.SUBTRACT ->
                    if (Random.nextBoolean()) SequenceOperation.ADD else SequenceOperation.MULTIPLY
                else ->
                    if (Random.nextBoolean()) SequenceOperation.ADD else SequenceOperation.SUBTRACT
            }

            val maxStep = when (config.level) {
                in 5..8 -> 3
                in 9..12 -> 5
                else -> 2
            }

            val firstValue = when (firstOp) {
                SequenceOperation.ADD -> Random.nextInt(1, maxStep + 1)
                SequenceOperation.SUBTRACT -> Random.nextInt(1, maxStep + 1)
                SequenceOperation.MULTIPLY -> Random.nextInt(2, maxStep.coerceAtMost(4) + 1)
            }

            val secondValue = when (secondOp) {
                SequenceOperation.ADD -> Random.nextInt(1, maxStep + 1)
                SequenceOperation.SUBTRACT -> Random.nextInt(1, maxStep + 1)
                SequenceOperation.MULTIPLY -> Random.nextInt(2, maxStep.coerceAtMost(4) + 1)
            }

            return listOf(SequenceRuleStep(
                operation = firstOp,
                value = firstValue,
                alternateOperation = secondOp,
                alternateValue = secondValue
            ))
        } else {
            val stepCount = when {
                config.level >= 9 -> 3
                config.level >= 5 -> 2
                else -> 1
            }

            val operationsPool = when {
                config.level >= 7 -> listOf(SequenceOperation.ADD, SequenceOperation.SUBTRACT, SequenceOperation.MULTIPLY)
                config.level >= 4 -> listOf(SequenceOperation.ADD, SequenceOperation.MULTIPLY, SequenceOperation.SUBTRACT)
                else -> listOf(SequenceOperation.ADD, SequenceOperation.MULTIPLY)
            }

            val maxStep = config.maxStepMagnitude.coerceAtLeast(2)

            return List(stepCount) {
                val operation = operationsPool.random()
                val value = when (operation) {
                    SequenceOperation.ADD -> Random.nextInt(1, maxStep + 1)
                    SequenceOperation.SUBTRACT -> Random.nextInt(1, maxStep)
                    SequenceOperation.MULTIPLY -> Random.nextInt(2, maxStep.coerceAtMost(MAX_MULTIPLIER) + 1)
                }
                SequenceRuleStep(operation, value)
            }
        }
    }

    private fun formatRuleDescription(steps: List<SequenceRuleStep>): String {
        if (steps.isEmpty()) return ""
        return if (steps.size == 1) {
            "Repeat ${steps.first().describe()} each step"
        } else {
            steps.joinToString(separator = " then ") { it.describe() } + " (repeat)"
        }
    }
}


// Main function
fun main() {
    val generator = GenerateSequenceChallengeUseCase()

    for (level in 1..11) {
        println("\n=== LEVEL $level ===")

        // Calculate challenges per level (same logic as in the game)
        val challengesPerLevel = (5 + (level / 2)).coerceAtMost(10) // 5-10 challenges per level

        println("Challenges to complete: $challengesPerLevel")
        println("=".repeat(40))

        val config = SequenceConfig(
            level = level,
            minStart = 1,
            maxStart = 10 + (level * 2),
            maxStepMagnitude = 2 + (level / 2),
            length = 4 + (level / 3),
            maxValue = 999
        )

        for (challengeNum in 1..challengesPerLevel) {
            println("\nChallenge $challengeNum of $challengesPerLevel")
            println("-".repeat(20))

            val challenge = generator(config)

            println("Sequence: ${challenge.displaySequence.joinToString { it?.toString() ?: "?" }}")
            println("Solution: ${challenge.answer}")
            println("Full Sequence: ${challenge.fullSequence}")
            println("Rule: ${challenge.ruleDescription}")
            println("Missing Position: ${challenge.missingPosition}")
        }
    }
}