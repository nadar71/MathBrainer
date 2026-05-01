package eu.indiewalkabout.mathbrainer.feat_games.feat_enigma.domain.model

import eu.indiewalkabout.mathbrainer.core.presentation.state.Challenge

data class EnigmaChallenge(
    val equations: List<EnigmaEquation>,
    val finalExpression: String,
    val answer: Int,
    val level: Int
): Challenge
