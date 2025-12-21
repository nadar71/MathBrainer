package eu.indiewalkabout.mathbrainer.presentation.games.othergames.model

data class FallingOperation(
    val expression: String,
    val result: Int,
    val operator: Char,
    var x: Float,
    var y: Float
)
