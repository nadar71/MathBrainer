package eu.indiewalkabout.mathbrainer.db_test;


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
        List<GameResult> currentRes = results.getValue();

        assertEquals(currentRes.get(0).getResult_name(),"global_score");
        assertEquals(currentRes.get(0).getResult_value(),1000);

    }


    @Test
    public void insertGameResults_plural_test() throws Exception {
        mathBrainerDatabaseDao.insertGameResult(
                new GameResult("sum_choose_result_game_score", 100));

        mathBrainerDatabaseDao.insertGameResult(
                new GameResult("diff_choose_result_game_score", 200));

        mathBrainerDatabaseDao.insertGameResult(
                new GameResult("mult_choose_result_game_score", 300));

        mathBrainerDatabaseDao.insertGameResult(
                new GameResult("div_choose_result_game_score", 400));


        LiveData<List<GameResult>> results =  mathBrainerDatabaseDao.loadAllGamesResults();

        Observer<List<GameResult>> observer = ids -> assertEquals(4, ids.size());
        results.observeForever(observer);
        List<GameResult> currentRes = results.getValue();

        assertEquals(currentRes.get(0).getResult_name(),"sum_choose_result_game_score");
        assertEquals(currentRes.get(0).getResult_value(),100);

        assertEquals(currentRes.get(1).getResult_name(),"diff_choose_result_game_score");
        assertEquals(currentRes.get(1).getResult_value(),200);

        assertEquals(currentRes.get(2).getResult_name(),"mult_choose_result_game_score");
        assertEquals(currentRes.get(2).getResult_value(),300);

        assertEquals(currentRes.get(3).getResult_name(),"div_choose_result_game_score");
        assertEquals(currentRes.get(3).getResult_value(),400);

    }



    @Test
    public void updateGameResults_test() throws Exception {
        mathBrainerDatabaseDao.insertGameResult(
                new GameResult("random_op_game_score", 500));

        mathBrainerDatabaseDao.insertGameResult(
                new GameResult("count_objects_game_score", 600));

        mathBrainerDatabaseDao.insertGameResult(
                new GameResult("mix_write_result_game_score", 1300));

        mathBrainerDatabaseDao.insertGameResult(
                new GameResult("operations_ko", 1400));


        mathBrainerDatabaseDao.updateGameResult(new GameResult("random_op_game_score",1200));
        mathBrainerDatabaseDao.updateGameResult("operations_ko",700);


        LiveData<List<GameResult>> results =  mathBrainerDatabaseDao.loadAllGamesResults();

        Observer<List<GameResult>> observer = ids -> assertEquals(4, ids.size());
        results.observeForever(observer);
        List<GameResult> currentRes = results.getValue();

        assertEquals(currentRes.get(0).getResult_name(),"random_op_game_score");
        assertEquals(currentRes.get(0).getResult_value(),1200);

        assertEquals(currentRes.get(3).getResult_name(),"operations_ko");
        assertEquals(currentRes.get(3).getResult_value(),700);

    }


    @Test
    public void isGameResultExists_test() throws Exception {
        mathBrainerDatabaseDao.insertGameResult(
                new GameResult("random_op_game_score", 500));

        mathBrainerDatabaseDao.insertGameResult(
                new GameResult("count_objects_game_score", 600));

        GameResult gres_01 = mathBrainerDatabaseDao.isGameResultExists("operations_ko");
        GameResult gres_02 = mathBrainerDatabaseDao.isGameResultExists("count_objects_game_score");

        LiveData<List<GameResult>> results =  mathBrainerDatabaseDao.loadAllGamesResults();

        Observer<List<GameResult>> observer = ids -> {};
        results.observeForever(observer);
        List<GameResult> currentRes = results.getValue();

        assertEquals(currentRes.get(0).getResult_name(),"random_op_game_score");
        assertEquals(currentRes.get(1).getResult_name(),"count_objects_game_score");

    }

    // getGameResult

    @Test
    public void deleteGameResult_test() throws Exception {
        mathBrainerDatabaseDao.insertGameResult(
                new GameResult("doublenumber_game_score", 110));

        mathBrainerDatabaseDao.insertGameResult(
                new GameResult("mix_choose_result_game_score", 210));

        LiveData<List<GameResult>> results =  mathBrainerDatabaseDao.loadAllGamesResults();

        // before
        Observer<List<GameResult>> observer = ids -> assertEquals(2, ids.size());

        GameResult tobeDeleted = new GameResult("doublenumber_game_score", 110);
        mathBrainerDatabaseDao.deleteGameResult(tobeDeleted);

        // after
        Observer<List<GameResult>>  observer_02 = ids -> assertEquals(1, ids.size());


    }



    @Test
    public void deleteGameResults_plural_test() throws Exception {
        mathBrainerDatabaseDao.insertGameResult(
                new GameResult("doublenumber_game_score", 110));

        mathBrainerDatabaseDao.insertGameResult(
                new GameResult("mix_choose_result_game_score", 210));

        mathBrainerDatabaseDao.insertGameResult(
                new GameResult("sum_choose_result_game_score", 100));

        mathBrainerDatabaseDao.insertGameResult(
                new GameResult("diff_choose_result_game_score", 200));

        LiveData<List<GameResult>> results =  mathBrainerDatabaseDao.loadAllGamesResults();

        // before
        Observer<List<GameResult>> observer = ids -> assertEquals(4, ids.size());

        mathBrainerDatabaseDao.deleteGameResult(new GameResult("doublenumber_game_score", 110));
        mathBrainerDatabaseDao.deleteGameResult(new GameResult("mix_choose_result_game_score", 110));

        // after
        Observer<List<GameResult>>  observer_02 = ids -> assertEquals(2, ids.size());

    }




}
