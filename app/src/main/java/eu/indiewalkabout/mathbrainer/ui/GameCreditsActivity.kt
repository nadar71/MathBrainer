package eu.indiewalkabout.mathbrainer.ui

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView

import com.unity3d.ads.IUnityAdsListener
import com.unity3d.ads.UnityAds

import eu.indiewalkabout.mathbrainer.R
import eu.indiewalkabout.mathbrainer.util.ConsentSDK
import eu.indiewalkabout.mathbrainer.util.MathBrainerUtility
import kotlinx.android.synthetic.main.activity_credits.*

class GameCreditsActivity : AppCompatActivity() {

    private val unityAdsListener = UnityAdsListener()

    // internal var gdprConsent_tv: TextView? = null
    // internal var backhome_img:  ImageView? = null
    private lateinit var consentSDK: ConsentSDK


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_credits)

        // Unity ads init
        UnityAds.initialize(this, resources.getString(R.string.unityads_key), unityAdsListener)


        // Initialize ConsentSDK
        initConsentSDK(this)

        // Checking the status of the user
        if (ConsentSDK.isUserLocationWithinEea(this)) {
            val choice = if (ConsentSDK.isConsentPersonalized(this)) "Personalize" else "Non-Personalize"
            Log.i(TAG, "onCreate: consent choice : $choice")

            gdprConsent_tv.setOnClickListener {
                // Check Consent SDK
                // Request the consent without callback
                // consentSDK.requestConsent(null);
                //To get the result of the consent
                consentSDK!!.requestConsent(object : ConsentSDK.ConsentStatusCallback() {
                    override fun onResult(isRequestLocationInEeaOrUnknown: Boolean, isConsentPersonalized: Int) {
                        var choice = ""
                        when (isConsentPersonalized) {
                            0  -> choice = "Non-Personalize"
                            1  -> choice = "Personalized"
                            -1 -> choice = "Error occurred"
                        }
                        Log.i(TAG, "onCreate: consent choice : $choice")

                        hideStatusNavBars()
                    }

                })
            }

        } else {
            gdprConsent_tv!!.visibility = View.INVISIBLE
        }


        // You have to pass the AdRequest from ConsentSDK.getAdRequest(this) because it handle the right way to load the ad
        mAdView!!.loadAd(ConsentSDK.getAdRequest(this@GameCreditsActivity))


        backhome_img.setOnClickListener {
            // show unityads randomic
            MathBrainerUtility.showUnityAdsRandom(this@GameCreditsActivity)

            val intent = Intent(this@GameCreditsActivity, ChooseGameActivity::class.java)
            startActivity(intent)
        }


        hideStatusNavBars()

    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Make bottom navigation bar and status bar hide, without resize when reappearing
     * ---------------------------------------------------------------------------------------------
     */
    private fun hideStatusNavBars() {
        // minsdk version is 19, no need code for lower api
        val decorView = window.decorView
        val uiOptions = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION     // hide navigation bar

                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY  // hide navigation bar

                // View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                // View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_FULLSCREEN) // // hide status bar
        decorView.systemUiVisibility = uiOptions
    }

    /**
     * -----------------------------------------------------------------------------------------
     * Initialize consent
     * @param context
     * -----------------------------------------------------------------------------------------
     */
    private fun initConsentSDK(context: Context) {
        // Initialize ConsentSDK
        consentSDK = ConsentSDK.Builder(context)
                .addTestDeviceId("7DC1A1E8AEAD7908E42271D4B68FB270") // Add your test device id "Remove addTestDeviceId on production!"
                .addCustomLogTag("gdpr_TAG") // Add custom tag default: ID_LOG
                .addPrivacyPolicy("http://www.indie-walkabout.eu/privacy-policy-app") // Add your privacy policy url
                .addPublisherId("pub-8846176967909254") // Add your admob publisher id
                .build()!!
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Unity ads listener
     * ---------------------------------------------------------------------------------------------
     */
    private inner class UnityAdsListener : IUnityAdsListener {

        override fun onUnityAdsReady(s: String) {

        }

        override fun onUnityAdsStart(s: String) {

        }

        override fun onUnityAdsFinish(s: String, finishState: UnityAds.FinishState) {

        }

        override fun onUnityAdsError(unityAdsError: UnityAds.UnityAdsError, s: String) {

        }
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * MENU STUFF
     * ---------------------------------------------------------------------------------------------
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        // When the home button is pressed, take the user back to Home
        if (id == android.R.id.home) {


            onBackPressed()
        }

        return super.onOptionsItemSelected(item)
    }


    // ---------------------------------------------------------------------------------------------
    //                                  REVEALING FAB BTN STUFF
    // ---------------------------------------------------------------------------------------------
    override fun onBackPressed() {
        super.onBackPressed()

        // show unityads randomic
        MathBrainerUtility.showUnityAdsRandom(this)

    }

    companion object {

        val TAG = GameCreditsActivity::class.java.name
    }
}
