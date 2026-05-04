package eu.indiewalkabout.mathbrainer.feat_games.feat_math_random_operation.presentation.ui

import eu.indiewalkabout.mathbrainer.feat_games.FakeMathBrainerRepository
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_random_operation.domain.use_cases.GenerateRandomOperationChallengeUseCase
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_random_operation.domain.use_cases.UpdateRandomOperationScoreUseCase
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
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RandomOperationViewModelTest {

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
            initialStats = listOf(GameStats(gameId = "random", highScore = 44))
        )
        val viewModel = createViewModel(repository)

        viewModel.refreshGameStat("random", 0)
        runCurrent()
        viewModel.startGame()
        runCurrent()

        val state = viewModel.uiState.value
        assertEquals(44, state.highScore)
        assertFalse(state.isGameOver)
        assertEquals(1, state.level)
    }

    @Test
    fun `selecting the correct operation increases score`() = runTest(dispatcher) {
        val repository = FakeMathBrainerRepository()
        val viewModel = createViewModel(repository)

        viewModel.refreshGameStat("random", 0)
        runCurrent()
        viewModel.startGame()
        runCurrent()

        val correctOperation = viewModel.uiState.value.challenge!!.correctOperation
        viewModel.onOperationSelected(correctOperation)

        assertEquals(25, viewModel.uiState.value.score)
        assertEquals(25, viewModel.uiState.value.highScore)
    }

    @Test
    fun `quit persists random operation score only once`() = runTest(dispatcher) {
        val repository = FakeMathBrainerRepository()
        val viewModel = createViewModel(repository)

        viewModel.refreshGameStat("random", 0)
        runCurrent()
        viewModel.startGame()
        runCurrent()

        val correctOperation = viewModel.uiState.value.challenge!!.correctOperation
        viewModel.onOperationSelected(correctOperation)
        runCurrent()

        viewModel.onQuitGame()
        runCurrent()
        viewModel.onQuitGame()
        runCurrent()

        assertEquals(1, repository.insertedScoresCount)
        assertEquals(1, repository.insertedStatsCount)
    }

    private fun createViewModel(repository: FakeMathBrainerRepository): RandomOperationViewModel {
        return RandomOperationViewModel(
            generateRandomOperationChallengeUseCase = GenerateRandomOperationChallengeUseCase(),
            updateRandomOperationScoreUseCase = UpdateRandomOperationScoreUseCase(repository),
            getGameStatsUseCase = GetGameStatsUseCase(repository),
            updateGameStatsUseCase = UpdateGameStatsUseCase(repository)
        )
    }
}
