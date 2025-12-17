package eu.indiewalkabout.mathbrainer.feat_games.feat_sequence_complete.domain.model

enum class SequenceOperation(val symbol: String) {
    ADD("+"),
    SUBTRACT("-"),
    MULTIPLY("×");
}

data class SequenceRuleStep(
    val operation: SequenceOperation,
    val value: Int
) {
    fun apply(to: Int): Int = when (operation) {
        SequenceOperation.ADD -> to + value
        SequenceOperation.SUBTRACT -> to - value
        SequenceOperation.MULTIPLY -> to * value
    }

    fun describe(): String = when (operation) {
        SequenceOperation.ADD -> "+$value"
        SequenceOperation.SUBTRACT -> "-$value"
        SequenceOperation.MULTIPLY -> "×$value"
    }
}
