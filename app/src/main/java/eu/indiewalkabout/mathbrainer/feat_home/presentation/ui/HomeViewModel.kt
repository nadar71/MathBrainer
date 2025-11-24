package eu.indiewalkabout.mathbrainer.feat_home.presentation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.indiewalkabout.mathbrainer.R
import eu.indiewalkabout.mathbrainer.feat_games.feat_count_items.presentation.ui.CountObjectsActivity
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_choose.presentation.ui.Math_Op_Choose_Result_Activity
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_double.presentation.ui.DoubleNumberActivity
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_write.presentation.ui.Math_Op_Write_Result_Activity
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_random_operation.presentation.ui.RandomOperationActivity
import eu.indiewalkabout.mathbrainer.feat_games.feat_number_order.presentation.ui.NumberOrderActivity
import eu.indiewalkabout.mathbrainer.feat_home.domain.model.GameDefinition
import eu.indiewalkabout.mathbrainer.feat_home.domain.usecase.GetGameScoresUseCase
import eu.indiewalkabout.mathbrainer.feat_home.presentation.model.GameUiModel
import eu.indiewalkabout.mathbrainer.feat_statistics.domain.model.GameScores
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getGameScoresUseCase: GetGameScoresUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    private var scoresJob: Job? = null

    private val definitions = listOf(
        GameDefinition(
            id = "sum_choose",
            titleRes = R.string.sum_choose_title,
            descriptionRes = R.string.sum_choose_description,
            target = Math_Op_Choose_Result_Activity::class,
            operation = "+"
        ),
        GameDefinition(
            id = "diff_choose",
            titleRes = R.string.diff_choose_title,
            descriptionRes = R.string.diff_choose_description,
            target = Math_Op_Choose_Result_Activity::class,
            operation = "-"
        ),
        GameDefinition(
            id = "mult_choose",
            titleRes = R.string.mult_choose_title,
            descriptionRes = R.string.mult_choose_description,
            target = Math_Op_Choose_Result_Activity::class,
            operation = "*"
        ),
        GameDefinition(
            id = "div_choose",
            titleRes = R.string.div_choose_title,
            descriptionRes = R.string.div_choose_description,
            target = Math_Op_Choose_Result_Activity::class,
            operation = "/"
        ),
        GameDefinition(
            id = "sum_write",
            titleRes = R.string.sum_write_title,
            descriptionRes = R.string.sum_write_description,
            target = Math_Op_Write_Result_Activity::class,
            operation = "+"
        ),
        GameDefinition(
            id = "diff_write",
            titleRes = R.string.diff_write_title,
            descriptionRes = R.string.diff_write_description,
            target = Math_Op_Write_Result_Activity::class,
            operation = "-"
        ),
        GameDefinition(
            id = "mult_write",
            titleRes = R.string.mult_write_title,
            descriptionRes = R.string.mult_write_description,
            target = Math_Op_Write_Result_Activity::class,
            operation = "*"
        ),
        GameDefinition(
            id = "div_write",
            titleRes = R.string.div_write_title,
            descriptionRes = R.string.div_write_description,
            target = Math_Op_Write_Result_Activity::class,
            operation = "/"
        ),
        GameDefinition(
            id = "mix_choose",
            titleRes = R.string.mix_choose_title,
            descriptionRes = R.string.mix_choose_description,
            target = Math_Op_Choose_Result_Activity::class,
            requiresHighScore = true
        ),
        GameDefinition(
            id = "mix_write",
            titleRes = R.string.mix_write_title,
            descriptionRes = R.string.mix_write_description,
            target = Math_Op_Write_Result_Activity::class,
            requiresHighScore = true
        ),
        GameDefinition(
            id = "quick_count",
            titleRes = R.string.quick_count_title,
            descriptionRes = R.string.quick_count_description,
            target = CountObjectsActivity::class
        ),
        GameDefinition(
            id = "double",
            titleRes = R.string.double_number_title,
            descriptionRes = R.string.double_number_description,
            target = DoubleNumberActivity::class
        ),
        GameDefinition(
            id = "order",
            titleRes = R.string.number_order_title,
            descriptionRes = R.string.number_order_description,
            target = NumberOrderActivity::class
        ),
        GameDefinition(
            id = "random",
            titleRes = R.string.random_operations_title,
            descriptionRes = R.string.random_operations_description,
            target = RandomOperationActivity::class
        )
    )

    init {
        refresh()
    }

    fun refresh() {
        scoresJob?.cancel()
        scoresJob = viewModelScope.launch {
            _uiState.value = HomeUiState(isLoading = true)
            getGameScoresUseCase().collect { scores ->
                val games = definitions.map { definition ->
                    GameUiModel(
                        definition = definition,
                        highScore = getHighScore(definition, scores)
                    )
                }
                _uiState.value = HomeUiState(isLoading = false, games = games)
            }
        }
    }

    private fun getHighScore(definition: GameDefinition, scores: GameScores): Int? {
        return when (definition.id) {
            "sum_choose" -> scores.sum_choose_result_game_score
            "diff_choose" -> scores.diff_choose_result_game_score
            "mult_choose" -> scores.mult_choose_result_game_score
            "div_choose" -> scores.div_choose_result_game_score
            "sum_write" -> scores.sum_write_result_game_score
            "diff_write" -> scores.diff_write_result_game_score
            "mult_write" -> scores.mult_write_result_game_score
            "div_write" -> scores.div_write_result_game_score
            "mix_choose" -> scores.mix_choose_result_game_score
            "mix_write" -> scores.mix_write_result_game_score
            "quick_count" -> scores.count_objects_game_score
            "double" -> scores.doublenumber_game_score
            "order" -> scores.number_order_game_score
            "random" -> scores.random_op_game_score
            else -> null
        }
    }
}
