package eu.indiewalkabout.mathbrainer.presentation.games.othergames

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import eu.indiewalkabout.mathbrainer.R
import eu.indiewalkabout.mathbrainer.core.util.ConsentSDK.Companion.getAdRequest
import eu.indiewalkabout.mathbrainer.databinding.ActivitySoundsSeqBinding

// TODO  : to be implemented
class SoundsSeqActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySoundsSeqBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_sounds_seq)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sounds_seq)

        // You have to pass the AdRequest from ConsentSDK.getAdRequest(this) because it handle the right way to load the ad
        binding.mAdView.loadAd(getAdRequest(this@SoundsSeqActivity))
    }
}
