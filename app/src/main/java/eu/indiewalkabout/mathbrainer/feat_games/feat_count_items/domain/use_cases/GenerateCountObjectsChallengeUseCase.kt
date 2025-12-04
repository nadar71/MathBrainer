package eu.indiewalkabout.mathbrainer.feat_games.feat_count_items.domain.use_cases

import eu.indiewalkabout.mathbrainer.feat_games.feat_count_items.domain.model.CountObjectsChallenge
import eu.indiewalkabout.mathbrainer.feat_games.feat_count_items.domain.model.CountObjectsConfig
import javax.inject.Inject
import kotlin.math.ceil
import kotlin.random.Random

class GenerateCountObjectsChallengeUseCase @Inject constructor() {

    operator fun invoke(config: CountObjectsConfig): CountObjectsChallenge {
        val minItems = ceil(config.maxItemsToCount * MIN_ITEMS_RATIO).toInt().coerceAtLeast(1)
        val itemsToCount = Random.nextInt(minItems, config.maxItemsToCount + 1)

        val correctAnswerIndex = Random.nextInt(ANSWER_BUTTONS)
        val answers = MutableList(ANSWER_BUTTONS) { 0 }
        answers[correctAnswerIndex] = itemsToCount

        for (index in answers.indices) {
            if (index == correctAnswerIndex) continue

            var candidate: Int
            do {
                val offset = Random.nextInt(1, config.answerOffset + 1)
                val sign = if (offset <= 3) if (Random.nextBoolean()) 1 else -1 else 1
                candidate = (itemsToCount + sign * offset).coerceAtLeast(1)
            } while (candidate == itemsToCount || answers.contains(candidate))

            answers[index] = candidate
        }

        return CountObjectsChallenge(
            itemsToCount = itemsToCount,
            answerOptions = answers.shuffled()
        )
    }

    companion object {
        private const val MIN_ITEMS_RATIO = 0.7
        private const val ANSWER_BUTTONS = 4
    }
}