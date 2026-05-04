package eu.indiewalkabout.mathbrainer.feat_games.feat_count_items.presentation.ui

import eu.indiewalkabout.mathbrainer.feat_games.FakeMathBrainerRepository
import eu.indiewalkabout.mathbrainer.feat_games.feat_count_items.domain.use_cases.GenerateCountObjectsChallengeUseCase
import eu.indiewalkabout.mathbrainer.feat_games.feat_count_items.domain.use_cases.UpdateCountObjectsScoreUseCase
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
class CountObjectsViewModelTest {

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
    fun `refresh and start load saved high score and challenge`() = runTest(dispatcher) {
        val repository = FakeMathBrainerRepository(
            initialStats = listOf(GameStats(gameId = "quick_count", highScore = 29))
        )
        val viewModel = createViewModel(repository)

        viewModel.refreshGameStat("quick_count", 0)
        runCurrent()
        viewModel.startGame()
        runCurrent()

        val state = viewModel.uiState.value
        assertEquals(29, state.highScore)
        assertTrue(state.isShowingItems)
        assertFalse(state.isGameOver)
    }

    @Test
    fun `submitting the correct answer increases score`() = runTest(dispatcher) {
        val repository = FakeMathBrainerRepository()
        val viewModel = createViewModel(repository)

        viewModel.refreshGameStat("quick_count", 0)
        runCurrent()
        viewModel.startGame()
        runCurrent()
        advanceTimeBy(2_000)
        runCurrent()

        val answer = viewModel.uiState.value.challenge!!.itemsToCount
        viewModel.submitAnswer(answer)

        val state = viewModel.uiState.value
        assertEquals(25, state.score)
        assertEquals(25, state.highScore)
        assertTrue(state.showNextButton)
    }

    @Test
    fun `quit persists count objects score only once`() = runTest(dispatcher) {
        val repository = FakeMathBrainerRepository()
        val viewModel = createViewModel(repository)

        viewModel.refreshGameStat("quick_count", 0)
        runCurrent()
        viewModel.startGame()
        runCurrent()
        advanceTimeBy(2_000)
        runCurrent()

        val answer = viewModel.uiState.value.challenge!!.itemsToCount
        viewModel.submitAnswer(answer)

        viewModel.onQuitGame()
        runCurrent()
        viewModel.onQuitGame()
        runCurrent()

        assertEquals(1, repository.insertedScoresCount)
        assertEquals(1, repository.insertedStatsCount)
    }

    private fun createViewModel(repository: FakeMathBrainerRepository): CountObjectsViewModel {
        return CountObjectsViewModel(
            generateCountObjectsChallengeUseCase = GenerateCountObjectsChallengeUseCase(),
            updateCountObjectsScoreUseCase = UpdateCountObjectsScoreUseCase(repository),
            getGameStatsUseCase = GetGameStatsUseCase(repository),
            updateGameStatsUseCase = UpdateGameStatsUseCase(repository)
        )
    }
}
