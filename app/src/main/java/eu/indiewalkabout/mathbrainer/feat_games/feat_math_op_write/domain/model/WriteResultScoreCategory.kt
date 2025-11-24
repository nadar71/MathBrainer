package eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_write.domain.model

import eu.indiewalkabout.mathbrainer.feat_statistics.domain.model.GameScores

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
}