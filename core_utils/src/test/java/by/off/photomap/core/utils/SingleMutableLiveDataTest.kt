package by.off.photomap.core.utils

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.Observer
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class SingleMutableLiveDataTest {
    companion object {
        const val SAMPLE_LIVE_DATA_OBJECT = "sample string"
    }

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @Test
    fun liveDataTest() {
        val ld = SingleMutableLiveData<String>()
        ld.observe(StubLifecycleOwner(), Observer {
            assertEquals("Expecting to receive a non null value $SAMPLE_LIVE_DATA_OBJECT", SAMPLE_LIVE_DATA_OBJECT, it)
        })
        ld.postValue(SAMPLE_LIVE_DATA_OBJECT)

        assertNull("Expecting read value to be null",ld.value)
    }
}
// todo move to separate place
class StubLifecycleOwner : LifecycleOwner {
    private val lifecycleRegistry = LifecycleRegistry(this).apply { markState(Lifecycle.State.STARTED) }

    override fun getLifecycle(): Lifecycle = lifecycleRegistry
}