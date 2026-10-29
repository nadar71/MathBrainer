package eu.indiewalkabout.mathbrainer.feat_games.feat_number_order.presentation.ui

import eu.indiewalkabout.mathbrainer.feat_games.FakeMathBrainerRepository
import eu.indiewalkabout.mathbrainer.feat_games.feat_number_order.domain.use_cases.GenerateNumberOrderChallengeUseCase
import eu.indiewalkabout.mathbrainer.feat_games.feat_number_order.domain.use_cases.UpdateNumberOrderScoreUseCase
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
class NumberOrderViewModelTest {

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
    fun `initialize loads saved high score and challenge`() = runTest(dispatcher) {
        val repository = FakeMathBrainerRepository(
            initialStats = listOf(GameStats(gameId = "number_order", highScore = 27))
        )
        val viewModel = createViewModel(repository)

        viewModel.initialize("number_order", 0)
        runCurrent()

        val state = viewModel.uiState.value
        assertEquals(27, state.highScore)
        assertTrue(state.isMemorizing)
        assertFalse(state.isGameOver)
    }

    @Test
    fun `tapping correct order increases score`() = runTest(dispatcher) {
        val repository = FakeMathBrainerRepository()
        val viewModel = createViewModel(repository)

        viewModel.initialize("number_order", 0)
        runCurrent()
        advanceTimeBy(2_000)
        runCurrent()

        val itemCount = viewModel.uiState.value.challenge!!.itemCount
        repeat(itemCount) { index -> viewModel.onMarkerTapped(index) }

        val state = viewModel.uiState.value
        assertEquals(25, state.score)
        assertEquals(25, state.highScore)
        assertTrue(state.showNextButton)
    }

    @Test
    fun `wrong tap stays highlighted until retry starts a new round`() = runTest(dispatcher) {
        val viewModel = createViewModel(FakeMathBrainerRepository())
        viewModel.initialize("number_order", 0)
        runCurrent()
        advanceTimeBy(2_000)
        runCurrent()

        viewModel.onMarkerTapped(1)
        assertEquals(1, viewModel.uiState.value.wrongMarkerIndex)
        assertEquals(2, viewModel.uiState.value.lives)
        assertEquals(
            eu.indiewalkabout.mathbrainer.core.presentation.state.ChallengeUiState.Feedback.FAILURE,
            viewModel.uiState.value.feedback
        )
        viewModel.onMarkerTapped(0)
        assertEquals(1, viewModel.uiState.value.wrongMarkerIndex)

        viewModel.onNextPressed()
        runCurrent()
        org.junit.Assert.assertNull(viewModel.uiState.value.wrongMarkerIndex)
        org.junit.Assert.assertNull(viewModel.uiState.value.feedback)
        assertTrue(viewModel.uiState.value.isMemorizing)
    }

    @Test
    fun `quit persists number order score only once`() = runTest(dispatcher) {
        val repository = FakeMathBrainerRepository()
        val viewModel = createViewModel(repository)

        viewModel.initialize("number_order", 0)
        runCurrent()
        advanceTimeBy(2_000)
        runCurrent()

        val itemCount = viewModel.uiState.value.challenge!!.itemCount
        repeat(itemCount) { index -> viewModel.onMarkerTapped(index) }

        viewModel.onBackPressed()
        runCurrent()
        viewModel.onBackPressed()
        runCurrent()

        assertEquals(1, repository.insertedScoresCount)
        assertEquals(1, repository.insertedStatsCount)
    }

    private fun createViewModel(repository: FakeMathBrainerRepository): NumberOrderViewModel {
        return NumberOrderViewModel(
            generateNumberOrderChallengeUseCase = GenerateNumberOrderChallengeUseCase(),
            updateNumberOrderScoreUseCase = UpdateNumberOrderScoreUseCase(repository),
            getGameStatsUseCase = GetGameStatsUseCase(repository),
            updateGameStatsUseCase = UpdateGameStatsUseCase(repository)
        )
    }
}
