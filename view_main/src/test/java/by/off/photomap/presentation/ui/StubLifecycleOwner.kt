package by.off.photomap.presentation.ui

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LifecycleRegistry

class StubLifecycleOwner : LifecycleOwner {
    private val lifecycleRegistry = LifecycleRegistry(this).apply { markState(Lifecycle.State.STARTED) }

    override fun getLifecycle(): Lifecycle = lifecycleRegistry
}