package by.off.photomap.presentation.ui.main

import android.support.test.espresso.Espresso
import android.support.test.espresso.matcher.ViewMatchers
import by.off.photomap.R
import by.off.photomap.presentation.ui.BaseActivityRobot
import by.off.photomap.presentation.ui.checkGone
import by.off.photomap.presentation.ui.checkVisible
import by.off.photomap.presentation.ui.viewactions.selectTabAt

class MainActivityRobot : BaseActivityRobot() {
    companion object {
        const val TABS_COUNT = 2
    }

    var tabIndex = 0

    fun checkFABsVisibility() {
        when (tabIndex) {
            0 -> checkFABsVisible()
            1 -> checkFABsGone()
        }
    }

    fun testFABsOnTabsSwitch(times: Int) {
        for (i in 1..(times)) {
            selectNextTab()
            checkFABsVisibility()
        }
    }

    private fun selectNextTab() {
        tabIndex = if (tabIndex == 0) 1 else 0
        Espresso.onView(ViewMatchers.withId(R.id.tabs)).perform(selectTabAt(tabIndex))
    }

    private fun checkFABsVisible() {
        Espresso.onView(ViewMatchers.withId(R.id.fabAddPhoto)).checkVisible()
        Espresso.onView(ViewMatchers.withId(R.id.fabLocation)).checkVisible()
    }

    private fun checkFABsGone() {
        Espresso.onView(ViewMatchers.withId(R.id.fabAddPhoto)).checkGone()
        Espresso.onView(ViewMatchers.withId(R.id.fabLocation)).checkGone()
    }
}

fun mainActivity(func: MainActivityRobot.() -> Unit) = MainActivityRobot().apply { func() }