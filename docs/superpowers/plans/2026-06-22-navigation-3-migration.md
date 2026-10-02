# Navigation 3 Migration Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Migrate the app from Navigation Compose 2 to Navigation 3 with typed destinations and a state-driven navigator, while preserving current user-facing behavior.

**Architecture:** Replace `NavHostController` and string routes with typed `NavKey` destinations, a `NavigationState` backed by `rememberNavBackStack()`, and a `Navigator` that mutates the stack in response to UI events. Rebuild the current `NavGraph` as a `NavDisplay` with typed entries, and update screens to receive explicit navigation callbacks instead of a controller.

**Tech Stack:** Kotlin, Jetpack Compose, Navigation 3 (`androidx.navigation3.runtime`, `androidx.navigation3.ui`), Hilt, StateFlow, Compose lifecycle collection

---

## File Structure

**Create**
- `app/src/main/java/eu/indiewalkabout/mathbrainer/core/presentation/navigation/AppRoute.kt`
- `app/src/main/java/eu/indiewalkabout/mathbrainer/core/presentation/navigation/NavigationState.kt`
- `app/src/main/java/eu/indiewalkabout/mathbrainer/core/presentation/navigation/Navigator.kt`
- `app/src/test/java/eu/indiewalkabout/mathbrainer/core/presentation/navigation/NavigatorTest.kt`
- `app/src/test/java/eu/indiewalkabout/mathbrainer/core/presentation/navigation/GameRouteResolverNav3Test.kt`
- `app/src/test/java/eu/indiewalkabout/mathbrainer/core/presentation/navigation/NavigationStateTest.kt`

**Modify**
- `gradle/libs.versions.toml`
- `app/build.gradle.kts`
- `app/src/main/java/eu/indiewalkabout/mathbrainer/core/presentation/navigation/NavGraph.kt`
- `app/src/main/java/eu/indiewalkabout/mathbrainer/core/presentation/navigation/GameRouteResolver.kt`
- `app/src/main/java/eu/indiewalkabout/mathbrainer/feat_home/presentation/ui/HomeGameActivity.kt`
- `app/src/main/java/eu/indiewalkabout/mathbrainer/feat_home/presentation/ui/HomeScreen.kt`
- `app/src/main/java/eu/indiewalkabout/mathbrainer/feat_statistics/presentation/ui/StatisticScreen.kt`

**Delete**
- `app/src/main/java/eu/indiewalkabout/mathbrainer/core/presentation/navigation/ScreenRoutes.kt`

**Verify during implementation**
- `app/src/main/java/eu/indiewalkabout/mathbrainer/feat_settings/presentation/ui/GameSettingsScreen.kt`
- `app/src/main/java/eu/indiewalkabout/mathbrainer/feat_credits/presentation/ui/GameCreditsScreen.kt`
- `app/src/main/java/eu/indiewalkabout/mathbrainer/core/presentation/navigation/NavGraph.kt` screen imports for all game destinations

---

### Task 1: Add Navigation 3 dependencies and typed route model

**Files:**
- Modify: `gradle/libs.versions.toml`
- Modify: `app/build.gradle.kts`
- Create: `app/src/main/java/eu/indiewalkabout/mathbrainer/core/presentation/navigation/AppRoute.kt`
- Delete: `app/src/main/java/eu/indiewalkabout/mathbrainer/core/presentation/navigation/ScreenRoutes.kt`

- [ ] **Step 1: Write the failing route-model test**

```kotlin
package eu.indiewalkabout.mathbrainer.core.presentation.navigation

import org.junit.Assert.assertEquals
import org.junit.Test

class GameRouteResolverNav3Test {

    @Test
    fun `write game types resolve to typed write routes`() {
        val route = GameRouteResolver.resolve(GameTypes.SUM_WRITE)

        assertEquals(MathWriteRoute(GameTypes.SUM_WRITE.id), route)
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew :app:testDebugUnitTest --tests "eu.indiewalkabout.mathbrainer.core.presentation.navigation.GameRouteResolverNav3Test"`

Expected: FAIL because `GameRouteResolver` still returns `String` routes and typed routes do not exist yet.

