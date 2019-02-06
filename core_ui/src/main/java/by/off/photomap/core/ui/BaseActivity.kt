package by.off.photomap.core.ui

import android.arch.lifecycle.ViewModel
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity
import by.off.photomap.core.utils.di.ViewModelFactory
import javax.inject.Inject

abstract class BaseActivity<VM : ViewModel, B : ViewDataBinding> : AppCompatActivity() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    protected lateinit var binding: B
        private set

    @get:LayoutRes
    abstract val layout: Int

    abstract val viewModelClass: Class<VM>

    protected lateinit var viewModel: VM
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inject()
        viewModel = viewModelFactory.create(viewModelClass)
        binding = DataBindingUtil.setContentView(this, layout)
    }

    abstract fun inject()
}