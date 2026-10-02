package eu.indiewalkabout.mathbrainer.feat_ads

import eu.indiewalkabout.mathbrainer.feat_ads.util.AdsCoordinator
import eu.indiewalkabout.mathbrainer.feat_ads.util.AdsState
import eu.indiewalkabout.mathbrainer.feat_ads.util.ConsentClient
import eu.indiewalkabout.mathbrainer.feat_ads.util.ConsentDebugPolicy
import eu.indiewalkabout.mathbrainer.feat_ads.util.ConsentManager
import eu.indiewalkabout.mathbrainer.feat_ads.util.InitializationGate
import eu.indiewalkabout.mathbrainer.feat_ads.util.MobileAdsClient
import eu.indiewalkabout.mathbrainer.feat_ads.util.MobileAdsInitializer
import eu.indiewalkabout.mathbrainer.feat_ads.util.RequestConfigurationUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import com.google.android.ump.ConsentDebugSettings

class ConsentManagerTest {

    @Test
    fun `ads stay loading and uninitialized until UMP allows requests`() {
        val consentClient = FakeConsentClient(canRequestAds = true)
        val mobileAdsClient = FakeMobileAdsClient()
        val coordinator = coordinator(mobileAdsClient)
        val states = mutableListOf<AdsState>()

        coordinator.requestConsent(consentClient, states::add)

        assertEquals(listOf(AdsState.Loading), states)
        assertFalse(mobileAdsClient.initialized)

        consentClient.completeInfoUpdate()

        assertEquals(listOf(AdsState.Loading), states)
        assertFalse(mobileAdsClient.initialized)

        consentClient.dismissConsentForm(succeeded = true)

        assertEquals(listOf(AdsState.Loading, AdsState.Allowed), states)
        assertTrue(mobileAdsClient.initialized)
    }

    @Test
    fun `consent info update errors fail closed`() {
        val consentClient = FakeConsentClient(canRequestAds = true)
        val mobileAdsClient = FakeMobileAdsClient()
        val states = mutableListOf<AdsState>()

        coordinator(mobileAdsClient).requestConsent(consentClient, states::add)
        consentClient.failInfoUpdate()

        assertEquals(listOf(AdsState.Loading, AdsState.Unavailable), states)
        assertFalse(mobileAdsClient.initialized)
        assertFalse(consentClient.consentFormRequested)
    }

    @Test
    fun `consent form errors fail closed even when prior consent could request ads`() {
        val consentClient = FakeConsentClient(canRequestAds = true)
        val mobileAdsClient = FakeMobileAdsClient()
        val states = mutableListOf<AdsState>()

        coordinator(mobileAdsClient).requestConsent(consentClient, states::add)
        consentClient.completeInfoUpdate()
        consentClient.dismissConsentForm(succeeded = false)

        assertEquals(listOf(AdsState.Loading, AdsState.Unavailable), states)
        assertFalse(mobileAdsClient.initialized)
    }

    @Test
    fun `UMP denial leaves ads unavailable`() {
        val consentClient = FakeConsentClient(canRequestAds = false)
        val mobileAdsClient = FakeMobileAdsClient()
        val states = mutableListOf<AdsState>()

        coordinator(mobileAdsClient).requestConsent(consentClient, states::add)
        consentClient.completeInfoUpdate()
        consentClient.dismissConsentForm(succeeded = true)

        assertEquals(listOf(AdsState.Loading, AdsState.Unavailable), states)
        assertFalse(mobileAdsClient.initialized)
    }

    @Test
    fun `privacy options reopen through UMP and re-evaluate eligibility`() {
        val consentClient = FakeConsentClient(canRequestAds = false)
        val mobileAdsClient = FakeMobileAdsClient()
        val states = mutableListOf<AdsState>()
        val coordinator = coordinator(mobileAdsClient)

        coordinator.showPrivacyOptions(consentClient, states::add)

        assertEquals(listOf(AdsState.Loading), states)
        assertTrue(consentClient.privacyOptionsRequested)

        consentClient.canRequestAds = true
        consentClient.dismissPrivacyOptions(succeeded = true)

        assertEquals(listOf(AdsState.Loading, AdsState.Allowed), states)
        assertTrue(mobileAdsClient.initialized)
    }

