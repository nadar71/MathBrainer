package eu.indiewalkabout.mathbrainer.statistic;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import eu.indiewalkabout.mathbrainer.domain.model.results.GameResult;
import eu.indiewalkabout.mathbrainer.data.local.db.MathBrainerDatabase;
import eu.indiewalkabout.mathbrainer.data.local.db.MathBrainerDbDao;
import eu.indiewalkabout.mathbrainer.data.repository.MathBrainerRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;

/* TODO : fix error
MathBrainerRepository_Test bug #58
Launching every single tests by itself they are passed, but running altogether only the first
it's ok, the other no, independently by which tests are active in the class.

Tried with reset mocks in teardown function and remock explicitly inside @before method,
as well as doing it inside each test (both reset and remock), check the commented code.
 */

@RunWith(MockitoJUnitRunner.class)
public class MathBrainerRepository_Test {

    @Mock
    MathBrainerDatabase mockDb;

    @Mock
    MathBrainerDbDao mockDao;

    @Mock
    GameResult gameResult;

    MathBrainerRepository repository;


    @Before
    public void setup() {
        // mockDb = mock(MathBrainerDatabase.class);
        // mockDao = mock(mathBrainerDbDao.class);
        // gameResult = mock(GameResult.class);
        MockitoAnnotations.initMocks(this);
        repository = MathBrainerRepository.Companion.getInstance(mockDb);
        when(mockDb.mathBrainerDbDao()).thenReturn(mockDao);

    }

/*
    @After
    public void teardown() {
        Mockito.reset(mockDb);
        Mockito.reset(mockDao);
        Mockito.reset(gameResult);
        // Mockito.clearInvocations();
        Mockito.validateMockitoUsage();
        // mockDb = null;
        // mockDao = null;
        // gameResult = null;
        // repository = null;
    }

 */





    @Test
    public void initGameResults_test(){
        repository.initGameResults();
        verify(mockDao, times(repository.getGame_results_list().size())).insertGameResult(any());
    }

    @Test
    public void getGameResult_test(){
        when(mockDao.isGameResultExists(anyString())).thenReturn(gameResult);
        when(mockDao.getGameResult(anyString())).thenReturn(1);
        if (repository != null)  repository.getGameResult("dummy");
        verify(mockDao, times(1)).isGameResultExists(any());
        verify(mockDao, times(1)).getGameResult(anyString());
    }



    @Test
    public void insertGameResult_test(){
        repository.insertGameResult(gameResult);
        verify(mockDao, times(1)).insertGameResult(any());
    }


    @Test
    public void incrementGameResult_test(){
        repository.incrementGameResult("dummy");
        when(mockDao.getGameResult(anyString())).thenReturn(1);
        verify(mockDao, times(1)).getGameResult(any());
        verify(mockDao, times(1)).updateGameResult(anyString(),eq(1));

    }

    @Test
    public void incrementGameResultByDelta_test(){
        repository.incrementGameResultByDelta("dummy",1);
        when(mockDao.getGameResult(anyString())).thenReturn(1);
        verify(mockDao, times(1)).getGameResult(any());
        verify(mockDao, times(1)).updateGameResult(anyString(),eq(1));
    }


    @Test
    public void updateGameResultHighscore_test(){
        repository.updateGameResultHighscore("dummy",1);
        when(mockDao.getGameResult(anyString())).thenReturn(1);
        verify(mockDao, times(1)).getGameResult(any());
        verify(mockDao, times(1)).updateGameResult(anyString(),eq(1));

    }



    @Test
    public void deleteGameResult_test(){
        repository.deleteGameResult(gameResult);
        verify(mockDao, times(1)).deleteGameResult(any());
    }




}
