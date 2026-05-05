package eu.indiewalkabout.mathbrainer.feat_games.feat_sequence_complete.presentation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.indiewalkabout.mathbrainer.core.presentation.state.ChallengeUiState
import eu.indiewalkabout.mathbrainer.feat_games.feat_sequence_complete.domain.model.SequenceChallenge
import eu.indiewalkabout.mathbrainer.feat_games.feat_sequence_complete.domain.model.SequenceConfig
import eu.indiewalkabout.mathbrainer.feat_games.feat_sequence_complete.domain.use_cases.GenerateSequenceChallengeUseCase
import eu.indiewalkabout.mathbrainer.feat_games.feat_sequence_complete.domain.use_cases.UpdateSequenceCompleteScoreUseCase
import eu.indiewalkabout.mathbrainer.feat_games.feat_sequence_complete.presentation.state.SequenceCompleteUiState
import eu.indiewalkabout.mathbrainer.feat_games.shared.presentation.session.GameSessionTracker
import eu.indiewalkabout.mathbrainer.feat_home.domain.model.GameTypes
import eu.indiewalkabout.mathbrainer.feat_statistics.domain.use_cases.GetGameStatsUseCase
import eu.indiewalkabout.mathbrainer.feat_statistics.domain.use_cases.UpdateGameStatsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SequenceCompleteViewModel @Inject constructor(
    private val generateSequenceChallengeUseCase: GenerateSequenceChallengeUseCase,
    private val updateSequenceCompleteScoreUseCase: UpdateSequenceCompleteScoreUseCase,
    private val getGameStatsUseCase: GetGameStatsUseCase,
    private val updateGameStatsUseCase: UpdateGameStatsUseCase
) : ViewModel() {
    private val sessionTracker = GameSessionTracker(GameTypes.SEQUENCE_COMPLETE.id)
    private var challengesCompletedInternal = 0
    private var challengesPerLevelInternal = SequenceCompleteUiState.INITIAL_CHALLENGES_PER_LEVEL
    private var currentChallenge: SequenceChallenge? = null

    private val _uiState = MutableStateFlow(SequenceCompleteUiState())
    val uiState: StateFlow<SequenceCompleteUiState> = _uiState.asStateFlow()

    fun initialize(gameId: String, fallbackHighScore: Int = 0) {
        viewModelScope.launch {
            sessionTracker.loadStats(gameId, fallbackHighScore, getGameStatsUseCase::invoke)
            resetSessionState(fallbackHighScore)
            launchNewChallenge()
        }
    }

    fun onDigitPressed(digit: Int) {
        if (_uiState.value.isGameOver || _uiState.value.isReadyForNext) return
        _uiState.update { it.copy(inputValue = (it.inputValue + digit.toString()).take(7)) }
    }

    fun onDeletePressed() {
        if (_uiState.value.isGameOver || _uiState.value.isReadyForNext) return
        _uiState.update { current ->
            val newValue = if (current.inputValue.isNotEmpty()) current.inputValue.dropLast(1) else ""
            current.copy(inputValue = newValue)
        }
    }

    fun onSubmitPressed() {
        if (_uiState.value.isGameOver || _uiState.value.isReadyForNext) return
        val challenge = currentChallenge ?: return
        val attempt = _uiState.value.inputValue.toIntOrNull() ?: return

        if (attempt == challenge.answer) {
            handleSuccess(challenge)
        } else {
            handleFailure(challenge)
        }
    }

    fun onNextPressed() {
        if (_uiState.value.isGameOver || !_uiState.value.isReadyForNext) return
        viewModelScope.launch { launchNewChallenge() }
    }

    fun onBackPressed() {
        persistScoreIfNeeded()
    }

    private fun resetSessionState(initialHighScore: Int) {
        sessionTracker.beginSession()
        challengesCompletedInternal = 0
        challengesPerLevelInternal = SequenceCompleteUiState.INITIAL_CHALLENGES_PER_LEVEL
        currentChallenge = null
        _uiState.value = SequenceCompleteUiState(
            highScore = sessionTracker.sessionHighScoreOr(initialHighScore)
        )
    }

    private fun launchNewChallenge() {
        val config = buildConfig()
        val challenge = generateSequenceChallengeUseCase(config)
        currentChallenge = challenge

        _uiState.update {
            it.copy(
                visibleSequence = challenge.displaySequence,
                inputValue = "",
                feedback = null,
                ruleDescription = null,
                revealedAnswer = null,
                isReadyForNext = false
            )
        }
    }

    private fun handleSuccess(challenge: SequenceChallenge) {
        val newScore = _uiState.value.score + SCORE_INCREMENT
        val shouldLevelUp = challengesCompletedInternal + 1 >= challengesPerLevelInternal

        val (nextLevel, nextChallengesPerLevel) = if (shouldLevelUp) {
            challengesCompletedInternal = 0
            promoteLevel(_uiState.value.level)
        } else {
            challengesCompletedInternal++
            _uiState.value.level to challengesPerLevelInternal
        }

        sessionTracker.recordSuccess(newScore, nextLevel)

        _uiState.update {
            it.copy(
                feedback = ChallengeUiState.Feedback.SUCCESS,
                score = newScore,
                highScore = sessionTracker.highScore,
                challengesCompleted = challengesCompletedInternal,
                challengesPerLevel = nextChallengesPerLevel,
                level = nextLevel,
                ruleDescription = challenge.ruleDescription,
                revealedAnswer = challenge.answer,
                visibleSequence = revealSequence(challenge),
                isReadyForNext = true
            )
        }
    }

    private fun handleFailure(challenge: SequenceChallenge) {
        val remainingLives = _uiState.value.lives - 1
        sessionTracker.recordFailure(_uiState.value.level)
        _uiState.update {
            it.copy(
                lives = remainingLives,
                feedback = ChallengeUiState.Feedback.FAILURE,
                ruleDescription = challenge.ruleDescription,
                revealedAnswer = challenge.answer,
                visibleSequence = revealSequence(challenge),
                isReadyForNext = remainingLives > 0
            )
        }

        if (remainingLives <= 0) {
            onGameOver()
        }
    }

    private fun promoteLevel(currentLevel: Int): Pair<Int, Int> {
        val nextLevel = currentLevel + 1
        val nextChallengesPerLevel = (SequenceCompleteUiState.INITIAL_CHALLENGES_PER_LEVEL + (nextLevel / 2)).coerceAtMost(MAX_CHALLENGES_PER_LEVEL)
        challengesPerLevelInternal = nextChallengesPerLevel
        return nextLevel to nextChallengesPerLevel
    }

    private fun onGameOver() {
        _uiState.update { it.copy(isGameOver = true, isReadyForNext = false) }
        persistScoreIfNeeded()
    }

    private fun persistScoreIfNeeded() {
        val finalScore = _uiState.value.score
        val persistRequest = sessionTracker.buildPersistRequest(finalScore, _uiState.value.level) ?: return
        viewModelScope.launch {
            updateGameStatsUseCase(persistRequest.previousStats, persistRequest.updatedStats)
            if (persistRequest.finalScore > 0) {
                updateSequenceCompleteScoreUseCase(persistRequest.finalScore)
            }
        }
    }

    private fun buildConfig(): SequenceConfig {
        val level = _uiState.value.level
        val length = (BASE_SEQUENCE_LENGTH + (level - 1) / 2).coerceAtMost(MAX_SEQUENCE_LENGTH)
        val maxStep = (BASE_STEP_MAGNITUDE + level * STEP_GROWTH).coerceAtMost(MAX_STEP_MAGNITUDE)
        val maxStart = (BASE_START_MAX + level * START_GROWTH).coerceAtMost(MAX_START_VALUE)

        return SequenceConfig(
            level = level,
            minStart = 1,
            maxStart = maxStart,
            maxStepMagnitude = maxStep,
            length = length,
            maxValue = MAX_VALUE
        )
    }

    private fun revealSequence(challenge: SequenceChallenge): List<Int?> {
        return challenge.fullSequence.map { value -> value }
    }

    companion object {
        private const val SCORE_INCREMENT = 10
        private const val MAX_VALUE = 9_999
        private const val BASE_SEQUENCE_LENGTH = 4
        private const val MAX_SEQUENCE_LENGTH = 6
        private const val BASE_STEP_MAGNITUDE = 3
        private const val STEP_GROWTH = 2
        private const val MAX_STEP_MAGNITUDE = 12
        private const val BASE_START_MAX = 15
        private const val START_GROWTH = 12
        private const val MAX_START_VALUE = 5_000
        private const val MAX_CHALLENGES_PER_LEVEL = 10
    }
}
