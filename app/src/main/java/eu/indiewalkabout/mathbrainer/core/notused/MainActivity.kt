package eu.indiewalkabout.mathbrainer.core.notused

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdView
import eu.indiewalkabout.mathbrainer.R
import eu.indiewalkabout.mathbrainer.core.util.ConsentSDK
import eu.indiewalkabout.mathbrainer.core.util.ConsentSDK.Companion.getAdRequest
import eu.indiewalkabout.mathbrainer.core.util.ConsentSDK.ConsentCallback
import eu.indiewalkabout.mathbrainer.presentation.ui.ChooseGameActivity

class MainActivity : AppCompatActivity() {
    // admob banner ref
    var mAdView: AdView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // Initialize ConsentSDK
        val consentSDK = ConsentSDK.Builder(this)
            .addTestDeviceId("7DC1A1E8AEAD7908E42271D4B68FB270") // redminote 5 // Add your test device id "Remove addTestDeviceId on production!"
            // .addTestDeviceId("9978A5F791A259430A0156313ED9C6A2")
            .addCustomLogTag("gdpr_TAG") // Add custom tag default: ID_LOG
            .addPrivacyPolicy("http://www.indie-walkabout.eu/privacy-policy-app") // Add your privacy policy url
            .addPublisherId("pub-8846176967909254") // Add your admob publisher id
            .build()


        // To check the consent and load ads
        consentSDK.checkConsent(object : ConsentCallback() {
            override fun onResult(isRequestLocationInEeaOrUnknown: Boolean) {
                Log.i(
                    "gdpr_TAG",
                    "onResult: isRequestLocationInEeaOrUnknown : $isRequestLocationInEeaOrUnknown"
                )
                // You have to pass the AdRequest from ConsentSDK.getAdRequest(this) because it handle the right way to load the ad
                mAdView!!.loadAd(getAdRequest(this@MainActivity))
            }
        })
        mAdView = findViewById(R.id.mAdView)

        // You have to pass the AdRequest from ConsentSDK.getAdRequest(this) because it handle the right way to load the ad
        mAdView?.loadAd(getAdRequest(this@MainActivity))


        // BEGIN Button management
        val playBtn = findViewById<Button>(R.id.playBtn)
        playBtn.setOnClickListener {
            val intent = Intent(this@MainActivity, ChooseGameActivity::class.java)
            startActivity(intent)
        }
        val scoreBtn = findViewById<Button>(R.id.scoreBtn)
        scoreBtn.setOnClickListener {
            val intent = Intent(this@MainActivity, ChooseGameActivity::class.java)
            startActivity(intent)
        }
        val StatBtn = findViewById<Button>(R.id.StatBtn)
        StatBtn.setOnClickListener {
            val intent = Intent(this@MainActivity, ChooseGameActivity::class.java)
            startActivity(intent)
        }
        val hintsBtn = findViewById<Button>(R.id.hintsBtn)
        hintsBtn.setOnClickListener {
            val intent = Intent(this@MainActivity, ChooseGameActivity::class.java)
            startActivity(intent)
        }


        // END Button management
    }

    // ---------------------------------------------------------------------------------------------
    //                                          MENU STUFF
    // ---------------------------------------------------------------------------------------------
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.mainactivity_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        return if (id == R.id.action_settings) {
            true
        } else super.onOptionsItemSelected(item)
    }
}