    @Test
    fun `privacy options errors fail closed`() {
        val consentClient = FakeConsentClient(canRequestAds = true)
        val mobileAdsClient = FakeMobileAdsClient()
        val states = mutableListOf<AdsState>()

        coordinator(mobileAdsClient).showPrivacyOptions(consentClient, states::add)
        consentClient.dismissPrivacyOptions(succeeded = false)

        assertEquals(listOf(AdsState.Loading, AdsState.Unavailable), states)
        assertFalse(mobileAdsClient.initialized)
    }

    @Test
    fun `older consent success is ignored after newer privacy request succeeds`() {
        val olderConsent = FakeConsentClient(canRequestAds = true)
        val newerPrivacy = FakeConsentClient(canRequestAds = true)
        val mobileAdsClient = FakeMobileAdsClient()
        val states = mutableListOf<AdsState>()
        val coordinator = coordinator(mobileAdsClient)

        coordinator.requestConsent(olderConsent, states::add)
        coordinator.showPrivacyOptions(newerPrivacy, states::add)
        newerPrivacy.dismissPrivacyOptions(succeeded = true)
        olderConsent.completeInfoUpdate()

        assertEquals(
            listOf(AdsState.Loading, AdsState.Loading, AdsState.Allowed),
            states
        )
        assertFalse(olderConsent.consentFormRequested)
        assertEquals(1, mobileAdsClient.initializeCalls)
    }

    @Test
    fun `newer failure is not overwritten by older consent success`() {
        val olderConsent = FakeConsentClient(canRequestAds = true)
        val newerPrivacy = FakeConsentClient(canRequestAds = true)
        val mobileAdsClient = FakeMobileAdsClient()
        val states = mutableListOf<AdsState>()
        val coordinator = coordinator(mobileAdsClient)

        coordinator.requestConsent(olderConsent, states::add)
        coordinator.showPrivacyOptions(newerPrivacy, states::add)
        newerPrivacy.dismissPrivacyOptions(succeeded = false)
        olderConsent.completeInfoUpdate()

        assertEquals(
            listOf(AdsState.Loading, AdsState.Loading, AdsState.Unavailable),
            states
        )
        assertFalse(olderConsent.consentFormRequested)
        assertFalse(mobileAdsClient.initialized)
    }

    @Test
    fun `callbacks are ignored after coordinator invalidation`() {
        val consentClient = FakeConsentClient(canRequestAds = true)
        val mobileAdsClient = FakeMobileAdsClient()
        val states = mutableListOf<AdsState>()
        val coordinator = coordinator(mobileAdsClient)

        coordinator.requestConsent(consentClient, states::add)
        coordinator.invalidate()
        consentClient.completeInfoUpdate()

        assertEquals(listOf(AdsState.Loading), states)
        assertFalse(consentClient.consentFormRequested)
        assertFalse(mobileAdsClient.initialized)
    }

    @Test
    fun `Mobile Ads configuration exception fails closed`() {
        val consentClient = FakeConsentClient(canRequestAds = true)
        val mobileAdsClient = FakeMobileAdsClient(throwOnConfiguration = true)
        val states = mutableListOf<AdsState>()

        coordinator(mobileAdsClient).requestConsent(consentClient, states::add)
        consentClient.completeInfoUpdate()
        consentClient.dismissConsentForm(succeeded = true)

        assertEquals(listOf(AdsState.Loading, AdsState.Unavailable), states)
        assertFalse(mobileAdsClient.initialized)
    }

    @Test
    fun `Mobile Ads initialization exception fails closed`() {
        val consentClient = FakeConsentClient(canRequestAds = true)
        val mobileAdsClient = FakeMobileAdsClient(throwOnInitialization = true)
        val states = mutableListOf<AdsState>()

        coordinator(mobileAdsClient).requestConsent(consentClient, states::add)
        consentClient.completeInfoUpdate()
        consentClient.dismissConsentForm(succeeded = true)

        assertEquals(listOf(AdsState.Loading, AdsState.Unavailable), states)
        assertFalse(mobileAdsClient.initialized)
    }

