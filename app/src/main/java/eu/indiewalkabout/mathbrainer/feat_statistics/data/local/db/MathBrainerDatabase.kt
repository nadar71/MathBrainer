package eu.indiewalkabout.mathbrainer.feat_statistics.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import eu.indiewalkabout.mathbrainer.feat_statistics.domain.model.GameScores
import eu.indiewalkabout.mathbrainer.feat_statistics.domain.model.GameStatistics
import eu.indiewalkabout.mathbrainer.feat_statistics.domain.model.GameStats

@Database(
    entities = [
        GameScores::class,
        GameStatistics::class,
        GameStats::class
    ],
    version = 10,
    exportSchema = true
)
@TypeConverters(DateConverter::class)

abstract class MathBrainerDatabase : RoomDatabase() {
    abstract fun mathBrainerDbDao(): MathBrainerDbDao

    companion object {
        private const val DBNAME = "MathBrainerDB"

        fun getDbInstance(context: Context): MathBrainerDatabase = createDatabase(context, DBNAME)

        internal fun createDatabase(context: Context, databaseName: String): MathBrainerDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                MathBrainerDatabase::class.java,
                databaseName
            )
                // Fail closed until an upgrade path is proven by a production schema artifact.
                .build()
        }
    }
}
