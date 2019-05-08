package eu.indiewalkabout.mathbrainer.statistics;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;


/**
 * Game result object; store in key-value pairs the game results.
 * The name of the adopted keys are below :
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
 */
@Entity(tableName = "GAMERESULTS_LIST")
public class GameResult {

    @PrimaryKey
    @NonNull
    private String result_name;

    private int result_value;


    public GameResult(String result_name, int result_value) {
        this.result_name = result_name;
        this.result_value = result_value;
    }



    public String getResult_name() {
        return result_name;
    }

    public int getResult_value() {
        return result_value;
    }


    public void setResult_name(String result_name) {
        this.result_name = result_name;
    }

    public void setResult_value(int result_value) {
        this.result_value = result_value;
    }
}