- [ ] **Step 3: Add Navigation 3 dependencies**

Add Navigation 3 artifacts in `gradle/libs.versions.toml`:

```toml
[versions]
navigation3 = "1.0.0"

[libraries]
androidx-navigation3-runtime = { module = "androidx.navigation3:navigation3-runtime", version.ref = "navigation3" }
androidx-navigation3-ui = { module = "androidx.navigation3:navigation3-ui", version.ref = "navigation3" }
```

Update `app/build.gradle.kts`:

```kotlin
implementation(libs.androidx.navigation3.runtime)
implementation(libs.androidx.navigation3.ui)
```

Remove the old Navigation Compose dependency only after `NavGraph.kt` has been migrated and compilation succeeds:

```kotlin
// remove later in the task after NavGraph is migrated
implementation(libs.androidx.navigation.compose)
```

- [ ] **Step 4: Add typed destination keys**

Create `AppRoute.kt`:

```kotlin
package eu.indiewalkabout.mathbrainer.core.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface AppRoute : NavKey

@Serializable
data object HomeRoute : AppRoute

@Serializable
data object StatisticsRoute : AppRoute

@Serializable
data object GameSettingsRoute : AppRoute

@Serializable
data object GameCreditsRoute : AppRoute

@Serializable
data class MathWriteRoute(val operation: String) : AppRoute

@Serializable
data class MathChooseRoute(val operation: String) : AppRoute

@Serializable
data object DoubleNumberRoute : AppRoute

@Serializable
data object RandomOperationRoute : AppRoute

@Serializable
data object MemoryFlashRoute : AppRoute

@Serializable
data object CountObjectsRoute : AppRoute

@Serializable
data object NumberOrderRoute : AppRoute

@Serializable
data object SequenceCompleteRoute : AppRoute

@Serializable
data object FallingOpsRoute : AppRoute

@Serializable
data object EnigmaRoute : AppRoute
```

Delete `ScreenRoutes.kt` once all references are replaced.

- [ ] **Step 5: Run compile to verify dependency and route model setup**

Run: `./gradlew :app:compileDebugKotlin`

Expected: still FAIL because navigation files and screens still reference `ScreenRoutes` and `NavHostController`, but the new dependencies should resolve successfully.

- [ ] **Step 6: Commit**

```bash
git add gradle/libs.versions.toml app/build.gradle.kts app/src/main/java/eu/indiewalkabout/mathbrainer/core/presentation/navigation/AppRoute.kt app/src/main/java/eu/indiewalkabout/mathbrainer/core/presentation/navigation/ScreenRoutes.kt app/src/test/java/eu/indiewalkabout/mathbrainer/core/presentation/navigation/GameRouteResolverNav3Test.kt
git commit -m "feat: add navigation 3 typed route model"
```

---

### Task 2: Introduce NavigationState and Navigator

**Files:**
- Create: `app/src/main/java/eu/indiewalkabout/mathbrainer/core/presentation/navigation/NavigationState.kt`
- Create: `app/src/main/java/eu/indiewalkabout/mathbrainer/core/presentation/navigation/Navigator.kt`
- Create: `app/src/test/java/eu/indiewalkabout/mathbrainer/core/presentation/navigation/NavigationStateTest.kt`
- Create: `app/src/test/java/eu/indiewalkabout/mathbrainer/core/presentation/navigation/NavigatorTest.kt`

- [ ] **Step 1: Write failing tests for navigation state and navigator behavior**

`NavigationStateTest.kt`

```kotlin
package eu.indiewalkabout.mathbrainer.core.presentation.navigation

import androidx.navigation3.runtime.rememberNavBackStack
import androidx.compose.runtime.mutableStateListOf
import org.junit.Assert.assertEquals
import org.junit.Test

class NavigationStateTest {

    @Test
    fun `navigation state starts from home route`() {
        val state = NavigationState(
            backStack = mutableStateListOf<AppRoute>(HomeRoute)
        )

        assertEquals(HomeRoute, state.backStack.first())
        assertEquals(HomeRoute, state.currentRoute)
    }
}
```

`NavigatorTest.kt`

