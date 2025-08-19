package eu.indiewalkabout.mathbrainer.domain.model.results

import androidx.room.Entity


@Entity(tableName = "GameStatistics")
data class GameStatistics(
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