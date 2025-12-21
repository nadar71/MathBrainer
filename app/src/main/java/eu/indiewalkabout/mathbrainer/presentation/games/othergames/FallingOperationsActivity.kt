package eu.indiewalkabout.mathbrainer.presentation.games.othergames

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.unity3d.services.banners.BannerView
import com.unity3d.services.banners.UnityBannerSize
import eu.indiewalkabout.mathbrainer.R
import eu.indiewalkabout.mathbrainer.core.unityads.bannerListener
import eu.indiewalkabout.mathbrainer.core.util.GameOverDialog
import eu.indiewalkabout.mathbrainer.core.util.IGameFunctions
import eu.indiewalkabout.mathbrainer.core.util.MyKeyboard
import eu.indiewalkabout.mathbrainer.databinding.ActivityFallingOperationsBinding
import eu.indiewalkabout.mathbrainer.domain.model.results.Results
import eu.indiewalkabout.mathbrainer.presentation.games.othergames.model.FallingOperation
import eu.indiewalkabout.mathbrainer.presentation.ui.ChooseGameActivity

class FallingOperationsActivity : AppCompatActivity(), IGameFunctions {
    private lateinit var binding: ActivityFallingOperationsBinding
    private lateinit var viewModel: FallingOperationsViewModel
    private lateinit var livesValueIv: ArrayList<ImageView>
    private lateinit var keyboard: MyKeyboard
    private lateinit var gameOverDialog: GameOverDialog

    private val handler = Handler(Looper.getMainLooper())
    private var lastSpawnTime = 0L
    private var isRunning = false

    private var bottomBanner: BannerView? = null

    private val gameLoop = object : Runnable {
        override fun run() {
            if (!isRunning) return
            val now = System.currentTimeMillis()
            if (lastSpawnTime == 0L) {
                lastSpawnTime = now
            }
            val rectWidth = binding.fallingOpsView.operationWidth()
            val rectHeight = binding.fallingOpsView.operationHeight()
            if (now - lastSpawnTime >= viewModel.spawnIntervalMs()) {
                viewModel.addOperation(binding.fallingOpsView.width, rectWidth, -rectHeight)
                lastSpawnTime = now
            }
            val hitBottom = viewModel.advancePositions(rectHeight, binding.fallingOpsView.height.toFloat())
            if (hitBottom) {
                Results.incrementGameResultsThread("lives_missed")
                if (viewModel.lives.value == 0) {
                    endGame()
                    return
                }
            }
            handler.postDelayed(this, FRAME_DELAY)
        }
    }

