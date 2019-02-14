package by.off.photomap.core.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.location.Location
import android.os.Build
import android.support.annotation.ColorInt
import android.support.annotation.ColorRes
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

fun View.fadeAway(duration: Long = 150) {
    fade(this, 1.0f, 0.0f, duration) { this.hide() }
}

fun View.fadeIn(duration: Long = 250) {
    fade(this, 0.0f, 1.0f, duration) { this.show() }
}

private fun fade(view: View, start: Float, end: Float, duration: Long, onFinish: () -> Unit) {
    ObjectAnimator.ofFloat(view, View.ALPHA, start, end).apply {
        setDuration(duration)
        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                onFinish()
            }
        })
    }.start()
}

fun Snackbar.colorError() =
    this.apply {
        view.findViewById<TextView>(android.support.design.R.id.snackbar_text)
            .setTextColor(this.context.resources.getColor(R.color.snackbar_error_text))
    }

fun Context.getColorVal(@ColorRes colorRes: Int) =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        this.getColor(colorRes)
    } else {
        this.resources.getColor(colorRes)
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

fun formatLatitude(value: Double, ctx: Context) =
    StringBuilder().append(formatGeoCoordinate(value)).append(" ").append(
        if (value >= 0) ctx.getString(R.string.lat_north) else ctx.getString(R.string.lat_south)
    ).toString()

fun formatLongitude(value: Double, ctx: Context) =
    StringBuilder().append(formatGeoCoordinate(value)).append(" ").append(
        if (value >= 0) ctx.getString(R.string.long_west) else ctx.getString(R.string.long_east)
    ).toString()


private fun formatGeoCoordinate(value: Double): String {
    val coordString = Location.convert(Math.abs(value), Location.FORMAT_MINUTES)
    return try {
        coordString.split(":").let {
            val degree = it[0]
            val dotIndex = it[1].indexOfFirst { c -> c == '.' }
            val minutes = it[1].subSequence(0, dotIndex + 3)
            "$degree˚ $minutes´"
        }
    } catch (e: Exception) {
        coordString
    }
}