```kotlin
package eu.indiewalkabout.mathbrainer.core.presentation.navigation

import androidx.compose.runtime.mutableStateListOf
import org.junit.Assert.assertEquals
import org.junit.Test

class NavigatorTest {

    @Test
    fun `navigate appends destination to back stack`() {
        val state = NavigationState(mutableStateListOf<AppRoute>(HomeRoute))
        val navigator = Navigator(state)

        navigator.navigate(StatisticsRoute)

        assertEquals(listOf(HomeRoute, StatisticsRoute), state.backStack)
    }

    @Test
    fun `back removes top route when possible`() {
        val state = NavigationState(mutableStateListOf<AppRoute>(HomeRoute, StatisticsRoute))
        val navigator = Navigator(state)

        navigator.back()

        assertEquals(listOf(HomeRoute), state.backStack)
    }
}
```

- [ ] **Step 2: Run tests to verify they fail**

Run: `./gradlew :app:testDebugUnitTest --tests "eu.indiewalkabout.mathbrainer.core.presentation.navigation.NavigationStateTest" --tests "eu.indiewalkabout.mathbrainer.core.presentation.navigation.NavigatorTest"`

Expected: FAIL because these classes do not exist yet.

- [ ] **Step 3: Add navigation state holder**

Create `NavigationState.kt`:

```kotlin
package eu.indiewalkabout.mathbrainer.core.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.rememberNavBackStack

@Stable
class NavigationState(
    val backStack: SnapshotStateList<AppRoute>
) {
    val currentRoute: AppRoute
        get() = backStack.last()
}

@Composable
fun rememberNavigationState(): NavigationState {
    val backStack = rememberNavBackStack(HomeRoute)
    return NavigationState(backStack)
}
```

- [ ] **Step 4: Add navigator**

Create `Navigator.kt`:

```kotlin
package eu.indiewalkabout.mathbrainer.core.presentation.navigation

class Navigator(
    private val state: NavigationState
) {
    fun navigate(route: AppRoute) {
        state.backStack.add(route)
    }

    fun back() {
        if (state.backStack.size > 1) {
            state.backStack.removeAt(state.backStack.lastIndex)
        }
    }

    fun navigateHome() {
        state.backStack.clear()
        state.backStack.add(HomeRoute)
    }
}
```

- [ ] **Step 5: Run tests to verify they pass**

Run: `./gradlew :app:testDebugUnitTest --tests "eu.indiewalkabout.mathbrainer.core.presentation.navigation.NavigationStateTest" --tests "eu.indiewalkabout.mathbrainer.core.presentation.navigation.NavigatorTest"`

Expected: PASS

- [ ] **Step 6: Commit**

```bash
git add app/src/main/java/eu/indiewalkabout/mathbrainer/core/presentation/navigation/NavigationState.kt app/src/main/java/eu/indiewalkabout/mathbrainer/core/presentation/navigation/Navigator.kt app/src/test/java/eu/indiewalkabout/mathbrainer/core/presentation/navigation/NavigationStateTest.kt app/src/test/java/eu/indiewalkabout/mathbrainer/core/presentation/navigation/NavigatorTest.kt
git commit -m "feat: add navigation 3 state holder and navigator"
```

---

### Task 3: Convert route resolution and screen APIs away from NavHostController

**Files:**
- Modify: `app/src/main/java/eu/indiewalkabout/mathbrainer/core/presentation/navigation/GameRouteResolver.kt`
- Modify: `app/src/main/java/eu/indiewalkabout/mathbrainer/feat_home/presentation/ui/HomeScreen.kt`
- Modify: `app/src/main/java/eu/indiewalkabout/mathbrainer/feat_statistics/presentation/ui/StatisticScreen.kt`
- Test: `app/src/test/java/eu/indiewalkabout/mathbrainer/core/presentation/navigation/GameRouteResolverNav3Test.kt`

- [ ] **Step 1: Extend the failing resolver test with non-argument routes**

Update `GameRouteResolverNav3Test.kt`:

```kotlin
@Test
fun `random operation resolves to typed destination`() {
    val route = GameRouteResolver.resolve(GameTypes.RANDOM_OPERATION)

    assertEquals(RandomOperationRoute, route)
}
```

