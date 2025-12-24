package eu.indiewalkabout.mathbrainer.feat_games.feat_math_random_operation.domain.model

enum class ExpressionGrouping {
    NONE,
    LEFT,
    RIGHT
}

data class RandomOperationChallenge(
    val operands: List<Int>,
    val operations: List<Char>,
    val hiddenOperationIndex: Int,
    val result: Int,
    val grouping: ExpressionGrouping = ExpressionGrouping.NONE
) {
    val correctOperation: Char
        get() = operations[hiddenOperationIndex]
}
