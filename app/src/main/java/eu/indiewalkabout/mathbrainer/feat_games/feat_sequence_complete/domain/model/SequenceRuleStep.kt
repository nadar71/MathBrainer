package eu.indiewalkabout.mathbrainer.feat_games.feat_sequence_complete.domain.model

enum class SequenceOperation(val symbol: String) {
    ADD("+"),
    SUBTRACT("-"),
    MULTIPLY("×");
}

data class SequenceRuleStep(
    val operation: SequenceOperation,
    val value: Int,
    val alternateOperation: SequenceOperation? = null,
    val alternateValue: Int? = null
) {
    fun apply(to: Int, position: Int): Int {
        val useAlternate = alternateOperation != null && position % 2 == 1
        return when (if (useAlternate) alternateOperation else operation) {
            SequenceOperation.ADD -> to + (if (useAlternate) alternateValue!! else value)
            SequenceOperation.SUBTRACT -> to - (if (useAlternate) alternateValue!! else value)
            SequenceOperation.MULTIPLY -> to * (if (useAlternate) alternateValue!! else value)
            else -> to
        }
    }

    fun describe(): String {
        return if (alternateOperation != null && alternateValue != null) {
            "${operation.symbol}$value, ${alternateOperation.symbol}$alternateValue (alternating)"
        } else {
            "${operation.symbol}$value"
        }
    }
}