- [ ] **Step 2: Run the resolver test to verify it fails**

Run: `./gradlew :app:testDebugUnitTest --tests "eu.indiewalkabout.mathbrainer.core.presentation.navigation.GameRouteResolverNav3Test"`

Expected: FAIL because the resolver still returns `String`.

- [ ] **Step 3: Convert resolver to typed routes**

Update `GameRouteResolver.kt`:

```kotlin
package eu.indiewalkabout.mathbrainer.core.presentation.navigation

import eu.indiewalkabout.mathbrainer.feat_home.domain.model.GameTypes

object GameRouteResolver {

    fun resolve(gameType: GameTypes): AppRoute {
        return when (gameType) {
            GameTypes.SUM_WRITE,
            GameTypes.DIFF_WRITE,
            GameTypes.MULT_WRITE,
            GameTypes.DIV_WRITE,
            GameTypes.MIX_WRITE -> MathWriteRoute(gameType.id)

            GameTypes.SUM_CHOOSE,
            GameTypes.DIFF_CHOOSE,
            GameTypes.MULT_CHOOSE,
            GameTypes.DIV_CHOOSE,
            GameTypes.MIX_CHOOSE -> MathChooseRoute(gameType.id)

            GameTypes.RANDOM_OPERATION -> RandomOperationRoute
            GameTypes.MEMORY_FLASH -> MemoryFlashRoute
            GameTypes.DOUBLE_NUMBER -> DoubleNumberRoute
            GameTypes.QUICK_COUNT -> CountObjectsRoute
            GameTypes.NUMBER_ORDER -> NumberOrderRoute
            GameTypes.SEQUENCE_COMPLETE -> SequenceCompleteRoute
            GameTypes.FALLING_OPS -> FallingOpsRoute
            GameTypes.ENIGMA -> EnigmaRoute
        }
    }
}
```

- [ ] **Step 4: Replace `NavHostController` screen APIs with explicit callbacks**

Update `HomeScreen.kt` signature and usage:

```kotlin
@Composable
fun HomeScreen(
    onOpenStatistics: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenGame: (GameTypes) -> Unit,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    // ...
    Image(
        // ...
        modifier = Modifier.clickable(onClick = onOpenStatistics)
    )
    Image(
        // ...
        modifier = Modifier.clickable(onClick = onOpenSettings)
    )
    GameGrid(
        // ...
        onGameSelected = { game ->
            val gameType = GameTypes.fromId(game.definition.id) ?: return@GameGrid
            onOpenGame(gameType)
        }
    )
}
```

Update `StatisticScreen.kt` similarly:

```kotlin
@Composable
fun StatisticScreen(
    onBack: () -> Unit,
    onOpenGame: (GameTypes) -> Unit,
    statisticViewModel: StatisticViewModel = hiltViewModel()
) {
    // ...
    Icon(
        // ...
        modifier = Modifier.clickable(onClick = onBack)
    )
    GameGrid(
        // ...
        onGameSelected = { game ->
            val gameType = GameTypes.fromId(game.definition.id) ?: return@GameGrid
            onOpenGame(gameType)
        }
    )
}
```

- [ ] **Step 5: Run the resolver test to verify it passes**

Run: `./gradlew :app:testDebugUnitTest --tests "eu.indiewalkabout.mathbrainer.core.presentation.navigation.GameRouteResolverNav3Test"`

Expected: PASS

- [ ] **Step 6: Commit**

```bash
git add app/src/main/java/eu/indiewalkabout/mathbrainer/core/presentation/navigation/GameRouteResolver.kt app/src/main/java/eu/indiewalkabout/mathbrainer/feat_home/presentation/ui/HomeScreen.kt app/src/main/java/eu/indiewalkabout/mathbrainer/feat_statistics/presentation/ui/StatisticScreen.kt app/src/test/java/eu/indiewalkabout/mathbrainer/core/presentation/navigation/GameRouteResolverNav3Test.kt
git commit -m "refactor: decouple screens from nav controller"
```

---

### Task 4: Replace NavHost with Navigation 3 NavDisplay

