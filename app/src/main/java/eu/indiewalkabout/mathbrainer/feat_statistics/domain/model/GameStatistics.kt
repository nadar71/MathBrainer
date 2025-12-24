package eu.indiewalkabout.mathbrainer.feat_statistics.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey


// Store the statistics as a whole
@Entity(tableName = "GameStatistics")
data class GameStatistics(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    val operations_executed: Int,
    val operations_ok: Int,
    val operations_ko: Int,
    val sums: Int,
    val differences: Int,
    val multiplications: Int,
    val divisions: Int,
    val doublings: Int,
    val level_upgrades: Int,
    val lives_missed: Int,
    val objects_counted: Int,
    val numbers_in_order: Int,
    val games_played: Int,
    val games_lose: Int
)