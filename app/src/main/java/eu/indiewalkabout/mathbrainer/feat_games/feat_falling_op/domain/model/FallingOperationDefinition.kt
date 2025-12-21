package eu.indiewalkabout.mathbrainer.feat_games.feat_falling_op.domain.model

data class FallingOperationDefinition(
    val left: Int,
    val right: Int,
    val operator: Char,
    val result: Int
) {
    val expression: String = "$left $operator $right"
}
