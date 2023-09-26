package eu.indiewalkabout.mathbrainer.domain.model.results

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey


/**
 * -------------------------------------------------------------------------------------------------
 * Game result object; store in key-value pairs the game results.
 * The name of the adopted keys/primary key are below :
 * - global_score
 * - doublenumber_game_score
 *
 * - sum_choose_result_game_score
 * - diff_choose_result_game_score
 * - mult_choose_result_game_score
 * - div_choose_result_game_score
 * - mix_choose_result_game_score
 *
 * - sum_write_result_game_score
 * - diff_write_result_game_score
 * - mult_write_result_game_score
 * - div_write_result_game_score
 * - mix_write_result_game_score
 *
 * - random_op_game_score
 * - count_objects_game_score
 * - number_order_game_score
 * - operations_executed
 * - operations_ok
 * - operations_ko
 * - sums
 * - differences
 * - multiplications
 * - divisions
 * - doublings
 * - level_upgrades
 * - lifes_missed
 * - objects_counted
 * - numbers_in_order
 * - games_played
 * - games_lose
 * -------------------------------------------------------------------------------------------------
 */
@Entity(tableName = "GAMERESULTS_LIST")
class GameResult(@field:PrimaryKey
                 var result_name: String, var result_value: Int)