**Files:**
- Modify: `app/src/main/java/eu/indiewalkabout/mathbrainer/core/presentation/navigation/NavGraph.kt`
- Modify: `app/src/main/java/eu/indiewalkabout/mathbrainer/feat_home/presentation/ui/HomeGameActivity.kt`
- Verify: `app/src/main/java/eu/indiewalkabout/mathbrainer/feat_settings/presentation/ui/GameSettingsScreen.kt`
- Verify: `app/src/main/java/eu/indiewalkabout/mathbrainer/feat_credits/presentation/ui/GameCreditsScreen.kt`

- [ ] **Step 1: Write the failing compile check**

Run: `./gradlew :app:compileDebugKotlin`

Expected: FAIL before the migration because `NavGraph.kt` still imports `NavHost`, `composable`, and `NavHostController`, while `HomeScreen.kt` and `StatisticScreen.kt` no longer accept a controller.

- [ ] **Step 2: Replace activity-level `rememberNavController()` usage**

Update `HomeGameActivity.kt`:

```kotlin
setContent {
    MathBrainerTheme {
        NavGraph()
    }
}
```

No `rememberNavController()` should remain in the activity.

- [ ] **Step 3: Rebuild NavGraph with Navigation 3**

Replace `NavGraph.kt` with this structure:

```kotlin
package eu.indiewalkabout.mathbrainer.core.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberDecoratedNavEntries
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
fun NavGraph() {
    val navigationState = rememberNavigationState()
    val navigator = Navigator(navigationState)
    val decorator = rememberSaveableStateHolderNavEntryDecorator<AppRoute>()
    val entries = rememberDecoratedNavEntries(
        backStack = navigationState.backStack,
        entryDecorators = listOf(decorator),
        entryProvider = entryProvider {
            entry<HomeRoute> {
                val viewModel = hiltViewModel<HomeViewModel>()
                ScreenWithBottomBanner {
                    HomeScreen(
                        onOpenStatistics = { navigator.navigate(StatisticsRoute) },
                        onOpenSettings = { navigator.navigate(GameSettingsRoute) },
                        onOpenGame = { gameType -> navigator.navigate(GameRouteResolver.resolve(gameType)) },
                        homeViewModel = viewModel
                    )
                }
            }
            entry<MathWriteRoute> { route ->
                ScreenWithBottomBanner {
                    MathWriteGameScreen(
                        operation = route.operation,
                        onBack = navigator::back
                    )
                }
            }
            entry<MathChooseRoute> { route ->
                ScreenWithBottomBanner {
                    MathChooseGameScreen(
                        operation = route.operation,
                        onBack = navigator::back
                    )
                }
            }
            entry<DoubleNumberRoute> {
                ScreenWithBottomBanner { DoubleNumberGameScreen(onBack = navigator::back) }
            }
            entry<RandomOperationRoute> {
                ScreenWithBottomBanner { RandomOperationGameScreen(onBack = navigator::back) }
            }
            entry<MemoryFlashRoute> {
                ScreenWithBottomBanner { MemoryFlashGameScreen(onBack = navigator::back) }
            }
            entry<NumberOrderRoute> {
                ScreenWithBottomBanner { NumberOrderGameScreen(onBack = navigator::back) }
            }
            entry<CountObjectsRoute> {
                ScreenWithBottomBanner { CountObjectsGameScreen(onBack = navigator::back) }
            }
            entry<SequenceCompleteRoute> {
                ScreenWithBottomBanner { SequenceCompleteGameScreen(onBack = navigator::back) }
            }
            entry<FallingOpsRoute> {
                ScreenWithBottomBanner { FallingOpsGameScreen(onBack = navigator::back) }
            }
            entry<EnigmaRoute> {
                ScreenWithBottomBanner { EnigmaGameScreen(onBack = navigator::back) }
            }
            entry<GameSettingsRoute> {
                ScreenWithBottomBanner {
                    GameSettingsScreen(
                        onBack = navigator::back,
                        onCreditsClick = { navigator.navigate(GameCreditsRoute) }
                    )
                }
            }
            entry<GameCreditsRoute> {
                ScreenWithBottomBanner { GameCreditsScreen(onBack = navigator::back) }
            }
            entry<StatisticsRoute> {
                val viewModel = hiltViewModel<StatisticViewModel>()
                ScreenWithBottomBanner {
                    StatisticScreen(
                        onBack = navigator::back,
                        onOpenGame = { gameType -> navigator.navigate(GameRouteResolver.resolve(gameType)) },
                        statisticViewModel = viewModel
                    )
                }
            }
        }
    )

    NavDisplay(
        backStack = navigationState.backStack,
        onBack = navigator::back,
        entryProvider = entryProvider {
            entries.forEach { entry(it.key) { it.Content() } }
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
```

