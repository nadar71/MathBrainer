package eu.indiewalkabout.mathbrainer.feat_games.feat_enigma.domain.model

data class EnigmaChallenge(
    val equations: List<EnigmaEquation>,
    val finalExpression: String,
    val answer: Int,
    val level: Int
)
