package eu.indiewalkabout.mathbrainer.feat_games.feat_sequence_complete.domain.model

data class SequenceConfig(
    val level: Int,
    val minStart: Int,
    val maxStart: Int,
    val maxStepMagnitude: Int,
    val length: Int,
    val maxValue: Int = 9_999
)
