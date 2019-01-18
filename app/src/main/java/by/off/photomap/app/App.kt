package by.off.photomap.app

import android.app.Application
import by.off.photomap.storage.parse.impl.ParseHelper

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        ParseHelper.initParse(this)
    }
}