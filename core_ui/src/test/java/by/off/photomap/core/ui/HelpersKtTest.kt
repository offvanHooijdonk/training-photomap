package by.off.photomap.core.ui

import org.junit.Assert.assertEquals
import org.junit.Test

class HelpersKtTest {
    companion object {
        const val HUE_RED = 0.0f
        const val HUE_GREEN = 120.0f
        const val HUE_BLUE = 240.0f
        const val HUE_YELLOW = 60.0f
        const val HUE_COLOR_1 = 200.0f
        const val HUE_COLOR_2 = 2.52f
        const val HUE_COLOR_3 = 173.68f

        const val COLOR_RED = 0xff0000
        const val COLOR_GREEN = 0x00ff00
        const val COLOR_BLUE = 0x0000ff
        const val COLOR_YELLOW = 0xffff00
        const val COLOR_1 = 0x4098c4
        const val COLOR_2 = 0xc63d37
        const val COLOR_3 = 0x008577

        const val LAT_NORTH = "N"
        const val LAT_SOUTH = "S"
        const val LON_EAST = "E"
        const val LON_WEST = "W"
    }

    @Test
    fun hue() {
        assertEquals("Hue for red must be $HUE_RED", HUE_RED, by.off.photomap.core.ui.hue(COLOR_RED))
        assertEquals("Hue for green must be $HUE_GREEN", HUE_GREEN, by.off.photomap.core.ui.hue(COLOR_GREEN))
        assertEquals("Hue for blue must be $HUE_BLUE", HUE_BLUE, by.off.photomap.core.ui.hue(COLOR_BLUE))
        assertEquals("Hue for yellow must be $HUE_YELLOW", HUE_YELLOW, by.off.photomap.core.ui.hue(COLOR_YELLOW))
        assertEquals("Hue for color $COLOR_1 must be $HUE_COLOR_1", HUE_COLOR_1, by.off.photomap.core.ui.hue(COLOR_1), 0.01f)
        assertEquals("Hue for color $COLOR_2 must be $HUE_COLOR_2", HUE_COLOR_2, by.off.photomap.core.ui.hue(COLOR_2), 0.01f)
        assertEquals("Hue for color $COLOR_3 must be $HUE_COLOR_3", HUE_COLOR_3, by.off.photomap.core.ui.hue(COLOR_3), 0.01f)
    }
}