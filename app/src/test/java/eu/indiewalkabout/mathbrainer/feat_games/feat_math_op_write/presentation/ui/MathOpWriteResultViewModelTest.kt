package eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_write.presentation.ui

import eu.indiewalkabout.mathbrainer.feat_games.FakeMathBrainerRepository
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_write.domain.use_cases.GenerateMathWriteChallengeUseCase
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_write.domain.use_cases.UpdateWriteResultScoreUseCase
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
class MathOpWriteResultViewModelTest {

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
    fun `initialize loads saved high score and starts a challenge`() = runTest(dispatcher) {
        val repository = FakeMathBrainerRepository(
            initialStats = listOf(GameStats(gameId = "sum_write", highScore = 55))
        )
        val viewModel = createViewModel(repository)

        viewModel.initialize(gameId = "sum_write", fallbackHighScore = 0)
        runCurrent()

        val state = viewModel.uiState.value
        assertEquals(55, state.highScore)
        assertFalse(state.isGameOver)
        assertEquals('+', state.challenge?.operation)
    }

    @Test
    fun `submitting the correct answer increases score`() = runTest(dispatcher) {
        val repository = FakeMathBrainerRepository()
        val viewModel = createViewModel(repository)

        viewModel.initialize(gameId = "sum_write", fallbackHighScore = 0)
        runCurrent()

        val answer = viewModel.uiState.value.challenge!!.answer.toString()
        answer.forEach { digit -> viewModel.onDigitPressed(digit.digitToInt()) }

        viewModel.onSubmitPressed()

        assertEquals(25, viewModel.uiState.value.score)
        assertEquals(25, viewModel.uiState.value.highScore)
    }

    @Test
    fun `back press persists score only once`() = runTest(dispatcher) {
        val repository = FakeMathBrainerRepository()
        val viewModel = createViewModel(repository)

        viewModel.initialize(gameId = "sum_write", fallbackHighScore = 0)
        runCurrent()

        val answer = viewModel.uiState.value.challenge!!.answer.toString()
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

    private fun createViewModel(repository: FakeMathBrainerRepository): MathOpWriteResultViewModel {
        return MathOpWriteResultViewModel(
            generateMathWriteChallengeUseCase = GenerateMathWriteChallengeUseCase(),
            updateGameStatsUseCase = UpdateGameStatsUseCase(repository),
            getGameStatsUseCase = GetGameStatsUseCase(repository),
            updateWriteResultScoreUseCase = UpdateWriteResultScoreUseCase(repository)
        )
    }
}
