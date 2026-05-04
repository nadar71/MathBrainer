package eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_double.presentation.ui

import eu.indiewalkabout.mathbrainer.feat_games.FakeMathBrainerRepository
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_double.domain.use_cases.GenerateDoubleNumberChallengeUseCase
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_double.domain.use_cases.UpdateDoubleNumberScoreUseCase
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
class DoubleNumberViewModelTest {

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
            initialStats = listOf(GameStats(gameId = "double", highScore = 18))
        )
        val viewModel = createViewModel(repository)

        viewModel.refreshGameStat("double", 0)
        runCurrent()
        viewModel.startGame()
        runCurrent()

        val state = viewModel.uiState.value
        assertEquals(18, state.highScore)
        assertFalse(state.isGameOver)
        assertEquals(1, state.level)
    }

    @Test
    fun `submitting the correct answer increases score`() = runTest(dispatcher) {
        val repository = FakeMathBrainerRepository()
        val viewModel = createViewModel(repository)

        viewModel.refreshGameStat("double", 0)
        runCurrent()
        viewModel.startGame()
        runCurrent()

        val answer = viewModel.uiState.value.challenge!!.answer.toString()
        answer.forEach { digit -> viewModel.onDigitPressed(digit.digitToInt()) }
        viewModel.submitAnswer()

        assertEquals(25, viewModel.uiState.value.score)
        assertEquals(25, viewModel.uiState.value.highScore)
    }

    @Test
    fun `quit persists double number score only once`() = runTest(dispatcher) {
        val repository = FakeMathBrainerRepository()
        val viewModel = createViewModel(repository)

        viewModel.refreshGameStat("double", 0)
        runCurrent()
        viewModel.startGame()
        runCurrent()

        val answer = viewModel.uiState.value.challenge!!.answer.toString()
        answer.forEach { digit -> viewModel.onDigitPressed(digit.digitToInt()) }
        viewModel.submitAnswer()
        runCurrent()

        viewModel.onQuitGame()
        runCurrent()
        viewModel.onQuitGame()
        runCurrent()

        assertEquals(1, repository.insertedScoresCount)
        assertEquals(1, repository.insertedStatsCount)
    }

    private fun createViewModel(repository: FakeMathBrainerRepository): DoubleNumberViewModel {
        return DoubleNumberViewModel(
            generateDoubleNumberChallengeUseCase = GenerateDoubleNumberChallengeUseCase(),
            updateDoubleNumberScoreUseCase = UpdateDoubleNumberScoreUseCase(repository),
            getGameStatsUseCase = GetGameStatsUseCase(repository),
            updateGameStatsUseCase = UpdateGameStatsUseCase(repository)
        )
    }
}
