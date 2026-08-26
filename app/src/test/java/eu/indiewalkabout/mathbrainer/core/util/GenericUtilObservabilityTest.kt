package eu.indiewalkabout.mathbrainer.core.util

import eu.indiewalkabout.mathbrainer.core.observability.AppErrorReporter
import eu.indiewalkabout.mathbrainer.core.observability.NoOpErrorReporter
import org.junit.Assert.assertEquals
import org.junit.Test

class GenericUtilObservabilityTest {

    @Test
    fun `reports app store recovery with a static privacy-safe context`() {
        val reporter = RecordingErrorReporter()

        GenericUtil.reportAppStoreUnavailable(reporter)

        assertEquals("Unable to open app store.", reporter.error?.message)
        assertEquals(mapOf("operation" to "open_app_store"), reporter.context)
    }

    @Test
    fun `no-op reporter accepts a recoverable error`() {
        NoOpErrorReporter.record(
            error = IllegalStateException("recoverable failure"),
            context = mapOf("operation" to "open_app_store")
        )
    }

    @Test
    fun `reporter failures do not disrupt app store recovery`() {
        GenericUtil.reportAppStoreUnavailable(
            object : AppErrorReporter {
                override fun record(error: Throwable, context: Map<String, String>) {
                    error("reporting failed")
                }
            }
        )
    }

    private class RecordingErrorReporter : AppErrorReporter {
        var error: Throwable? = null
        var context: Map<String, String> = emptyMap()

        override fun record(error: Throwable, context: Map<String, String>) {
            this.error = error
            this.context = context
        }
    }
}
