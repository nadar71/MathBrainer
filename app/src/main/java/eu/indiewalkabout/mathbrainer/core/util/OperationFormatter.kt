package eu.indiewalkabout.mathbrainer.core.util

object OperationFormatter {
    fun format(operation: Char): String {
        return when (operation) {
            '*' -> "×"
            else -> operation.toString()
        }
    }

    fun format(operation: String): String {
        return operation.replace('*', '×')
    }
}
