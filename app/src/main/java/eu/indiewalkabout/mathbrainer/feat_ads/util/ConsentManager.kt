package eu.indiewalkabout.mathbrainer.feat_ads.util

import android.app.Activity
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import eu.indiewalkabout.mathbrainer.BuildConfig

sealed interface AdsState {
    data object Loading : AdsState
    data object Allowed : AdsState
    data object Unavailable : AdsState
}

internal data class ConsentDebugConfiguration(
    val testDeviceId: String,
    val geography: Int
)

internal object ConsentDebugPolicy {
    fun configuration(isDebug: Boolean, testDeviceId: String): ConsentDebugConfiguration? =
        if (isDebug && testDeviceId.isNotBlank()) {
            ConsentDebugConfiguration(
                testDeviceId = testDeviceId,
                geography = ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA
            )
        } else {
            null
        }
}

internal interface ConsentClient {
    val canRequestAds: Boolean

    fun requestConsentInfoUpdate(onSuccess: () -> Unit, onFailure: () -> Unit)

    fun loadAndShowConsentFormIfRequired(onDismissed: (succeeded: Boolean) -> Unit)

    fun showPrivacyOptionsForm(onDismissed: (succeeded: Boolean) -> Unit)
}

class ConsentManager {
    fun requestConsent(activity: Activity, onResult: (AdsState) -> Unit) {
        requestConsent(GoogleConsentClient(activity), onResult)
    }

    internal fun requestConsent(client: ConsentClient, onResult: (AdsState) -> Unit) {
        onResult(AdsState.Loading)
        client.requestConsentInfoUpdate(
            onSuccess = {
                client.loadAndShowConsentFormIfRequired { succeeded ->
                    onResult(client.resolvedState(succeeded))
                }
            },
            onFailure = { onResult(AdsState.Unavailable) }
        )
    }

    fun showPrivacyOptions(activity: Activity, onResult: (AdsState) -> Unit) {
        showPrivacyOptions(GoogleConsentClient(activity), onResult)
    }

    internal fun showPrivacyOptions(client: ConsentClient, onResult: (AdsState) -> Unit) {
        onResult(AdsState.Loading)
        client.showPrivacyOptionsForm { succeeded ->
            onResult(client.resolvedState(succeeded))
        }
    }

    private fun ConsentClient.resolvedState(succeeded: Boolean): AdsState =
        if (succeeded && canRequestAds) AdsState.Allowed else AdsState.Unavailable
}

internal class AdsCoordinator(
    private val consentManager: ConsentManager,
    private val mobileAdsInitializer: MobileAdsInitializer,
    private val mobileAdsClient: MobileAdsClient,
    private val testDeviceIds: List<String>
) {
    fun requestConsent(activity: Activity, onResult: (AdsState) -> Unit) {
        handleConsent(onResult) { callback ->
            consentManager.requestConsent(activity, callback)
        }
    }

    internal fun requestConsent(client: ConsentClient, onResult: (AdsState) -> Unit) {
        handleConsent(onResult) { callback ->
            consentManager.requestConsent(client, callback)
        }
    }

    fun showPrivacyOptions(activity: Activity, onResult: (AdsState) -> Unit) {
        handleConsent(onResult) { callback ->
            consentManager.showPrivacyOptions(activity, callback)
        }
    }

    internal fun showPrivacyOptions(client: ConsentClient, onResult: (AdsState) -> Unit) {
        handleConsent(onResult) { callback ->
            consentManager.showPrivacyOptions(client, callback)
        }
    }

    private fun handleConsent(
        onResult: (AdsState) -> Unit,
        request: ((AdsState) -> Unit) -> Unit
    ) {
        request { state ->
            if (state == AdsState.Allowed) {
                mobileAdsInitializer.initialize(mobileAdsClient, testDeviceIds)
            }
            onResult(state)
        }
    }
}

private class GoogleConsentClient(
    private val activity: Activity
) : ConsentClient {
    private val consentInformation: ConsentInformation =
        UserMessagingPlatform.getConsentInformation(activity)

    override val canRequestAds: Boolean
        get() = consentInformation.canRequestAds()

    override fun requestConsentInfoUpdate(onSuccess: () -> Unit, onFailure: () -> Unit) {
        consentInformation.requestConsentInfoUpdate(
            activity,
            consentRequestParameters(),
            onSuccess,
            { onFailure() }
        )
    }

    override fun loadAndShowConsentFormIfRequired(onDismissed: (Boolean) -> Unit) {
        UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) { formError ->
            onDismissed(formError == null)
        }
    }

    override fun showPrivacyOptionsForm(onDismissed: (Boolean) -> Unit) {
        UserMessagingPlatform.showPrivacyOptionsForm(activity) { formError ->
            onDismissed(formError == null)
        }
    }

    private fun consentRequestParameters(): ConsentRequestParameters {
        val builder = ConsentRequestParameters.Builder()
            .setTagForUnderAgeOfConsent(false)

        ConsentDebugPolicy.configuration(
            isDebug = BuildConfig.DEBUG,
            testDeviceId = BuildConfig.ADMOB_TEST_DEVICE_ID
        )?.let { configuration ->
            val debugSettings = ConsentDebugSettings.Builder(activity)
                .addTestDeviceHashedId(configuration.testDeviceId)
                .setDebugGeography(configuration.geography)
                .build()
            builder.setConsentDebugSettings(debugSettings)
        }

        return builder.build()
    }
}
