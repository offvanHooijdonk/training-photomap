package by.off.photomap.core.utils

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer

class SingleMutableLiveData<T> : MutableLiveData<T>() {
    private var obs: Observer<T>? = null

    override fun observe(owner: LifecycleOwner, observer: Observer<T>) {
        super.observe(owner, Observer {
            value?.let {
                obs?.onChanged(value)
                value = null
            }
        })
        obs = observer
    }

    override fun observeForever(observer: Observer<T>) {
        super.observeForever {
            value?.let {
                obs?.onChanged(value)
                value = null
            }
        }
        obs = observer
    }
}