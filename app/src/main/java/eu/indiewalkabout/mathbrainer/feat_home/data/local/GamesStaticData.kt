package eu.indiewalkabout.mathbrainer.feat_home.data.local

import eu.indiewalkabout.mathbrainer.R
import eu.indiewalkabout.mathbrainer.feat_home.domain.model.GameDefinition


// Definitions of each games by data
val gamesDefinitionsList = listOf(
    GameDefinition(
        id = "sum_choose",
        titleRes = R.string.game_card_choose_result_sum_text , // R.string.sum_choose_title,
        // descriptionRes = R.string.sum_choose_description,
    ),
    GameDefinition(
        id = "diff_choose",
        titleRes = R.string.game_card_choose_result_diff_text, // R.string.diff_choose_title,
        // descriptionRes = R.string.diff_choose_description,
    ),
    GameDefinition(
        id = "mult_choose",
        titleRes = R.string.game_card_choose_result_mult_text , // R.string.mult_choose_title,
        // descriptionRes = R.string.mult_choose_description,
    ),
    GameDefinition(
        id = "div_choose",
        titleRes = R.string.game_card_choose_result_div_text , // R.string.div_choose_title,
        // descriptionRes = R.string.div_choose_description,
    ),
    GameDefinition(
        id = "sum_write",
        titleRes = R.string.game_card_write_result_sum_text , // R.string.sum_write_title,
        // descriptionRes = R.string.sum_write_description,
    ),
    GameDefinition(
        id = "diff_write",
        titleRes = R.string.game_card_write_result_diff_text , // R.string.diff_write_title,
        // descriptionRes = R.string.diff_write_description,
    ),
    GameDefinition(
        id = "mult_write",
        titleRes = R.string.game_card_write_result_mult_text , // R.string.mult_write_title,
        // descriptionRes = R.string.mult_write_description,
    ),
    GameDefinition(
        id = "div_write",
        titleRes = R.string.game_card_write_result_div_text , // R.string.div_write_title,
        // descriptionRes = R.string.div_write_description,
    ),
    GameDefinition(
        id = "mix_choose",
        titleRes = R.string.game_card_choose_result_allop_text, // R.string.mix_choose_title,
        // descriptionRes = R.string.empty_descr, // R.string.mix_choose_description,
        requiresHighScore = true
    ),
    GameDefinition(
        id = "mix_write",
        titleRes = R.string.game_card_write_result_allop_text,// R.string.mix_write_title,
        // descriptionRes = R.string.empty_descr, // R.string.mix_write_description,
        requiresHighScore = true
    ),
    GameDefinition(
        id = "random",
        titleRes = R.string.game_card_choose_random_operation_text , // R.string.random_operations_title,
        // descriptionRes = R.string.random_operations_description,
    ),
    GameDefinition(
        id = "double",
        titleRes = R.string.game_card_double_number_title , // R.string.double_number_title,
        // descriptionRes = R.string.double_number_description,
    ),
    GameDefinition(
        id = "quick_count",
        titleRes = R.string.quick_count_title,
        // descriptionRes = R.string.quick_count_description,
    ),
    GameDefinition(
        id = "order",
        titleRes = R.string.number_order_title,
        // descriptionRes = R.string.number_order_description,
    ),

)