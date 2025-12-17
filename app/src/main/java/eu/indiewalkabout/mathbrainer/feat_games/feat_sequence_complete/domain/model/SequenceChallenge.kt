package eu.indiewalkabout.mathbrainer.feat_games.feat_sequence_complete.domain.model

data class SequenceChallenge(
    val displaySequence: List<Int?>,
    val fullSequence: List<Int>,
    val ruleSteps: List<SequenceRuleStep>,
    val ruleDescription: String
) {
    val answer: Int = fullSequence.last()
}
