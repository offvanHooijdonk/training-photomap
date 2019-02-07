package by.off.photomap.core.ui

import android.content.Context
import android.support.annotation.ColorInt
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.GONE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun Snackbar.colorError() =
    this.apply {
        view.findViewById<TextView>(android.support.design.R.id.snackbar_text)
            .setTextColor(this.context.resources.getColor(R.color.snackbar_error_text))
    }

val AppCompatActivity.ctx: Context
    get() = this

val Fragment.ctx: Context
    get() = this.requireContext()

fun SwipeRefreshLayout.setupDefaults() {
    this.setColorSchemeResources(R.color.refresh_1, R.color.refresh_2, R.color.refresh_3)
}

fun hue(@ColorInt color: Int): Float {
    val r = (color / 0xFFFF).let { if (it < 0) 255 + it else it }
    val g = color / 0xFF and 0xFF
    val b = color and 0xFF
    val max = Math.max(r, Math.max(g, b))
    val min = Math.min(r, Math.min(g, b))

    return (
            when (max) {
                r -> (g - b).toFloat() / (max - min).toFloat()
                g -> 2.0f + (b - r).toFloat() / (max - min).toFloat()
                else -> 4.0f + (r - g).toFloat() / (max - min).toFloat()
            } * 60
            ).let { if (it < 0) it + 360.0f else it }
}