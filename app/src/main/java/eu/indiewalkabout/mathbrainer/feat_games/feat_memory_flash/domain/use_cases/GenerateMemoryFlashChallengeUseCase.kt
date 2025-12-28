package eu.indiewalkabout.mathbrainer.feat_games.feat_memory_flash.domain.use_cases

import eu.indiewalkabout.mathbrainer.feat_games.feat_memory_flash.domain.model.MemoryFlashChallenge
import eu.indiewalkabout.mathbrainer.feat_games.feat_memory_flash.domain.model.MemoryFlashConfig
import javax.inject.Inject
import kotlin.random.Random

class GenerateMemoryFlashChallengeUseCase @Inject constructor() {
    operator fun invoke(config: MemoryFlashConfig): MemoryFlashChallenge {
        val digits = MutableList(config.length) { Random.nextInt(0, config.maxDigit + 1) }
        return MemoryFlashChallenge(sequence = digits)
    }
}
