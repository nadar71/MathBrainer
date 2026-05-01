package eu.indiewalkabout.mathbrainer.core.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalUriHandler

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

    fun openAppStore(context: Context,appPackageName: String) {
        val context = context
        val marketUri_01 = Uri.parse("market://details?id=$appPackageName")
        val marketUri_02 = Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")

        try {
            Log.d("openAppStore", "store uri: $marketUri_01")
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    marketUri_01
                )
            )
        } catch (anfe: ActivityNotFoundException) {
            try {
                Log.d("openAppStore", "store uri: $marketUri_02")
                context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        marketUri_02
                    )
                )
            } catch (e: ActivityNotFoundException){
                Log.e("OpenAppStore", "Error opening app store", e)
            }
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
