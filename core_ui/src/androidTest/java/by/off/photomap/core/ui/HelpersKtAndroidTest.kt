package by.off.photomap.core.ui

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HelpersKtAndroidTest {
    companion object {
        private const val DEGREE = "˚"
        private const val MINUTE = "´"
    }

    @Test
    fun formatLatitude() {
        var result = by.off.photomap.core.ui.formatLatitude(50.0, InstrumentationRegistry.getTargetContext())
        assertEquals("", "50$DEGREE 00$MINUTE N", result)

        result = by.off.photomap.core.ui.formatLatitude(-50.5, InstrumentationRegistry.getTargetContext())
        assertEquals("", "50$DEGREE 30$MINUTE S", result)

        result = by.off.photomap.core.ui.formatLatitude(0.0, InstrumentationRegistry.getTargetContext())
        assertEquals("", "0$DEGREE 00$MINUTE N", result)
    }

    @Test
    fun formatLongitude() {
        var result = by.off.photomap.core.ui.formatLongitude(50.0, InstrumentationRegistry.getTargetContext())
        assertEquals("", "50$DEGREE 00$MINUTE E", result)

        result = by.off.photomap.core.ui.formatLongitude(-50.5, InstrumentationRegistry.getTargetContext())
        assertEquals("", "50$DEGREE 30$MINUTE W", result)

        result = by.off.photomap.core.ui.formatLongitude(0.0, InstrumentationRegistry.getTargetContext())
        assertEquals("", "0$DEGREE 00$MINUTE E", result)
    }
}