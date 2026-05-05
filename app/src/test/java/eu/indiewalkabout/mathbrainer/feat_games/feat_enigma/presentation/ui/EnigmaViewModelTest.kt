package eu.indiewalkabout.mathbrainer.feat_games.feat_enigma.presentation.ui

import eu.indiewalkabout.mathbrainer.feat_games.FakeMathBrainerRepository
import eu.indiewalkabout.mathbrainer.feat_games.feat_enigma.domain.use_cases.GenerateEnigmaChallengeUseCase
import eu.indiewalkabout.mathbrainer.feat_games.feat_enigma.domain.use_cases.UpdateEnigmaScoreUseCase
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
class EnigmaViewModelTest {

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
            initialStats = listOf(GameStats(gameId = "enigma", highScore = 33))
        )
        val viewModel = createViewModel(repository)

        viewModel.initialize("enigma", 0)
        runCurrent()

        val state = viewModel.uiState.value
        assertEquals(33, state.highScore)
        assertFalse(state.isGameOver)
        assertEquals(1, state.level)
    }

    @Test
    fun `submitting correct answer increases score`() = runTest(dispatcher) {
        val repository = FakeMathBrainerRepository()
        val viewModel = createViewModel(repository)

        viewModel.initialize("enigma", 0)
        runCurrent()

        val answer = viewModel.uiState.value.challenge!!.answer.toString()
        answer.forEach { digit -> viewModel.onDigitPressed(digit.digitToInt()) }
        viewModel.onSubmitPressed()

        assertEquals(50, viewModel.uiState.value.score)
        assertEquals(50, viewModel.uiState.value.highScore)
    }

    @Test
    fun `quit persists enigma score only once`() = runTest(dispatcher) {
        val repository = FakeMathBrainerRepository()
        val viewModel = createViewModel(repository)

        viewModel.initialize("enigma", 0)
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

    private fun createViewModel(repository: FakeMathBrainerRepository): EnigmaViewModel {
        return EnigmaViewModel(
            generateEnigmaChallengeUseCase = GenerateEnigmaChallengeUseCase(),
            updateEnigmaScoreUseCase = UpdateEnigmaScoreUseCase(repository),
            getGameStatsUseCase = GetGameStatsUseCase(repository),
            updateGameStatsUseCase = UpdateGameStatsUseCase(repository)
        )
    }
}