    @Test
    fun `Mobile Ads initialization is idempotent`() {
        val client = FakeMobileAdsClient()
        val initializer = MobileAdsInitializer(FakeInitializationGate())

        initializer.initialize(client, listOf("debug-device"))
        initializer.initialize(client, listOf("debug-device"))

        assertEquals(1, client.initializeCalls)
        assertEquals(1, client.configurationCalls)
        assertEquals(listOf("debug-device"), client.testDeviceIds)
    }

    @Test
    fun `test device IDs are selected only for debug builds`() {
        assertEquals(
            listOf("debug-device"),
            RequestConfigurationUtils.testDeviceIds(
                isDebug = true,
                configuredId = "debug-device"
            )
        )
        assertTrue(
            RequestConfigurationUtils.testDeviceIds(
                isDebug = false,
                configuredId = "debug-device"
            ).isEmpty()
        )
        assertTrue(
            RequestConfigurationUtils.testDeviceIds(
                isDebug = true,
                configuredId = ""
            ).isEmpty()
        )
    }

    @Test
    fun `UMP EEA debug geography is mechanically excluded from release`() {
        val debugConfiguration = ConsentDebugPolicy.configuration(
            isDebug = true,
            testDeviceId = "debug-device"
        )

        assertEquals("debug-device", debugConfiguration?.testDeviceId)
        assertEquals(
            ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA,
            debugConfiguration?.geography
        )
        assertEquals(
            null,
            ConsentDebugPolicy.configuration(
                isDebug = false,
                testDeviceId = "debug-device"
            )
        )
    }

    private fun coordinator(client: FakeMobileAdsClient) = AdsCoordinator(
        consentManager = ConsentManager(),
        mobileAdsInitializer = MobileAdsInitializer(FakeInitializationGate()),
        mobileAdsClient = client,
        testDeviceIds = listOf("debug-device")
    )
}

private class FakeConsentClient(
    override var canRequestAds: Boolean
) : ConsentClient {
    private lateinit var infoUpdateSuccess: () -> Unit
    private lateinit var infoUpdateFailure: () -> Unit
    private lateinit var consentFormDismissed: (Boolean) -> Unit
    private lateinit var privacyOptionsDismissed: (Boolean) -> Unit

    var consentFormRequested = false
        private set
    var privacyOptionsRequested = false
        private set

    override fun requestConsentInfoUpdate(onSuccess: () -> Unit, onFailure: () -> Unit) {
        infoUpdateSuccess = onSuccess
        infoUpdateFailure = onFailure
    }

    override fun loadAndShowConsentFormIfRequired(onDismissed: (succeeded: Boolean) -> Unit) {
        consentFormRequested = true
        consentFormDismissed = onDismissed
    }

    override fun showPrivacyOptionsForm(onDismissed: (succeeded: Boolean) -> Unit) {
        privacyOptionsRequested = true
        privacyOptionsDismissed = onDismissed
    }

    fun completeInfoUpdate() = infoUpdateSuccess()

    fun failInfoUpdate() = infoUpdateFailure()

    fun dismissConsentForm(succeeded: Boolean) = consentFormDismissed(succeeded)

    fun dismissPrivacyOptions(succeeded: Boolean) = privacyOptionsDismissed(succeeded)
}

private class FakeMobileAdsClient(
    private val throwOnConfiguration: Boolean = false,
    private val throwOnInitialization: Boolean = false
) : MobileAdsClient {
    var initializeCalls = 0
        private set
    var configurationCalls = 0
        private set
    var testDeviceIds: List<String> = emptyList()
        private set

    val initialized: Boolean
        get() = initializeCalls > 0

    override fun setTestDeviceIds(testDeviceIds: List<String>) {
        if (throwOnConfiguration) error("configuration failed")
        configurationCalls += 1
        this.testDeviceIds = testDeviceIds
    }

    override fun initialize() {
        if (throwOnInitialization) error("initialization failed")
        initializeCalls += 1
    }
}

private class FakeInitializationGate : InitializationGate {
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
