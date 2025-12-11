package eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_write.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "MathWriteGameStats")
data class MathWriteGameStats(
    @PrimaryKey val operationId: String,
    val highScore: Int = 0,
    val gamesPlayed: Int = 0,
    val gamesWon: Int = 0,
    val gamesLost: Int = 0
)
