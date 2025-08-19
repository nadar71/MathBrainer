package eu.indiewalkabout.mathbrainer.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import eu.indiewalkabout.mathbrainer.domain.model.results.GameScores
import eu.indiewalkabout.mathbrainer.domain.model.results.OLD.GameResult

@Database(
    entities = [
        GameScores::class,
        GameResult::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(DateConverter::class)

abstract class MathBrainerDatabase : RoomDatabase() {
    abstract fun mathBrainerDbDao(): MathBrainerDbDao

    companion object {
        private val DBNAME = "MathBrainerDB"
        private var sDbInstance: MathBrainerDatabase? = null

        /*fun getsDbInstance(context: Context): MathBrainerDatabase? {
            if (sDbInstance == null) {
                synchronized(LOCK) {
                    Log.d(TAG, "Creating App db singleton instance...")
                    sDbInstance = Room.databaseBuilder(
                        context.applicationContext,
                        MathBrainerDatabase::class.java,
                        DBNAME
                    )
                        .build()
                }
            }
            Log.d(TAG, "Db created")
            return sDbInstance
        }*/
        fun getDbInstance(context: Context): MathBrainerDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                MathBrainerDatabase::class.java,
                DBNAME
            )
                .build()
        }
    }
}
