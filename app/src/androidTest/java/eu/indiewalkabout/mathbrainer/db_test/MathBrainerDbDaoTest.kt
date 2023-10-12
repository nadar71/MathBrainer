package eu.indiewalkabout.mathbrainer.db_test

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import eu.indiewalkabout.mathbrainer.data.local.db.MathBrainerDatabase
import eu.indiewalkabout.mathbrainer.data.local.db.MathBrainerDbDao
import eu.indiewalkabout.mathbrainer.domain.model.results.GameResult
import junit.framework.Assert.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/*
Error with : java.lang.RuntimeException:
Delegate runner 'androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner'
for AndroidJUnit4 could not be loaded.

Check java version
 */

@RunWith(AndroidJUnit4::class)
// @RunWith(ParameterizedAndroidJUnit4::class)
class MathBrainerDbDaoTest {

    @Rule var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var mathBrainerDatabase: MathBrainerDatabase
    private lateinit var mathBrainerDatabaseDao: MathBrainerDbDao

    @Before
    fun createDb() {
        mathBrainerDatabase = Room.inMemoryDatabaseBuilder<MathBrainerDatabase>(
            InstrumentationRegistry.getInstrumentation().context,
            MathBrainerDatabase::class.java
        ).build()
        mathBrainerDatabaseDao = mathBrainerDatabase!!.mathBrainerDbDao()
    }

    @After
    // @Throws(IOException::class)
    fun closeDb() {
        mathBrainerDatabase!!.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertGameResult_test() {
        var gameResult = GameResult("global_score", 1000)
        mathBrainerDatabaseDao.insertGameResult(gameResult)

        val results = mathBrainerDatabaseDao.loadAllGamesResults() as MutableList<GameResult>
        assertEquals(results.size, 1)
    }
}
