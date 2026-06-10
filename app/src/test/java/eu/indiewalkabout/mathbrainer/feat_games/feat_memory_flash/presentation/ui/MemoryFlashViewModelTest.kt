package eu.indiewalkabout.mathbrainer.feat_games.feat_memory_flash.presentation.ui

import eu.indiewalkabout.mathbrainer.feat_games.FakeMathBrainerRepository
import eu.indiewalkabout.mathbrainer.feat_games.feat_memory_flash.domain.use_cases.GenerateMemoryFlashChallengeUseCase
import eu.indiewalkabout.mathbrainer.feat_games.feat_memory_flash.domain.use_cases.UpdateMemoryFlashScoreUseCase
import eu.indiewalkabout.mathbrainer.feat_statistics.domain.model.GameStats
import eu.indiewalkabout.mathbrainer.feat_statistics.domain.use_cases.GetGameStatsUseCase
import eu.indiewalkabout.mathbrainer.feat_statistics.domain.use_cases.UpdateGameStatsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
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
class MemoryFlashViewModelTest {

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
    fun `initialize loads saved high score and starts first challenge`() = runTest(dispatcher) {
        val repository = FakeMathBrainerRepository(
            initialStats = listOf(GameStats(gameId = "memory_flash", highScore = 42))
        )
        val viewModel = createViewModel(repository)

        viewModel.initialize(gameId = "memory_flash", fallbackHighScore = 0)
        runCurrent()

        val state = viewModel.uiState.value
        assertEquals(42, state.highScore)
        assertTrue(state.visibleSequence.isNotBlank())
        assertTrue(state.isSequenceVisible)
        assertFalse(state.isGameOver)
    }

    @Test
    fun `submitting the correct answer marks challenge as success`() = runTest(dispatcher) {
        val repository = FakeMathBrainerRepository()
        val viewModel = createViewModel(repository)

        viewModel.initialize(gameId = "memory_flash", fallbackHighScore = 0)
        runCurrent()
        advanceTimeBy(900)
        runCurrent()

        val answer = viewModel.uiState.value.visibleSequence.filter(Char::isDigit)
        answer.forEach { digit -> viewModel.onDigitPressed(digit.digitToInt()) }

        viewModel.onSubmitPressed()

        val state = viewModel.uiState.value
        assertEquals(30, state.score)
        assertTrue(state.isReadyForNext)
        assertEquals(30, state.highScore)
    }

    @Test
    fun `back press persists score only once`() = runTest(dispatcher) {
        val repository = FakeMathBrainerRepository()
        val viewModel = createViewModel(repository)

        viewModel.initialize(gameId = "memory_flash", fallbackHighScore = 0)
        runCurrent()
        advanceTimeBy(900)
        runCurrent()

        val answer = viewModel.uiState.value.visibleSequence.filter(Char::isDigit)
        answer.forEach { digit -> viewModel.onDigitPressed(digit.digitToInt()) }
        viewModel.onSubmitPressed()

        viewModel.onBackPressed()
        runCurrent()
        viewModel.onBackPressed()
        runCurrent()

        assertEquals(1, repository.insertedScoresCount)
        assertEquals(1, repository.insertedStatsCount)
    }

    private fun createViewModel(repository: FakeMathBrainerRepository): MemoryFlashViewModel {
        return MemoryFlashViewModel(
            generateMemoryFlashChallengeUseCase = GenerateMemoryFlashChallengeUseCase(),
            updateMemoryFlashScoreUseCase = UpdateMemoryFlashScoreUseCase(repository),
            getGameStatsUseCase = GetGameStatsUseCase(repository),
            updateGameStatsUseCase = UpdateGameStatsUseCase(repository)
        )
    }
}
