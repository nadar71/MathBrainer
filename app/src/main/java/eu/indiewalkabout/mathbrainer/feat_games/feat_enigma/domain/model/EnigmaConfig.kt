package eu.indiewalkabout.mathbrainer.feat_games.feat_enigma.domain.model

data class EnigmaConfig(
    val level: Int,
    val minValue: Int = 2,
    val maxValue: Int = 20
)
