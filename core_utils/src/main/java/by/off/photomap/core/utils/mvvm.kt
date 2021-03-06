package by.off.photomap.core.utils

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations

fun <X, Y> LiveData<X>.map(mapFunction: (X) -> Y): LiveData<Y> =
    Transformations.map(this, mapFunction)
