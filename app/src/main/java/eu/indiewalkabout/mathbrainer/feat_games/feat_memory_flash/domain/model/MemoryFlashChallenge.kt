package eu.indiewalkabout.mathbrainer.feat_games.feat_memory_flash.domain.model

data class MemoryFlashChallenge(
    val sequence: List<Int>
) {
    val answer: String = sequence.joinToString(separator = "")
    val displaySequence: String = sequence.joinToString(separator = " ")
}
