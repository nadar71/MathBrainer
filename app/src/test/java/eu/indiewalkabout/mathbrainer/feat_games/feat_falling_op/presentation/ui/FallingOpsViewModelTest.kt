package eu.indiewalkabout.mathbrainer.feat_games.feat_falling_op.presentation.ui

import eu.indiewalkabout.mathbrainer.feat_games.FakeMathBrainerRepository
import eu.indiewalkabout.mathbrainer.feat_games.feat_falling_op.domain.use_cases.GenerateFallingOperationUseCase
import eu.indiewalkabout.mathbrainer.feat_games.feat_falling_op.domain.use_cases.UpdateFallingOpsScoreUseCase
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
class FallingOpsViewModelTest {

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
    fun `initialize loads saved high score and first operation`() = runTest(dispatcher) {
        val repository = FakeMathBrainerRepository(
            initialStats = listOf(GameStats(gameId = "falling_ops", highScore = 12))
        )
        val viewModel = createViewModel(repository)

        viewModel.initialize("falling_ops", 0)
        runCurrent()

        val state = viewModel.uiState.value
        assertEquals(12, state.highScore)
        assertFalse(state.isGameOver)
        assertTrue(state.operations.isNotEmpty())
    }

    @Test
    fun `submitting correct result increases score`() = runTest(dispatcher) {
        val repository = FakeMathBrainerRepository()
        val viewModel = createViewModel(repository)

        viewModel.initialize("falling_ops", 0)
        runCurrent()

        val answer = viewModel.uiState.value.operations.first().definition.result.toString()
        answer.forEach { digit -> viewModel.onDigitPressed(digit.digitToInt()) }
        viewModel.onSubmitPressed()

        assertEquals(10, viewModel.uiState.value.score)
        assertEquals(10, viewModel.uiState.value.highScore)
    }

    @Test
    fun `quit persists falling ops score only once`() = runTest(dispatcher) {
        val repository = FakeMathBrainerRepository()
        val viewModel = createViewModel(repository)

        viewModel.initialize("falling_ops", 0)
        runCurrent()

        val answer = viewModel.uiState.value.operations.first().definition.result.toString()
        answer.forEach { digit -> viewModel.onDigitPressed(digit.digitToInt()) }
        viewModel.onSubmitPressed()
        runCurrent()

        viewModel.onBackPressed()
        runCurrent()
        viewModel.onBackPressed()
        runCurrent()

        assertEquals(1, repository.insertedScoresCount)
        assertEquals(1, repository.insertedStatsCount)
    }

    private fun createViewModel(repository: FakeMathBrainerRepository): FallingOpsViewModel {
        return FallingOpsViewModel(
            generateFallingOperationUseCase = GenerateFallingOperationUseCase(),
            updateFallingOpsScoreUseCase = UpdateFallingOpsScoreUseCase(repository),
            getGameStatsUseCase = GetGameStatsUseCase(repository),
            updateGameStatsUseCase = UpdateGameStatsUseCase(repository)
        )
    }
}
