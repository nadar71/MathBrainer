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
                val next = step.apply(numbers.last())
                if (next <= 0 || next > config.maxValue) {
                    isValid = false
                } else {
                    numbers.add(next)
                }
            }

            if (isValid && numbers.size == config.length) {
                val ruleDescription = formatRuleDescription(steps)
                val displaySequence = numbers.mapIndexed { index, value ->
                    if (index == numbers.lastIndex) null else value
                }
                return SequenceChallenge(
                    displaySequence = displaySequence,
                    fullSequence = numbers,
                    ruleSteps = steps,
                    ruleDescription = ruleDescription
                )
            }
        }

        val fallbackNumbers = (config.minStart until (config.minStart + config.length)).toList()
        val fallbackSteps = listOf(SequenceRuleStep(SequenceOperation.ADD, 1))
        return SequenceChallenge(
            displaySequence = fallbackNumbers.mapIndexed { index, value ->
                if (index == fallbackNumbers.lastIndex) null else value
            },
            fullSequence = fallbackNumbers,
            ruleSteps = fallbackSteps,
            ruleDescription = formatRuleDescription(fallbackSteps)
        )
    }

    private fun buildSteps(config: SequenceConfig): List<SequenceRuleStep> {
        val stepCount = when {
            config.level >= 8 -> 3
            config.level >= 4 -> 2
            else -> 1
        }

        val operationsPool = when {
            config.level >= 6 -> listOf(SequenceOperation.ADD, SequenceOperation.SUBTRACT, SequenceOperation.MULTIPLY)
            config.level >= 3 -> listOf(SequenceOperation.ADD, SequenceOperation.MULTIPLY, SequenceOperation.SUBTRACT)
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
