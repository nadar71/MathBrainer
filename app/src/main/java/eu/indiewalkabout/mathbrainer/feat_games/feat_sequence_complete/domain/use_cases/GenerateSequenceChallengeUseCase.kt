package eu.indiewalkabout.mathbrainer.feat_games.feat_sequence_complete.domain.use_cases

import eu.indiewalkabout.mathbrainer.feat_games.feat_sequence_complete.domain.model.SequenceChallenge
import eu.indiewalkabout.mathbrainer.feat_games.feat_sequence_complete.domain.model.SequenceConfig
import eu.indiewalkabout.mathbrainer.feat_games.feat_sequence_complete.domain.model.SequenceOperation
import eu.indiewalkabout.mathbrainer.feat_games.feat_sequence_complete.domain.model.SequenceRuleStep
import javax.inject.Inject
import kotlin.random.Random

class GenerateSequenceChallengeUseCase @Inject constructor() {

    operator fun invoke(config: SequenceConfig): SequenceChallenge {
        repeat(MAX_ATTEMPTS) {
            val steps = buildSteps(config)
            val start = Random.nextInt(config.minStart, config.maxStart.coerceAtLeast(config.minStart) + 1)
            val numbers = mutableListOf(start)

            var isValid = true
            while (numbers.size < config.length && isValid) {
                val step = steps[(numbers.size - 1) % steps.size]
                val next = step.apply(numbers.last(), numbers.size - 1) // Pass position for alternating
                if (next <= 0 || next > config.maxValue) {
                    isValid = false
                } else {
                    numbers.add(next)
                }
            }

            if (isValid && numbers.size == config.length) {
                val ruleDescription = formatRuleDescription(steps)
                // Randomly select a position to hide (excluding the first number to ensure solvability)
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
        // For fallback, hide the last number
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
        val level = config.level
        val useAlternating = when {
            level in 3..5 -> Random.nextFloat() < 0.3f  // 30% chance for levels 3-5
            level in 12..16 -> Random.nextFloat() < 0.3f // 30% chance for levels 12-16
            else -> false
        }

        if (useAlternating) {
            // For alternating patterns
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

            val maxStep = when (level) {
                in 3..5 -> 3
                in 12..13 -> 4
                else -> 5  // levels 14-16
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
            // Non-alternating patterns
            val stepCount = when {
                level in 1..5 -> 1
                level in 6..8 -> 2
                level in 9..11 -> Random.nextInt(1, 4)  // 1-3 steps
                level in 12..13 -> 2
                else -> Random.nextInt(1, 4)  // 14-16: 1-3 steps
            }

            val operationsPool = when {
                level >= 1 -> listOf(SequenceOperation.ADD, SequenceOperation.SUBTRACT, SequenceOperation.MULTIPLY)
                else -> listOf(SequenceOperation.ADD, SequenceOperation.MULTIPLY)
            }

            val maxStep = when (level) {
                in 1..2 -> 3
                in 3..5 -> 4
                in 6..8 -> 5
                in 9..11 -> 6
                in 12..13 -> 5
                else -> 7  // 14-16
            }

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

    companion object {
        private const val MAX_MULTIPLIER = 7
        private const val MAX_ATTEMPTS = 50
    }
}


/* RULES
Level Progression
Levels 1-2: Basic Operations
Operations: Addition, Subtraction, Multiplication
Pattern Type: Single operation patterns
Step Values: 1-3
Example:
Sequence: 3, 6, 9, 12 (×3)
Rule: "Repeat ×3 each step"

Levels 3-5: Introducing Alternation
Operations: All operations (+, -, ×)
Pattern Type:
70%: Single operation patterns
30%: Alternating patterns (two operations that alternate)
Step Values: 1-4
Example Alternating:
Sequence: 2, 5, 4, 7, 6, 9
Rule: "+3, -1 (alternating)"

Levels 6-8: Multi-step Sequences
Operations: All operations (+, -, ×)
Pattern Type: 1-2 operations in sequence (no alternation)
Step Values: 1-5
Example:
Sequence: 1, 3, 6, 8, 11, 13
Rule: "+2 then +3 (repeat)"

Levels 9-11: Complex Sequences
Operations: All operations (+, -, ×)
Pattern Type: 1-3 operations in sequence (no alternation)
Step Values: 1-6
Example:
Sequence: 2, 4, 8, 5, 10, 7
Rule: "×2 then -3 then +2 (repeat)"

Levels 12-13: Alternating Multi-step
Operations: All operations (+, -, ×)
Pattern Type:
70%: 2 operations in sequence
30%: Alternating patterns
Step Values: 1-5
Example Alternating:
Sequence: 1, 4, 2, 8, 4, 16
Rule: "×4, -2 (alternating)"

Levels 14-16: Advanced Challenges
Operations: All operations (+, -, ×)
Pattern Type:
70%: 1-3 operations in sequence
30%: Alternating patterns
Step Values: 1-7
Example:
Sequence: 1, 2, 6, 7, 21, 22
Rule: "+1 then ×3 (repeat)"


Sequence Generation:
Each sequence starts with a random number between minStart and maxStart
The sequence length increases with level
One number is randomly hidden (never the first number)

Pattern Generation:
For non-alternating patterns, operations are applied in sequence
For alternating patterns, operations switch between two different operations
The SequenceRuleStep class handles both regular and alternating operations

Difficulty Scaling:
More operations in sequence as levels increase
Larger step values at higher levels
More complex operation combinations

Fallback:
If the generator can't create a valid sequence after 50 attempts, it falls back to a simple +1 pattern

 */
