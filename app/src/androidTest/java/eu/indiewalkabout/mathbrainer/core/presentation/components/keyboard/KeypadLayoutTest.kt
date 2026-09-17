package eu.indiewalkabout.mathbrainer.core.presentation.components.keyboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import eu.indiewalkabout.mathbrainer.R
import eu.indiewalkabout.mathbrainer.core.presentation.theme.MathBrainerTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class KeypadLayoutTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun submitKeyRemainsReachableInsideScrollableGameContent() {
        composeRule.setContent {
            MathBrainerTheme {
                Column(
                    modifier = Modifier
                        .height(320.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.height(240.dp))
                    Keypad(
                        feedback = null,
                        inputValue = "",
                        onDigitPressed = {},
                        onDelete = {},
                        onSubmit = {}
                    )
                }
            }
        }

        val submitLabel = InstrumentationRegistry.getInstrumentation()
            .targetContext.getString(R.string.submit_label)
        composeRule.onNodeWithText(submitLabel)
            .performScrollTo()
            .assertIsDisplayed()
    }
}
