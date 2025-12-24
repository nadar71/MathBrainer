package eu.indiewalkabout.mathbrainer.feat_statistics.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import eu.indiewalkabout.mathbrainer.feat_statistics.domain.model.GameScores
import eu.indiewalkabout.mathbrainer.feat_statistics.domain.model.GameStats
import eu.indiewalkabout.mathbrainer.feat_statistics.domain.model.GameStatistics

@Database(
    entities = [
        GameScores::class,
        GameStatistics::class,
        GameStats::class
    ],
    version = 6,
    exportSchema = true
)
@TypeConverters(DateConverter::class)

abstract class MathBrainerDatabase : RoomDatabase() {
    abstract fun mathBrainerDbDao(): MathBrainerDbDao

    companion object {
        private val DBNAME = "MathBrainerDB"

        fun getDbInstance(context: Context): MathBrainerDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                MathBrainerDatabase::class.java,
                DBNAME
            )
                .fallbackToDestructiveMigration(true)
                .build()
        }
    }
}
