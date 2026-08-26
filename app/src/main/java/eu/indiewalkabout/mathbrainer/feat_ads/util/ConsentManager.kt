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

    internal fun requestConsent(
        client: ConsentClient,
        onResult: (AdsState) -> Unit,
        isRequestActive: () -> Boolean = { true }
    ) {
        onResult(AdsState.Loading)
        client.requestConsentInfoUpdate(
            onSuccess = {
                if (!isRequestActive()) return@requestConsentInfoUpdate
                client.loadAndShowConsentFormIfRequired { succeeded ->
                    if (isRequestActive()) {
                        onResult(client.resolvedState(succeeded))
                    }
                }
            },
            onFailure = {
                if (isRequestActive()) {
                    onResult(AdsState.Unavailable)
                }
            }
        )
    }

    fun showPrivacyOptions(activity: Activity, onResult: (AdsState) -> Unit) {
        showPrivacyOptions(GoogleConsentClient(activity), onResult)
    }

    internal fun showPrivacyOptions(
        client: ConsentClient,
        onResult: (AdsState) -> Unit,
        isRequestActive: () -> Boolean = { true }
    ) {
        onResult(AdsState.Loading)
        if (!isRequestActive()) return
        client.showPrivacyOptionsForm { succeeded ->
            if (isRequestActive()) {
                onResult(client.resolvedState(succeeded))
            }
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
    private val requestLock = Any()
    private var nextGeneration = 0L
    private var activeGeneration: Long? = null

    fun requestConsent(activity: Activity, onResult: (AdsState) -> Unit) {
        handleConsent(onResult) { isActive, callback ->
            consentManager.requestConsent(
                client = GoogleConsentClient(activity),
                onResult = callback,
                isRequestActive = isActive
            )
        }
    }

    internal fun requestConsent(client: ConsentClient, onResult: (AdsState) -> Unit) {
        handleConsent(onResult) { isActive, callback ->
            consentManager.requestConsent(client, callback, isActive)
        }
    }

    fun showPrivacyOptions(activity: Activity, onResult: (AdsState) -> Unit) {
        handleConsent(onResult) { isActive, callback ->
            consentManager.showPrivacyOptions(
                client = GoogleConsentClient(activity),
                onResult = callback,
                isRequestActive = isActive
            )
        }
    }

    internal fun showPrivacyOptions(client: ConsentClient, onResult: (AdsState) -> Unit) {
        handleConsent(onResult) { isActive, callback ->
            consentManager.showPrivacyOptions(client, callback, isActive)
        }
    }

    fun invalidate() {
        synchronized(requestLock) {
            nextGeneration += 1
            activeGeneration = null
        }
    }

    private fun handleConsent(
        onResult: (AdsState) -> Unit,
        request: (isActive: () -> Boolean, onState: (AdsState) -> Unit) -> Unit
    ) {
        val generation = synchronized(requestLock) {
            nextGeneration += 1
            activeGeneration = nextGeneration
            nextGeneration
        }

        val isActive = {
            synchronized(requestLock) {
                activeGeneration == generation
            }
        }

        request(isActive) { state ->
            synchronized(requestLock) {
                if (activeGeneration != generation) return@synchronized

                val publishedState = if (state == AdsState.Allowed) {
                    try {
                        mobileAdsInitializer.initialize(mobileAdsClient, testDeviceIds)
                        AdsState.Allowed
                    } catch (_: Exception) {
                        AdsState.Unavailable
                    }
                } else {
                    state
                }

                if (publishedState != AdsState.Loading) {
                    activeGeneration = null
                }
                onResult(publishedState)
            }
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
