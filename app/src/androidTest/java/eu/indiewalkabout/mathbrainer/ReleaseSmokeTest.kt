package eu.indiewalkabout.mathbrainer

import androidx.annotation.StringRes
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import eu.indiewalkabout.mathbrainer.feat_home.presentation.ui.HomeGameActivity
import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ReleaseSmokeTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<HomeGameActivity>()

    private val targetContext
        get() = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun activeLocale_matchesExpectedMatrixLocale() {
        val expectedTag = requireNotNull(
            InstrumentationRegistry.getArguments().getString(EXPECTED_LOCALE_ARGUMENT)
        ) { "Missing instrumentation argument: $EXPECTED_LOCALE_ARGUMENT" }
        val expectedLocale = Locale.forLanguageTag(expectedTag)
        val activeLocale = targetContext.resources.configuration.locales[0]

        assertEquals("Active language", expectedLocale.language, activeLocale.language)
        assertEquals("Active country", expectedLocale.country, activeLocale.country)
    }

    @Test
    fun coreNavigationAndGames_surviveActivityRecreation() {
        assertScreen(R.string.math_brainer_title)

        composeRule.onNodeWithContentDescription("Statistics").performClick()
        assertScreen(R.string.statistics_title)
        navigateBack()

        composeRule.onNodeWithContentDescription("Settings").performClick()
        assertScreen(R.string.credits_title)
        navigateBack()

        openGame(R.string.game_card_choose_result_sum_text)
        assertScreen(R.string.math_choose_title)
        recreateAndAssertScreen(R.string.math_choose_title)
        navigateBack()

        openGame(R.string.game_card_memory_flash_text, scrollToGame = true)
        assertScreen(R.string.memory_flash_title)
        recreateAndAssertScreen(R.string.memory_flash_title)
        navigateBack()

        assertScreen(R.string.math_brainer_title)
    }

    private fun openGame(@StringRes titleRes: Int, scrollToGame: Boolean = false) {
        val title = targetContext.getString(titleRes)
        if (scrollToGame) {
            composeRule.onNode(hasScrollAction()).performScrollToNode(hasText(title))
        }
        composeRule.waitUntil(timeoutMillis = SCREEN_TIMEOUT_MILLIS) {
            composeRule.onAllNodesWithText(title).fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText(title).performClick()
    }

    private fun navigateBack() {
        composeRule.onNodeWithContentDescription(
            targetContext.getString(R.string.navigate_back)
        ).performClick()
        assertScreen(R.string.math_brainer_title)
    }

    private fun recreateAndAssertScreen(@StringRes titleRes: Int) {
        composeRule.activityRule.scenario.recreate()
        assertScreen(titleRes)
    }

    private fun assertScreen(@StringRes titleRes: Int) {
        val title = targetContext.getString(titleRes)
        composeRule.waitUntil(timeoutMillis = SCREEN_TIMEOUT_MILLIS) {
            composeRule.onAllNodesWithText(title).fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onAllNodesWithText(title)[0].assertIsDisplayed()
    }

    private companion object {
        const val EXPECTED_LOCALE_ARGUMENT = "expectedLocale"
        const val SCREEN_TIMEOUT_MILLIS = 15_000L
    }
}
