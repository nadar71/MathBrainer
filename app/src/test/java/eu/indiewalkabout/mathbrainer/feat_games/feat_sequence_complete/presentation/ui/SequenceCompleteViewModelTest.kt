package eu.indiewalkabout.mathbrainer.feat_games.feat_sequence_complete.presentation.ui

import eu.indiewalkabout.mathbrainer.feat_games.FakeMathBrainerRepository
import eu.indiewalkabout.mathbrainer.feat_games.feat_sequence_complete.domain.model.SequenceChallenge
import eu.indiewalkabout.mathbrainer.feat_games.feat_sequence_complete.domain.use_cases.GenerateSequenceChallengeUseCase
import eu.indiewalkabout.mathbrainer.feat_games.feat_sequence_complete.domain.use_cases.UpdateSequenceCompleteScoreUseCase
import eu.indiewalkabout.mathbrainer.feat_statistics.domain.model.GameStats
import eu.indiewalkabout.mathbrainer.feat_statistics.domain.use_cases.GetGameStatsUseCase
import eu.indiewalkabout.mathbrainer.feat_statistics.domain.use_cases.UpdateGameStatsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SequenceCompleteViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `refresh and start load saved high score and first sequence`() = runTest(dispatcher) {
        val repository = FakeMathBrainerRepository(
            initialStats = listOf(GameStats(gameId = "sequence_complete", highScore = 37))
        )
        val viewModel = createViewModel(repository)

        viewModel.refreshGameStat("sequence_complete", 0)
        runCurrent()
        viewModel.startGame()
        runCurrent()

        val state = viewModel.uiState.value
        assertEquals(37, state.highScore)
        assertTrue(state.visibleSequence.isNotEmpty())
        assertFalse(state.isGameOver)
    }

    @Test
    fun `submitting the correct answer marks sequence success`() = runTest(dispatcher) {
        val repository = FakeMathBrainerRepository()
        val viewModel = createViewModel(repository)

        viewModel.refreshGameStat("sequence_complete", 0)
        runCurrent()
        viewModel.startGame()
        runCurrent()

        val challenge = viewModel.currentChallengeForTest()
        challenge.answer.toString().forEach { digit -> viewModel.onDigitPressed(digit.digitToInt()) }

        viewModel.submitAnswer()

        val state = viewModel.uiState.value
        assertEquals(10, state.score)
        assertEquals(10, state.highScore)
        assertTrue(state.isReadyForNext)
    }

    @Test
    fun `quit persists score only once`() = runTest(dispatcher) {
        val repository = FakeMathBrainerRepository()
        val viewModel = createViewModel(repository)

        viewModel.refreshGameStat("sequence_complete", 0)
        runCurrent()
        viewModel.startGame()
        runCurrent()

        val challenge = viewModel.currentChallengeForTest()
        challenge.answer.toString().forEach { digit -> viewModel.onDigitPressed(digit.digitToInt()) }
        viewModel.submitAnswer()

        viewModel.onQuitGame()
        runCurrent()
        viewModel.onQuitGame()
        runCurrent()

        assertEquals(1, repository.insertedScoresCount)
        assertEquals(1, repository.insertedStatsCount)
    }

    private fun createViewModel(repository: FakeMathBrainerRepository): SequenceCompleteViewModel {
        return SequenceCompleteViewModel(
            generateSequenceChallengeUseCase = GenerateSequenceChallengeUseCase(),
            updateSequenceCompleteScoreUseCase = UpdateSequenceCompleteScoreUseCase(repository),
            getGameStatsUseCase = GetGameStatsUseCase(repository),
            updateGameStatsUseCase = UpdateGameStatsUseCase(repository)
        )
    }

    private fun SequenceCompleteViewModel.currentChallengeForTest(): SequenceChallenge {
        val field = SequenceCompleteViewModel::class.java.getDeclaredField("currentChallenge")
        field.isAccessible = true
        return field.get(this) as SequenceChallenge
    }
}