If the exact `rememberDecoratedNavEntries` call signature differs in the installed Navigation 3 version, adjust the implementation to the package summary docs while preserving the same state-holder structure. [Navigation 3 runtime docs](https://developer.android.com/reference/kotlin/androidx/navigation3/runtime/package-summary), [Navigation 3 UI docs](https://developer.android.com/reference/kotlin/androidx/navigation3/ui/package-summary)

- [ ] **Step 4: Remove old Navigation Compose imports and dependency**

Delete imports/usages of:

```kotlin
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
```

Remove from `app/build.gradle.kts`:

```kotlin
implementation(libs.androidx.navigation.compose)
```

- [ ] **Step 5: Run compile to verify the migration**

Run: `./gradlew :app:compileDebugKotlin`

Expected: PASS

- [ ] **Step 6: Commit**

```bash
git add app/build.gradle.kts app/src/main/java/eu/indiewalkabout/mathbrainer/core/presentation/navigation/NavGraph.kt app/src/main/java/eu/indiewalkabout/mathbrainer/feat_home/presentation/ui/HomeGameActivity.kt
git commit -m "refactor: migrate compose navigation to navigation 3"
```

---

### Task 5: Verify behavior and stabilize tests

**Files:**
- Verify: `app/src/test/java/eu/indiewalkabout/mathbrainer/core/presentation/navigation/NavigatorTest.kt`
- Verify: `app/src/test/java/eu/indiewalkabout/mathbrainer/core/presentation/navigation/GameRouteResolverNav3Test.kt`
- Verify: `app/src/test/java/eu/indiewalkabout/mathbrainer/core/presentation/navigation/NavigationStateTest.kt`

- [ ] **Step 1: Run the focused navigation tests**

Run: `./gradlew :app:testDebugUnitTest --tests "eu.indiewalkabout.mathbrainer.core.presentation.navigation.NavigatorTest" --tests "eu.indiewalkabout.mathbrainer.core.presentation.navigation.GameRouteResolverNav3Test" --tests "eu.indiewalkabout.mathbrainer.core.presentation.navigation.NavigationStateTest"`

Expected: PASS

- [ ] **Step 2: Run the full unit test suite**

Run: `./gradlew testDebugUnitTest`

Expected: PASS

- [ ] **Step 3: Assemble debug build**

Run: `./gradlew :app:assembleDebug`

Expected: `BUILD SUCCESSFUL`

- [ ] **Step 4: Manual smoke test checklist**

Run the app and verify:

```text
1. Home opens as the first screen
2. Statistics button from Home opens Statistics
3. Back from Statistics returns to Home
4. Settings opens from Home
5. Credits opens from Settings
6. Back from Credits returns to Settings
7. Back from Settings returns to Home
8. Each game card still opens the correct game
9. Math Write and Math Choose still receive the correct operation argument
10. Back from every game returns to the previous screen
11. Banner remains visible at the bottom of navigable screens
```

- [ ] **Step 5: Commit**

```bash
git add .
git commit -m "test: verify navigation 3 migration"
```

---

## Notes for the implementer

- Keep user-facing behavior unchanged. This migration is architectural, not a UX redesign.
- Do not introduce multiple back stacks yet.
- Do not add animated scene customization during the first migration pass.
- If Navigation 3 requires `@Serializable` support in the module and the project does not already have the Kotlin serialization plugin configured, add the minimal plugin/dependency setup needed and extend Task 1 accordingly.
- If the installed Navigation 3 version differs from the sample signatures in the docs, adapt to the exact API surface from the official references while keeping the same state-holder and typed-route structure.

