package eu.indiewalkabout.mathbrainer.core.observability

/** Default reporter until the owner approves a privacy-reviewed provider integration. */
object NoOpErrorReporter : AppErrorReporter {
    override fun record(error: Throwable, context: Map<String, String>) = Unit
}
