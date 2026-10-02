package eu.indiewalkabout.mathbrainer.core.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalUriHandler
import eu.indiewalkabout.mathbrainer.core.observability.AppErrorReporter
import eu.indiewalkabout.mathbrainer.core.observability.NoOpErrorReporter

object GenericUtil {

    @Composable
    fun openUrlInBrowser(url: String) {
        val uriHandler = LocalUriHandler.current
        uriHandler.openUri(url)
    }

    fun openUrlInBrowserNotCompose(context: Context, url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    }

    fun openAppStore(
        context: Context,
        appPackageName: String,
        errorReporter: AppErrorReporter = NoOpErrorReporter
    ) {
        val marketUri = Uri.parse("market://details?id=$appPackageName")
        val webUri = Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")

        try {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    marketUri
                )
            )
        } catch (_: ActivityNotFoundException) {
            try {
                context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        webUri
                    )
                )
            } catch (_: ActivityNotFoundException) {
                reportAppStoreUnavailable(errorReporter)
            }
        }
    }

    internal fun reportAppStoreUnavailable(errorReporter: AppErrorReporter) {
        runCatching {
            errorReporter.record(
                error = IllegalStateException("Unable to open app store."),
                context = mapOf("operation" to "open_app_store")
            )
        }
    }

    // Extract only the digit with "." from a String
    fun returnDigit(s: String): String {
        return s.replace("[^0-9?!\\.]+".toRegex(), "")
    }

    // char from a String
    fun returnChar(s: String): String {
        return s.replace("[0-9]+".toRegex(), "")
    }


}
