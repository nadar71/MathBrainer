package eu.indiewalkabout.mathbrainer.feat_statistics.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey


// Store the scores of each game
@Entity(tableName = "GameScores")
data class GameScores(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    val global_score: Int,
    val doublenumber_game_score: Int,
    val sum_choose_result_game_score: Int,
    val diff_choose_result_game_score: Int,
    val mult_choose_result_game_score: Int,
    val div_choose_result_game_score: Int,
    val mix_choose_result_game_score: Int,
    val sum_write_result_game_score: Int,
    val diff_write_result_game_score: Int,
    val mult_write_result_game_score: Int,
    val div_write_result_game_score: Int,
    val mix_write_result_game_score: Int,
    val random_op_game_score: Int,
    val count_objects_game_score: Int,
    val number_order_game_score: Int,
    val sequence_complete_game_score: Int,
    val falling_ops_game_score: Int,
    val enigma_game_score: Int
){
    companion object {
        val emptyScores = GameScores(
            global_score = 0,
            doublenumber_game_score = 0,
            sum_choose_result_game_score = 0,
            diff_choose_result_game_score = 0,
            mult_choose_result_game_score = 0,
            div_choose_result_game_score = 0,
            mix_choose_result_game_score = 0,
            sum_write_result_game_score = 0,
            diff_write_result_game_score = 0,
            mult_write_result_game_score = 0,
            div_write_result_game_score = 0,
            mix_write_result_game_score = 0,
            random_op_game_score = 0,
            count_objects_game_score = 0,
            number_order_game_score = 0,
            sequence_complete_game_score = 0,
            falling_ops_game_score = 0,
	        enigma_game_score = 0
        )
    }
}
