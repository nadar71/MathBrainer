package eu.indiewalkabout.mathbrainer;


import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.room.Room;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import eu.indiewalkabout.mathbrainer.statistics.GameResult;
import eu.indiewalkabout.mathbrainer.statistics.MathBrainerDatabase;
import eu.indiewalkabout.mathbrainer.statistics.MathBrainerDbDao;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class MathBrainerDbDaoTest_java {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private MathBrainerDatabase mathBrainerDatabase;
    private MathBrainerDbDao mathBrainerDatabaseDao;


    @Before
    public void createDb() {
        mathBrainerDatabase = Room.inMemoryDatabaseBuilder(
                InstrumentationRegistry.getInstrumentation().getContext(),
                MathBrainerDatabase.class).build();
        mathBrainerDatabaseDao = mathBrainerDatabase.MathBrainerDbDao();

    }

    @After
    public void closeDb() throws IOException {

        mathBrainerDatabase.close();
    }


    @Test
    public void insertGameResult_test() throws Exception {

        GameResult gameResult = new GameResult("global_score", 1000);
        mathBrainerDatabaseDao.insertGameResult(gameResult);

        LiveData<List<GameResult>> results =  mathBrainerDatabaseDao.loadAllGamesResults();
        Observer<List<GameResult>> observer = ids -> assertEquals(1, ids.size());
        results.observeForever(observer);

    }




}
