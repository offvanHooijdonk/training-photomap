package by.off.photomap.presentation.ui

import android.app.Activity
import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.support.test.rule.ActivityTestRule
import org.junit.Rule
import org.junit.rules.TestRule

abstract class AbstractActivityUITest<T : Activity> {
    abstract val activityScenarioRule : ActivityTestRule<T>

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    protected fun startActivity() {
        activityScenarioRule.launchActivity(null)
    }

    protected fun startActivityAndWait() {
        startActivity()
        waitActivity()
    }
}