package eu.indiewalkabout.mathbrainer.feat_ads

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.ump.ConsentDebugSettings
import eu.indiewalkabout.mathbrainer.feat_ads.util.AdsCoordinator
import eu.indiewalkabout.mathbrainer.feat_ads.util.AdsState
import eu.indiewalkabout.mathbrainer.feat_ads.util.ConsentClient
import eu.indiewalkabout.mathbrainer.feat_ads.util.ConsentDebugPolicy
import eu.indiewalkabout.mathbrainer.feat_ads.util.ConsentManager
import eu.indiewalkabout.mathbrainer.feat_ads.util.InitializationGate
import eu.indiewalkabout.mathbrainer.feat_ads.util.MobileAdsClient
import eu.indiewalkabout.mathbrainer.feat_ads.util.MobileAdsInitializer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ConsentFlowTest {

    @Test
    fun eeaConsentRequired_doesNotInitializeAdsUntilFormAllowsRequests() {
        val consent = InstrumentedConsentClient(canRequestAds = true, formRequired = true)
        val mobileAds = InstrumentedMobileAdsClient()
        val states = mutableListOf<AdsState>()

        coordinator(mobileAds).requestConsent(consent, states::add)
        consent.completeInfoUpdate()

        assertEquals(listOf(AdsState.Loading), states)
        assertTrue(consent.consentFormRequested)
        assertFalse(mobileAds.initialized)

        consent.dismissRequiredForm(succeeded = true)

        assertEquals(listOf(AdsState.Loading, AdsState.Allowed), states)
        assertTrue(mobileAds.initialized)
        assertEquals(
            ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA,
            ConsentDebugPolicy.configuration(true, "debug-device")?.geography
        )
    }

    @Test
    fun eeaConsentNotRequired_allowsAdsOnlyAfterUmpNoOpFormCallback() {
        val consent = InstrumentedConsentClient(canRequestAds = true, formRequired = false)
        val mobileAds = InstrumentedMobileAdsClient()
        val states = mutableListOf<AdsState>()

        coordinator(mobileAds).requestConsent(consent, states::add)
        consent.completeInfoUpdate()

        assertEquals(listOf(AdsState.Loading, AdsState.Allowed), states)
        assertTrue(mobileAds.initialized)
    }

    @Test
    fun eeaConsentError_failsClosedWithoutInitializingAds() {
        val consent = InstrumentedConsentClient(canRequestAds = true, formRequired = true)
        val mobileAds = InstrumentedMobileAdsClient()
        val states = mutableListOf<AdsState>()

        coordinator(mobileAds).requestConsent(consent, states::add)
        consent.failInfoUpdate()

        assertEquals(listOf(AdsState.Loading, AdsState.Unavailable), states)
        assertFalse(mobileAds.initialized)
    }

    @Test
    fun privacyOptionsEntry_reopensUmpFormAndReevaluatesEligibility() {
        val consent = InstrumentedConsentClient(canRequestAds = false, formRequired = true)
        val states = mutableListOf<AdsState>()

        coordinator(InstrumentedMobileAdsClient()).showPrivacyOptions(consent, states::add)

        assertTrue(consent.privacyOptionsRequested)
        consent.canRequestAds = true
        consent.dismissPrivacyOptions(succeeded = true)
        assertEquals(listOf(AdsState.Loading, AdsState.Allowed), states)
    }

    private fun coordinator(client: InstrumentedMobileAdsClient) = AdsCoordinator(
        consentManager = ConsentManager(),
        mobileAdsInitializer = MobileAdsInitializer(InstrumentedInitializationGate()),
        mobileAdsClient = client,
        testDeviceIds = listOf("debug-device")
    )
}

private class InstrumentedConsentClient(
    override var canRequestAds: Boolean,
    private val formRequired: Boolean
) : ConsentClient {
    private lateinit var infoUpdateSuccess: () -> Unit
    private lateinit var infoUpdateFailure: () -> Unit
    private lateinit var formDismissed: (Boolean) -> Unit
    private lateinit var privacyDismissed: (Boolean) -> Unit

    var consentFormRequested = false
        private set
    var privacyOptionsRequested = false
        private set

    override fun requestConsentInfoUpdate(onSuccess: () -> Unit, onFailure: () -> Unit) {
        infoUpdateSuccess = onSuccess
        infoUpdateFailure = onFailure
    }

    override fun loadAndShowConsentFormIfRequired(onDismissed: (Boolean) -> Unit) {
        consentFormRequested = true
        if (formRequired) {
            formDismissed = onDismissed
        } else {
            onDismissed(true)
        }
    }

    override fun showPrivacyOptionsForm(onDismissed: (Boolean) -> Unit) {
        privacyOptionsRequested = true
        privacyDismissed = onDismissed
    }

    fun completeInfoUpdate() = infoUpdateSuccess()

    fun failInfoUpdate() = infoUpdateFailure()

    fun dismissRequiredForm(succeeded: Boolean) = formDismissed(succeeded)

    fun dismissPrivacyOptions(succeeded: Boolean) = privacyDismissed(succeeded)
}

private class InstrumentedMobileAdsClient : MobileAdsClient {
    var initialized = false
        private set

    override fun setTestDeviceIds(testDeviceIds: List<String>) = Unit

    override fun initialize() {
        initialized = true
    }
}

private class InstrumentedInitializationGate : InitializationGate {
    private var acquired = false

    override fun tryAcquire(): Boolean {
        if (acquired) return false
        acquired = true
        return true
    }

    override fun release() {
        acquired = false
    }
}
