package eu.indiewalkabout.mathbrainer.feat_statistics.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "GameStats")
data class GameStats(
    @PrimaryKey val gameId: String,
    val highScore: Int = 0,
    val gamesPlayed: Int = 0,
    val gamesWon: Int = 0,
    val gamesLost: Int = 0,
    val lastLevel: Int = 0
)
