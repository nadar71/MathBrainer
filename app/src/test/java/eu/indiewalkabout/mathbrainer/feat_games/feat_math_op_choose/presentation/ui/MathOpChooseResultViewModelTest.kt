package eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_choose.presentation.ui

import eu.indiewalkabout.mathbrainer.feat_games.FakeMathBrainerRepository
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_choose.domain.use_cases.GenerateMathChooseChallengeUseCase
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_choose.domain.use_cases.UpdateChooseResultScoreUseCase
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
class MathOpChooseResultViewModelTest {

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
            initialStats = listOf(GameStats(gameId = "sum_choose", highScore = 18))
        )
        val viewModel = createViewModel(repository)

        viewModel.initialize("sum_choose", 0)
        runCurrent()

        val state = viewModel.uiState.value
        assertEquals(18, state.highScore)
        assertFalse(state.isGameOver)
        assertEquals(1, state.level)
        assertEquals(20_000L, state.timeRemaining)
    }

    @Test
    fun `selecting correct option increases score`() = runTest(dispatcher) {
        val repository = FakeMathBrainerRepository()
        val viewModel = createViewModel(repository)

        viewModel.initialize("sum_choose", 0)
        runCurrent()

        val correctAnswer = viewModel.uiState.value.challenge!!.correctAnswer
        viewModel.onOptionSelected(correctAnswer)

        assertEquals(25, viewModel.uiState.value.score)
        assertEquals(25, viewModel.uiState.value.highScore)
    }

    @Test
    fun `quit persists choose result score only once`() = runTest(dispatcher) {
        val repository = FakeMathBrainerRepository()
        val viewModel = createViewModel(repository)

        viewModel.initialize("sum_choose", 0)
        runCurrent()

        val correctAnswer = viewModel.uiState.value.challenge!!.correctAnswer
        viewModel.onOptionSelected(correctAnswer)
        runCurrent()

        viewModel.onBackPressed()
        runCurrent()
        viewModel.onBackPressed()
        runCurrent()

        assertEquals(1, repository.insertedScoresCount)
        assertEquals(1, repository.insertedStatsCount)
    }

    private fun createViewModel(repository: FakeMathBrainerRepository): MathOpChooseResultViewModel {
        return MathOpChooseResultViewModel(
            generateMathChooseChallengeUseCase = GenerateMathChooseChallengeUseCase(),
            updateChooseResultScoreUseCase = UpdateChooseResultScoreUseCase(repository),
            getGameStatsUseCase = GetGameStatsUseCase(repository),
            updateGameStatsUseCase = UpdateGameStatsUseCase(repository)
        )
    }
}
