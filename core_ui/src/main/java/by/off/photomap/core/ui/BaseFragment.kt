package by.off.photomap.core.ui

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.Fragment
import by.off.photomap.core.utils.di.ViewModelFactory

abstract class BaseFragment : Fragment() {
    abstract var viewModelFactory: ViewModelFactory

    protected fun <T : ViewModel> getViewModel(modelClass: Class<T>): T =
        ViewModelProviders.of(this, viewModelFactory).get(modelClass)
}