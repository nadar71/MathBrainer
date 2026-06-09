package eu.indiewalkabout.mathbrainer.feat_home.domain.use_cases

import eu.indiewalkabout.mathbrainer.feat_home.domain.model.GameDefinition
import eu.indiewalkabout.mathbrainer.feat_statistics.domain.model.GameScores
import eu.indiewalkabout.mathbrainer.feat_statistics.domain.model.GameStats
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class BuildGameCatalogUiModelsUseCaseTest {

    private val useCase = BuildGameCatalogUiModelsUseCase()

    @Test
    fun `maps game definitions to ui models with scores and stats`() {
        val definitions = listOf(
            GameDefinition(id = "sum_choose"),
            GameDefinition(id = "memory_flash")
        )
        val scores = GameScores.emptyScores.copy(
            sum_choose_result_game_score = 12,
            memory_flash_game_score = 34
        )
        val stats = mapOf(
            "sum_choose" to GameStats(gameId = "sum_choose", highScore = 12, challengesPlayed = 3),
            "memory_flash" to GameStats(gameId = "memory_flash", highScore = 34, challengesPlayed = 7)
        )

        val result = useCase(
            definitions = definitions,
            scores = scores,
            stats = stats
        )

        assertEquals(2, result.size)
        assertEquals(12, result[0].highScore)
        assertEquals(3, result[0].gameStats?.challengesPlayed)
        assertEquals(34, result[1].highScore)
        assertEquals(7, result[1].gameStats?.challengesPlayed)
    }

    @Test
    fun `returns null score and default stats when no mapping exists`() {
        val definitions = listOf(GameDefinition(id = "unknown_game"))

        val result = useCase(
            definitions = definitions,
            scores = GameScores.emptyScores,
            stats = emptyMap()
        )

        assertEquals(1, result.size)
        assertNull(result[0].highScore)
        assertEquals("unknown_game", result[0].gameStats?.gameId)
        assertEquals(0, result[0].gameStats?.highScore)
    }
}
