package by.off.photomap.presentation.ui

import android.app.Activity
import android.content.pm.ActivityInfo

abstract class BaseActivityRobot {
    fun requestPortraintOrientation(act: Activity) {
        act.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        waitAnim()
    }

    fun requestLandscapeOrientation(act: Activity) {
        act.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        waitAnim()
    }
}