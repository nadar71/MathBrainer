package eu.indiewalkabout.mathbrainer.feat_statistics.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// Store the stats of a game
@Entity(tableName = "GameStats")
data class GameStats(
    @PrimaryKey val gameId: String,
    val highScore: Int = 0,
    val challengesPlayed: Int = 0,
    val challengesWon: Int = 0,
    val challengesLost: Int = 0,
    val lastLevel: Int = 0
)
