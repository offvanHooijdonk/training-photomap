package by.off.photomap.presentation.ui

import android.support.test.espresso.ViewInteraction
import android.support.test.espresso.assertion.ViewAssertions
import android.support.test.espresso.matcher.ViewMatchers

const val WAIT_ACTIVITY = 200L
const val WAIT_MAP = 2000L
const val WAIT_BOTTOM_DIALOG = 100L
const val WAIT_A_LITTLE = 20L

fun waitActivity() = Thread.sleep(WAIT_ACTIVITY)
fun waitMap() = Thread.sleep(WAIT_MAP)
fun waitBottomDialog() = Thread.sleep(WAIT_BOTTOM_DIALOG)
fun waitALittle() = Thread.sleep(WAIT_A_LITTLE)

fun ViewInteraction.checkVisible() {
    this.check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
}

fun ViewInteraction.checkGone() {
    this.check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
}

fun ViewInteraction.checkText(text: String) {
    this.check(ViewAssertions.matches(ViewMatchers.withText(text)))
}