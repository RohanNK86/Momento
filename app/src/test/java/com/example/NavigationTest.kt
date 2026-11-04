package com.example

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import com.example.ui.theme.AppTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class NavigationTest {
    @get:Rule val compose = createComposeRule()

    @Test
    fun splashIsRemovedAndSettingsReturnsHome() {
        lateinit var backDispatcher: OnBackPressedDispatcher
        compose.setContent {
            backDispatcher = checkNotNull(LocalOnBackPressedDispatcherOwner.current).onBackPressedDispatcher
            AppTheme { MomentoApp() }
        }
        compose.mainClock.advanceTimeBy(1500)
        compose.waitForIdle()
        compose.onNodeWithContentDescription("Settings").assertIsDisplayed().performClick()
        compose.mainClock.advanceTimeBy(1000)
        compose.onNodeWithContentDescription("Back").assertIsDisplayed().performClick()
        compose.mainClock.advanceTimeBy(1000)
        compose.onNodeWithContentDescription("Settings").assertIsDisplayed()
        compose.onNodeWithText("Tasks").performClick()
        compose.mainClock.advanceTimeBy(1000)
        compose.runOnUiThread { backDispatcher.onBackPressed() }
        compose.mainClock.advanceTimeBy(1000)
        compose.onNodeWithContentDescription("Settings").assertIsDisplayed()
        compose.onAllNodesWithText("Make today count.").assertCountEquals(0)
    }
}