    override val isGameOver: Boolean
        get() = viewModel.lives.value == 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_falling_operations)

        viewModel = ViewModelProvider(this).get(FallingOperationsViewModel::class.java)

        livesValueIv = arrayListOf(
            findViewById(R.id.life_01_iv),
            findViewById(R.id.life_02_iv),
            findViewById(R.id.life_03_iv)
        )

        setupCustomKeyboard()
        setupObservers()
        showHighscore()

        binding.backhomeImg.setOnClickListener {
            isComingHome()
            startActivity(Intent(this@FallingOperationsActivity, ChooseGameActivity::class.java))
        }

        bottomBanner = BannerView(this, "banner", UnityBannerSize(320, 50))
        bottomBanner?.listener = bannerListener
        bottomBanner?.load()
        binding.bannerLayout.addView(bottomBanner)

        hideStatusNavBars()
    }

    override fun onResume() {
        super.onResume()
        hideStatusNavBars()
        startGameLoop()
    }

    override fun onPause() {
        super.onPause()
        stopGameLoop()
    }

    private fun setupObservers() {
        viewModel.level.observe(this) { level ->
            binding.levelValueTv.text = level.toString()
        }
        viewModel.score.observe(this) { score ->
            binding.scoreValueTv.text = score.toString()
        }
        viewModel.lives.observe(this) { lives ->
            livesValueIv.forEachIndexed { index, imageView ->
                imageView.visibility = if (index < lives) View.VISIBLE else View.INVISIBLE
            }
        }
        viewModel.progress.observe(this) { progress ->
            binding.countdownBar.progress = progress
        }
        viewModel.progressTarget.observe(this) { target ->
            binding.countdownBar.max = target
        }
        viewModel.operationsState.observe(this) { operations ->
            binding.fallingOpsView.updateOperations(operations)
        }
    }

    private fun showHighscore() {
        val intent = intent
        val highscore = if (intent.hasExtra(ChooseGameActivity.HIGHSCORE)) {
            intent.getIntExtra(ChooseGameActivity.HIGHSCORE, 0)
        } else {
            0
        }
        binding.highscoreValueTv.text = highscore.toString()
    }

    private fun setupCustomKeyboard() {
        keyboard = findViewById(R.id.keyboard)

        binding.playerInputEt.setOnTouchListener { _, event ->
            val inType = binding.playerInputEt.inputType
            binding.playerInputEt.inputType = InputType.TYPE_NULL
            binding.playerInputEt.onTouchEvent(event)
            binding.playerInputEt.inputType = inType
            binding.playerInputEt.setTextIsSelectable(false)
            true
        }

        val ic = binding.playerInputEt.onCreateInputConnection(EditorInfo())
        keyboard.setInputConnection(ic, this@FallingOperationsActivity)
    }

    private fun startGameLoop() {
        if (isRunning) return
        isRunning = true
        binding.fallingOpsView.post {
            lastSpawnTime = 0L
            handler.post(gameLoop)
        }
    }

    private fun stopGameLoop() {
        isRunning = false
        handler.removeCallbacks(gameLoop)
    }

    private fun endGame() {
        stopGameLoop()
        Results.incrementGameResultsThread("games_played")
        Results.incrementGameResultsThread("games_lose")
        Results.updateGameResultHighscoreThread("falling_ops_game_score", viewModel.score.value ?: 0)
        Results.incrementGameResultByDeltaThread("global_score", viewModel.score.value ?: 0)
        gameOverDialog = GameOverDialog(this, this@FallingOperationsActivity, this)
    }

    private fun isComingHome() {
        Results.updateGameResultHighscoreThread("falling_ops_game_score", viewModel.score.value ?: 0)
        Results.incrementGameResultByDeltaThread("global_score", viewModel.score.value ?: 0)
    }

    private fun recordOperationStats(removed: List<FallingOperation>) {
        val sums = removed.count { it.operator == '+' }
        val differences = removed.count { it.operator == '-' }
        val multiplications = removed.count { it.operator == '*' }
        val divisions = removed.count { it.operator == '/' }
        if (sums > 0) {
            Results.incrementGameResultByDeltaThread("sums", sums)
        }
        if (differences > 0) {
            Results.incrementGameResultByDeltaThread("differences", differences)
        }
        if (multiplications > 0) {
            Results.incrementGameResultByDeltaThread("multiplications", multiplications)
        }
        if (divisions > 0) {
            Results.incrementGameResultByDeltaThread("divisions", divisions)
        }
    }

    override fun updateProgressBar(progress: Int) {
        binding.countdownBar.progress = progress
    }

    override fun newChallenge() {
        val rectWidth = binding.fallingOpsView.operationWidth()
        val rectHeight = binding.fallingOpsView.operationHeight()
        viewModel.addOperation(binding.fallingOpsView.width, rectWidth, -rectHeight)
    }

    override fun checkPlayerInput() {
        val input = binding.playerInputEt.text.toString()
        if (input.isEmpty()) {
            return
        }
        val value = input.toIntOrNull() ?: return
        val result = viewModel.consumeInput(value)
        Results.incrementGameResultsThread("operations_executed")
        if (result.matched) {
            Results.incrementGameResultsThread("operations_ok")
            recordOperationStats(result.removedOperations)
            if (result.leveledUp) {
                Results.incrementGameResultsThread("level_upgrades")
            }
        } else {
            Results.incrementGameResultsThread("operations_ko")
        }
        binding.playerInputEt.text.clear()
    }

    override fun checkCountdownExpired() {
    }

    private fun hideStatusNavBars() {
        val decorView = window.decorView
        val uiOptions = (
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_FULLSCREEN
            )
        decorView.systemUiVisibility = uiOptions
    }

    companion object {
        private const val FRAME_DELAY = 16L
    }
}
