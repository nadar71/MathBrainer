package eu.indiewalkabout.mathbrainer.core.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import eu.indiewalkabout.mathbrainer.R
import eu.indiewalkabout.mathbrainer.feat_ads.presentation.AdMobBannerView
import eu.indiewalkabout.mathbrainer.feat_credits.presentation.ui.GameCreditsScreen
import eu.indiewalkabout.mathbrainer.feat_games.feat_count_items.presentation.ui.CountObjectsGameScreen
import eu.indiewalkabout.mathbrainer.feat_games.feat_enigma.presentation.ui.EnigmaGameScreen
import eu.indiewalkabout.mathbrainer.feat_games.feat_falling_op.presentation.ui.FallingOpsGameScreen
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_choose.presentation.ui.MathChooseGameScreen
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_double.presentation.ui.DoubleNumberGameScreen
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_write.presentation.ui.MathWriteGameScreen
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_random_operation.presentation.ui.RandomOperationGameScreen
import eu.indiewalkabout.mathbrainer.feat_games.feat_memory_flash.presentation.ui.MemoryFlashGameScreen
import eu.indiewalkabout.mathbrainer.feat_games.feat_number_order.presentation.ui.NumberOrderGameScreen
import eu.indiewalkabout.mathbrainer.feat_games.feat_sequence_complete.presentation.ui.SequenceCompleteGameScreen
import eu.indiewalkabout.mathbrainer.feat_home.presentation.ui.HomeScreen
import eu.indiewalkabout.mathbrainer.feat_home.presentation.ui.HomeViewModel
import eu.indiewalkabout.mathbrainer.feat_settings.presentation.ui.GameSettingsScreen
import eu.indiewalkabout.mathbrainer.feat_statistics.presentation.ui.StatisticScreen
import eu.indiewalkabout.mathbrainer.feat_statistics.presentation.ui.StatisticViewModel

@Composable
fun NavGraph(
    navigationState: NavigationState = rememberNavigationState(),
    navigator: Navigator = rememberNavigator(navigationState)
) {
    val saveableStateHolder = rememberSaveableStateHolder()
    val entryDecorators = listOf(
        rememberSaveableStateHolderNavEntryDecorator<AppRoute>(saveableStateHolder)
    )

    NavDisplay<AppRoute>(
        backStack = navigationState.backStack,
        onBack = navigator::back,
        entryDecorators = entryDecorators,
        entryProvider = { route ->
            NavEntry<AppRoute>(route) { currentRoute ->
                when (currentRoute) {
                    HomeRoute -> {
                        val viewModel = hiltViewModel<HomeViewModel>()
                        ScreenWithBottomBanner {
                            HomeScreen(
                                onStatisticsClick = { navigator.navigate(StatisticsRoute) },
                                onSettingsClick = { navigator.navigate(GameSettingsRoute) },
                                onGameSelected = navigator::navigate,
                                homeViewModel = viewModel
                            )
                        }
                    }

                    is MathWriteRoute -> {
                        ScreenWithBottomBanner {
                            MathWriteGameScreen(
                                operation = currentRoute.operation,
                                onBack = navigator::back
                            )
                        }
                    }

                    is MathChooseRoute -> {
                        ScreenWithBottomBanner {
                            MathChooseGameScreen(
                                operation = currentRoute.operation,
                                onBack = navigator::back
                            )
                        }
                    }

                    DoubleNumberRoute -> {
                        ScreenWithBottomBanner {
                            DoubleNumberGameScreen(onBack = navigator::back)
                        }
                    }

                    RandomOperationRoute -> {
                        ScreenWithBottomBanner {
                            RandomOperationGameScreen(onBack = navigator::back)
                        }
                    }

                    MemoryFlashRoute -> {
                        ScreenWithBottomBanner {
                            MemoryFlashGameScreen(onBack = navigator::back)
                        }
                    }

                    NumberOrderRoute -> {
                        ScreenWithBottomBanner {
                            NumberOrderGameScreen(onBack = navigator::back)
                        }
                    }

                    CountObjectsRoute -> {
                        ScreenWithBottomBanner {
                            CountObjectsGameScreen(onBack = navigator::back)
                        }
                    }

                    SequenceCompleteRoute -> {
                        ScreenWithBottomBanner {
                            SequenceCompleteGameScreen(onBack = navigator::back)
                        }
                    }

                    FallingOpsRoute -> {
                        ScreenWithBottomBanner {
                            FallingOpsGameScreen(onBack = navigator::back)
                        }
                    }

                    EnigmaRoute -> {
                        ScreenWithBottomBanner {
                            EnigmaGameScreen(onBack = navigator::back)
                        }
                    }

                    GameSettingsRoute -> {
                        ScreenWithBottomBanner {
                            GameSettingsScreen(
                                onBack = navigator::back,
                                onCreditsClick = { navigator.navigate(GameCreditsRoute) }
                            )
                        }
                    }

                    GameCreditsRoute -> {
                        ScreenWithBottomBanner {
                            GameCreditsScreen(onBack = navigator::back)
                        }
                    }

                    StatisticsRoute -> {
                        val viewModel = hiltViewModel<StatisticViewModel>()
                        ScreenWithBottomBanner {
                            StatisticScreen(
                                onBack = navigator::back,
                                onGameSelected = navigator::navigate,
                                statisticViewModel = viewModel
                            )
                        }
                    }
                }
            }
        }
    )
}

@Composable
private fun ScreenWithBottomBanner(content: @Composable () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(1f, fill = true)) {
            content()
        }
        AdMobBannerView(
            adUnitId = stringResource(R.string.admob_key_bottom_banner),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
