package eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_choose.domain.model

import eu.indiewalkabout.mathbrainer.feat_statistics.domain.model.GameScores

enum class ChooseResultScoreCategory(val operationCode: String, val scoreField: (GameScores) -> Int) {
    SUM("sum_choose", { it.sum_choose_result_game_score }),
    DIFFERENCE("diff_choose", { it.diff_choose_result_game_score }),
    MULTIPLICATION("mult_choose", { it.mult_choose_result_game_score }),
    DIVISION("div_choose", { it.div_choose_result_game_score }),
    MIX("mix_choose", { it.mix_choose_result_game_score });

    companion object {
        fun fromOperation(operation: String?): ChooseResultScoreCategory {
            return when (operation) {
                "sum_choose" -> SUM
                "diff_choose" -> DIFFERENCE
                "mult_choose" -> MULTIPLICATION
                "div_choose" -> DIVISION
                else -> MIX
            }
        }
    }
}


