package eu.indiewalkabout.mathbrainer.feat_home.domain.model

import eu.indiewalkabout.mathbrainer.feat_statistics.domain.model.GameScores

enum class GameTypes(
    val id: String,
    val scoreField: (GameScores) -> Int?
) {
    // Choose Games
    SUM_CHOOSE(
        id = "sum_choose",
        scoreField = { it.sum_choose_result_game_score }
    ),
    DIFF_CHOOSE(
        id = "diff_choose",
        scoreField = { it.diff_choose_result_game_score }
    ),
    MULT_CHOOSE(
        id = "mult_choose",
        scoreField = { it.mult_choose_result_game_score }
    ),
    DIV_CHOOSE(
        id = "div_choose",
        scoreField = { it.div_choose_result_game_score }
    ),
    MIX_CHOOSE(
        id = "mix_choose",
        scoreField = { it.mix_choose_result_game_score }
    ),

    // Write Games
    SUM_WRITE(
        id = "sum_write",
        scoreField = { it.sum_write_result_game_score }
    ),
    DIFF_WRITE(
        id = "diff_write",
        scoreField = { it.diff_write_result_game_score }
    ),
    MULT_WRITE(
        id = "mult_write",
        scoreField = { it.mult_write_result_game_score }
    ),
    DIV_WRITE(
        id = "div_write",
        scoreField = { it.div_write_result_game_score }
    ),
    MIX_WRITE(
        id = "mix_write",
        scoreField = { it.mix_write_result_game_score }
    ),

    // Other Games
    QUICK_COUNT(
        id = "quick_count",
        scoreField = { it.count_objects_game_score }
    ),
    DOUBLE_NUMBER(
        id = "double",
        scoreField = { it.doublenumber_game_score }
    ),
    NUMBER_ORDER(
        id = "order",
        scoreField = { it.number_order_game_score }
    ),
    RANDOM_OPERATION(
        id = "random",
        scoreField = { it.random_op_game_score }
    ),
    SEQUENCE_COMPLETE(
        id = "sequence_complete",
        scoreField = { it.sequence_complete_game_score }
    ),
    FALLING_OPS(
        id = "falling_ops",
        scoreField = { it.falling_ops_game_score }
    ),
    ENIGMA(
        id = "enigma",
        scoreField = { it.enigma_game_score }
    );

    companion object {
        fun fromId(id: String): GameTypes? {
            return values().find { it.id == id }
        }
    }
}
