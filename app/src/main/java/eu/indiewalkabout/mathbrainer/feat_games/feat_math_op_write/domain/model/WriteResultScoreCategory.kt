package eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_write.domain.model

import eu.indiewalkabout.mathbrainer.feat_statistics.domain.model.GameScores


enum class WriteResultScoreCategory(val operationCode: String, val scoreField: (GameScores) -> Int) {
    SUM("sum_write", { it.sum_write_result_game_score }),
    DIFFERENCE("diff_write", { it.diff_write_result_game_score }),
    MULTIPLICATION("mult_write", { it.mult_write_result_game_score }),
    DIVISION("div_write", { it.div_write_result_game_score }),
    MIX("mix_write", { it.mix_write_result_game_score });

    companion object {
        fun fromOperation(operation: String?): WriteResultScoreCategory {
            return when (operation) {
                "sum_write" -> SUM
                "diff_write" -> DIFFERENCE
                "mult_write" -> MULTIPLICATION
                "div_write" -> DIVISION
                else -> MIX
            }
        }
    }
}

/*
enum class WriteResultScoreCategory(val operationSymbol: Char?, val scoreField: (GameScores) -> Int) {
    SUM('+', { it.sum_write_result_game_score }),
    DIFFERENCE('-', { it.diff_write_result_game_score }),
    MULTIPLICATION('*', { it.mult_write_result_game_score }),
    DIVISION('/', { it.div_write_result_game_score }),
    MIX(null, { it.mix_write_result_game_score });

    companion object {
        fun fromOperation(operation: Char?): WriteResultScoreCategory {
            return values().firstOrNull { it.operationSymbol == operation } ?: MIX
        }
    }
}*/
