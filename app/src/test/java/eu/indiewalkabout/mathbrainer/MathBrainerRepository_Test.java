package eu.indiewalkabout.mathbrainer;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import eu.indiewalkabout.mathbrainer.statistics.GameResult;
import eu.indiewalkabout.mathbrainer.statistics.MathBrainerDatabase;
import eu.indiewalkabout.mathbrainer.statistics.MathBrainerDbDao;
import eu.indiewalkabout.mathbrainer.statistics.MathBrainerRepository;
import eu.indiewalkabout.mathbrainer.util.SingletonProvider;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;

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
        MockitoAnnotations.initMocks(this);
        // repository = ((SingletonProvider) SingletonProvider.Companion.getsContext()).getRepository();
        repository = MathBrainerRepository.Companion.getInstance(mockDb);
        when(mockDb.MathBrainerDbDao()).thenReturn(mockDao);
    }

    @Test
    public void initGameResults_test(){
        repository.initGameResults();
        verify(mockDao, times(repository.getGame_results_list().size())).insertGameResult(any());
    }

    @Test
    public void getGameResult_test(){
        when(mockDao.isGameResultExists(anyString())).thenReturn(gameResult);
        when(mockDao.getGameResult(anyString())).thenReturn(1);
        repository.getGameResult("dummy");
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
