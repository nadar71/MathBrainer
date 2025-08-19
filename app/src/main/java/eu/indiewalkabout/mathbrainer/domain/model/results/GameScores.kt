package eu.indiewalkabout.mathbrainer.domain.model.results

import androidx.room.Entity

@Entity(tableName = "GameScores")
data class GameScores(
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
    val number_order_game_score: Int
)
