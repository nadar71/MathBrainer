package eu.indiewalkabout.mathbrainer.feat_ads.util

import android.content.Context
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import java.util.concurrent.atomic.AtomicBoolean

object RequestConfigurationUtils {
    fun testDeviceIds(isDebug: Boolean, configuredId: String): List<String> =
        if (isDebug && configuredId.isNotBlank()) listOf(configuredId) else emptyList()
}

internal interface MobileAdsClient {
    fun setTestDeviceIds(testDeviceIds: List<String>)

    fun initialize()
}

internal class GoogleMobileAdsClient(
    private val context: Context
) : MobileAdsClient {
    override fun setTestDeviceIds(testDeviceIds: List<String>) {
        val configuration = RequestConfiguration.Builder()
            .setTestDeviceIds(testDeviceIds)
            .build()
        MobileAds.setRequestConfiguration(configuration)
    }

    override fun initialize() {
        MobileAds.initialize(context) {}
    }
}

internal interface InitializationGate {
    fun tryAcquire(): Boolean

    fun release()
}

private object ProcessInitializationGate : InitializationGate {
    private val initialized = AtomicBoolean(false)

    override fun tryAcquire(): Boolean = initialized.compareAndSet(false, true)

    override fun release() {
        initialized.set(false)
    }
}

internal class MobileAdsInitializer(
    private val gate: InitializationGate = ProcessInitializationGate
) {
    fun initialize(client: MobileAdsClient, testDeviceIds: List<String>) {
        if (!gate.tryAcquire()) return

        try {
            if (testDeviceIds.isNotEmpty()) {
                client.setTestDeviceIds(testDeviceIds)
            }
            client.initialize()
        } catch (error: Throwable) {
            gate.release()
            throw error
        }
    }
}
