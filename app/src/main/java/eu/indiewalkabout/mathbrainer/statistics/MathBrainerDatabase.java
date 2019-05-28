package eu.indiewalkabout.mathbrainer.statistics;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.util.Log;


@Database(entities = {GameResult.class}, version = 1, exportSchema = false)
public abstract class MathBrainerDatabase extends RoomDatabase {

    private static final String TAG = MathBrainerDatabase.class.getSimpleName();

    // lock for synchro
    private static final Object LOCK   = new Object();
    private static final String DBNAME = "MathBrainerDB";
    private static MathBrainerDatabase sDbInstance ;

    // get the dao
    public abstract MathBrainerDbDao MathBrainerDbDao();



    public static MathBrainerDatabase getsDbInstance(Context context){
        if(sDbInstance == null){
            synchronized (LOCK){
                Log.d(TAG, "Creating App db singleton instance...");
                sDbInstance = Room.databaseBuilder(context.getApplicationContext(), MathBrainerDatabase.class,MathBrainerDatabase.DBNAME)
                        //.allowMainThreadQueries() // TODO : temporary for debugging, delete this
                        .build();
            }

        }
        Log.d(TAG, "Db created");
        return sDbInstance;
    }


}
