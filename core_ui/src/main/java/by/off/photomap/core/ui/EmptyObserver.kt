package by.off.photomap.core.ui

import android.arch.lifecycle.Observer

/**
 * An empty Observer, that does nothing on event.
 * Used instead of SAM-lambda Observer, which is compiled static and causes errors.
 */
class EmptyObserver<T> : Observer<T> {
    override fun onChanged(t: T?) {}
}