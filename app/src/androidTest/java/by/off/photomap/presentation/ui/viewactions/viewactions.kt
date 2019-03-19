package by.off.photomap.presentation.ui.viewactions

import android.support.annotation.ColorInt
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.TabLayout
import android.support.test.espresso.ViewAssertion
import android.support.test.espresso.action.CoordinatesProvider
import android.support.test.espresso.action.GeneralClickAction
import android.support.test.espresso.action.Press
import android.support.test.espresso.action.Tap
import junit.framework.AssertionFailedError

fun checkTabSelected(position: Int): ViewAssertion =
    ViewAssertion { view, _ ->
        if (view is TabLayout) {
            val tab = view.getTabAt(position) ?: throw AssertionFailedError("No tab at position $position")
            if (!tab.isSelected) throw AssertionFailedError("Tab at position $position is not selected")
        } else {
            throw AssertionFailedError("The vew is not a TabLayout")
        }
    }

fun checkFABColor(@ColorInt colorValue: Int) =
    ViewAssertion { view, _ ->
        val fab = view as? FloatingActionButton ?: throw AssertionFailedError("The view provided is not a Floating Action Button")
        if (fab.backgroundTintList?.defaultColor != colorValue) throw AssertionFailedError("The Floating Action Button color does not much $colorValue")
    }

fun longClickAt(x: Int, y: Int) =
        GeneralClickAction(Tap.LONG,
            CoordinatesProvider { FloatArray(2) { i ->
                when(i) {
                    0 -> x.toFloat()
                    1 -> y.toFloat()
                    else -> 0.0f
                }
            } },
            Press.FINGER)