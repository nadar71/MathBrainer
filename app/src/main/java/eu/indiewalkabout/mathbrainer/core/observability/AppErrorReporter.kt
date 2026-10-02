package eu.indiewalkabout.mathbrainer.core.observability

/**
 * Reports recoverable application failures without coupling feature code to a telemetry provider.
 * Implementations must not add user-linked or gameplay-sensitive values to [context].
 */
interface AppErrorReporter {
    fun record(error: Throwable, context: Map<String, String> = emptyMap())
}
