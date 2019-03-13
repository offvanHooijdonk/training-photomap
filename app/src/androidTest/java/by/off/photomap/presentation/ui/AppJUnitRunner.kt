package by.off.photomap.presentation.ui

import android.app.Application
import android.content.Context
import android.support.test.runner.AndroidJUnitRunner

class AppJUnitRunner : AndroidJUnitRunner() {
    override fun newApplication(cl: ClassLoader?, className: String?, context: Context?): Application =
        super.newApplication(cl, TestApp::class.java.name, context)
}