package by.off.photomap.core.ui

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.util.Log
import by.off.photomap.core.utils.LOGCAT
import by.off.photomap.core.utils.di.ViewModelFactory

abstract class BaseActivity : AppCompatActivity() {
    abstract var viewModelFactory: ViewModelFactory

    protected fun <T : ViewModel> getViewModel(modelClass: Class<T>): T =
        ViewModelProviders.of(this, viewModelFactory).get(modelClass)

}