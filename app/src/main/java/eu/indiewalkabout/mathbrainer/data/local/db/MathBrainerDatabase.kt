package eu.indiewalkabout.mathbrainer.data.local.db


import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import eu.indiewalkabout.mathbrainer.domain.model.results.GameResult


@Database(entities = [GameResult::class], version = 1, exportSchema = false)
abstract class MathBrainerDatabase : RoomDatabase() {

    // get the dao
    abstract fun mathBrainerDbDao(): MathBrainerDbDao

    companion object {

        private val TAG = MathBrainerDatabase::class.java.simpleName

        // lock for synchro
        private val LOCK = Any()
        private val DBNAME = "MathBrainerDB"
        private var sDbInstance: MathBrainerDatabase? = null


        fun getsDbInstance(context: Context): MathBrainerDatabase? {
            if (sDbInstance == null) {
                synchronized(LOCK) {
                    Log.d(TAG, "Creating App db singleton instance...")
                    sDbInstance = Room.databaseBuilder(context.applicationContext, MathBrainerDatabase::class.java, DBNAME)
                            //.allowMainThreadQueries() // TODO : temporary for debugging, delete this
                            .build()
                }

            }
            Log.d(TAG, "Db created")
            return sDbInstance
        }
    }


}
