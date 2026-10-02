package eu.indiewalkabout.mathbrainer.feat_statistics.data.local.db

import android.content.Context
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import eu.indiewalkabout.mathbrainer.feat_statistics.domain.model.GameScores
import eu.indiewalkabout.mathbrainer.feat_statistics.domain.model.GameStatistics
import eu.indiewalkabout.mathbrainer.feat_statistics.domain.model.GameStats
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MathBrainerMigrationTest {

    @get:Rule
    val migrationHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        MathBrainerDatabase::class.java,
        emptyList(),
        FrameworkSQLiteOpenHelperFactory()
    )

    private val context: Context = ApplicationProvider.getApplicationContext()

    @After
    fun tearDown() {
        context.deleteDatabase(CURRENT_SCHEMA_DB)
        context.deleteDatabase(UNKNOWN_VERSION_DB)
        context.deleteDatabase(DAO_DB)
    }

    @Test
    fun version10SchemaOpensThroughProductionBuilderWithoutLosingScoresOrStatistics() = runBlocking {
        val expectedScores = gameScores()
        val expectedStatistics = gameStatistics()
        val expectedGameStats = gameStats()

        migrationHelper.createDatabase(CURRENT_SCHEMA_DB, 10).apply {
            insertScores(this, expectedScores)
            insertStatistics(this, expectedStatistics)
            insertGameStats(this, expectedGameStats)
            close()
        }

        useDatabase(CURRENT_SCHEMA_DB) { database ->
            val dao = database.mathBrainerDbDao()

            assertEquals(expectedScores, dao.observeGameScores().first())
            assertEquals(expectedStatistics, dao.loadGameStatistics())
            assertEquals(listOf(expectedGameStats), dao.observeGameStats().first())
        }
    }

    @Test
    fun unknownPriorVersionFailsWithoutErasingExistingGameStats() {
        migrationHelper.createDatabase(UNKNOWN_VERSION_DB, 10).apply {
            insertGameStats(this, gameStats())
            execSQL("PRAGMA user_version = 9")
            close()
        }

        val exception = try {
            val database = MathBrainerDatabase.createDatabase(context, UNKNOWN_VERSION_DB)
            try {
                database.openHelper.writableDatabase
            } finally {
                database.close()
            }
            throw AssertionError("Expected an unknown database version to be rejected")
        } catch (error: IllegalStateException) {
            error
        }

        assertTrue(exception.message.orEmpty().contains("Migration"))
        context.openOrCreateDatabase(UNKNOWN_VERSION_DB, Context.MODE_PRIVATE, null).use { database ->
            database.rawQuery("SELECT gameId, highScore, challengesPlayed, challengesWon, challengesLost, lastLevel FROM GameStats", null)
                .use { cursor ->
                    assertTrue(cursor.moveToFirst())
                    assertEquals("number_order", cursor.getString(0))
                    assertEquals(900, cursor.getInt(1))
                    assertEquals(12, cursor.getInt(2))
                    assertEquals(10, cursor.getInt(3))
                    assertEquals(2, cursor.getInt(4))
                    assertEquals(7, cursor.getInt(5))
                }
        }
    }

    @Test
    fun freshDatabaseInitializesStatisticsTablesEmpty() = runBlocking {
        useDatabase(DAO_DB) { database ->
            val dao = database.mathBrainerDbDao()

            assertNull(dao.observeGameScores().first())
            assertEquals(emptyList<GameStats>(), dao.observeGameStats().first())
        }
    }

    @Test
    fun gameStatsUpsertReplacesTheSavedRow() = runBlocking {
        useDatabase(DAO_DB) { database ->
            val dao = database.mathBrainerDbDao()
            dao.insertGameStats(gameStats().copy(highScore = 100, challengesPlayed = 4))
            val replacement = gameStats()

            dao.insertGameStats(replacement)

            assertEquals(replacement, dao.getGameStats("number_order"))
        }
    }

    @Test
    fun gameStatsObservationEmitsInsertedValues() = runBlocking {
        useDatabase(DAO_DB) { database ->
            val dao = database.mathBrainerDbDao()
            val expected = gameStats()

            dao.insertGameStats(expected)

            assertEquals(listOf(expected), dao.observeGameStats().first())
        }
    }

    @Test
    fun clearingStatisticsRemovesScoresCumulativeStatisticsAndGameStats() = runBlocking {
        useDatabase(DAO_DB) { database ->
            val dao = database.mathBrainerDbDao()
            dao.insertGameScores(gameScores())
            dao.insertGameStatistics(gameStatistics())
            dao.insertGameStats(gameStats())

            dao.dropTableGameScores()
            dao.dropTableGameStatistics()
            dao.dropTableGameStats()

            assertNull(dao.observeGameScores().first())
            assertEquals(emptyList<GameStats>(), dao.observeGameStats().first())
            database.openHelper.writableDatabase.query("SELECT COUNT(*) FROM GameStatistics").use { cursor ->
                assertTrue(cursor.moveToFirst())
                assertEquals(0, cursor.getInt(0))
            }
        }
    }

    private fun insertScores(database: androidx.sqlite.db.SupportSQLiteDatabase, scores: GameScores) {
        database.execSQL(
            """
                INSERT INTO GameScores (
                    id, global_score, doublenumber_game_score, sum_choose_result_game_score,
                    diff_choose_result_game_score, mult_choose_result_game_score,
                    div_choose_result_game_score, mix_choose_result_game_score,
                    sum_write_result_game_score, diff_write_result_game_score,
                    mult_write_result_game_score, div_write_result_game_score,
                    mix_write_result_game_score, random_op_game_score, count_objects_game_score,
                    number_order_game_score, memory_flash_game_score, sequence_complete_game_score,
                    falling_ops_game_score, enigma_game_score
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """.trimIndent(),
            arrayOf(
                scores.id, scores.global_score, scores.doublenumber_game_score,
                scores.sum_choose_result_game_score, scores.diff_choose_result_game_score,
                scores.mult_choose_result_game_score, scores.div_choose_result_game_score,
                scores.mix_choose_result_game_score, scores.sum_write_result_game_score,
                scores.diff_write_result_game_score, scores.mult_write_result_game_score,
                scores.div_write_result_game_score, scores.mix_write_result_game_score,
                scores.random_op_game_score, scores.count_objects_game_score,
                scores.number_order_game_score, scores.memory_flash_game_score,
                scores.sequence_complete_game_score, scores.falling_ops_game_score,
                scores.enigma_game_score
            )
        )
    }

    private fun insertStatistics(
        database: androidx.sqlite.db.SupportSQLiteDatabase,
        statistics: GameStatistics
    ) {
        database.execSQL(
            """
                INSERT INTO GameStatistics (
                    id, operations_executed, operations_ok, operations_ko, sums, differences,
                    multiplications, divisions, doublings, level_upgrades, lives_missed,
                    objects_counted, numbers_in_order, games_played, games_lose
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """.trimIndent(),
            arrayOf(
                statistics.id, statistics.operations_executed, statistics.operations_ok,
                statistics.operations_ko, statistics.sums, statistics.differences,
                statistics.multiplications, statistics.divisions, statistics.doublings,
                statistics.level_upgrades, statistics.lives_missed, statistics.objects_counted,
                statistics.numbers_in_order, statistics.games_played, statistics.games_lose
            )
        )
    }

    private fun insertGameStats(
        database: androidx.sqlite.db.SupportSQLiteDatabase,
        stats: GameStats
    ) {
        database.execSQL(
            """
                INSERT INTO GameStats (gameId, highScore, challengesPlayed, challengesWon, challengesLost, lastLevel)
                VALUES (?, ?, ?, ?, ?, ?)
            """.trimIndent(),
            arrayOf(
                stats.gameId, stats.highScore, stats.challengesPlayed, stats.challengesWon,
                stats.challengesLost, stats.lastLevel
            )
        )
    }

    private fun gameScores() = GameScores(
        id = 1,
        global_score = 1000,
        doublenumber_game_score = 10,
        sum_choose_result_game_score = 20,
        diff_choose_result_game_score = 30,
        mult_choose_result_game_score = 40,
        div_choose_result_game_score = 50,
        mix_choose_result_game_score = 60,
        sum_write_result_game_score = 70,
        diff_write_result_game_score = 80,
        mult_write_result_game_score = 90,
        div_write_result_game_score = 100,
        mix_write_result_game_score = 110,
        random_op_game_score = 120,
        count_objects_game_score = 130,
        number_order_game_score = 140,
        memory_flash_game_score = 150,
        sequence_complete_game_score = 160,
        falling_ops_game_score = 170,
        enigma_game_score = 180
    )

    private fun gameStatistics() = GameStatistics(
        id = 1,
        operations_executed = 200,
        operations_ok = 180,
        operations_ko = 20,
        sums = 21,
        differences = 22,
        multiplications = 23,
        divisions = 24,
        doublings = 25,
        level_upgrades = 26,
        lives_missed = 27,
        objects_counted = 28,
        numbers_in_order = 29,
        games_played = 30,
        games_lose = 31
    )

    private fun gameStats() = GameStats(
        gameId = "number_order",
        highScore = 900,
        challengesPlayed = 12,
        challengesWon = 10,
        challengesLost = 2,
        lastLevel = 7
    )

    private suspend fun <T> useDatabase(
        databaseName: String,
        block: suspend (MathBrainerDatabase) -> T
    ): T {
        val database = MathBrainerDatabase.createDatabase(context, databaseName)
        return try {
            block(database)
        } finally {
            database.close()
        }
    }

    private companion object {
        const val CURRENT_SCHEMA_DB = "migration-current-v10.db"
        const val UNKNOWN_VERSION_DB = "migration-unknown-v9.db"
        const val DAO_DB = "dao-behavior.db"
    }
}
