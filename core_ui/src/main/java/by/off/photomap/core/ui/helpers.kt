package by.off.photomap.core.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.res.Configuration
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
import by.off.photomap.core.utils.di.ViewModelFactory

// region VISIBILITY
fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.GONE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.isVisible() = this.visibility == View.VISIBLE

fun View.fadeAway(duration: Long = 150, onFinish: (() -> Unit)? = null) {
    fade(this, 1.0f, 0.0f, duration) {
        this.hide()
        onFinish?.invoke()
    }
}

fun View.fadeIn(duration: Long = 250, onFinish: (() -> Unit)? = null) {
    this.show()
    fade(this, 0.0f, 1.0f, duration) { onFinish?.invoke() }
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
// endregion

// region ACTIVITY/FRAGMENT/CONTEXT
val AppCompatActivity.ctx: Context
    get() = this

val Fragment.ctx: Context
    get() = this.requireContext()

fun SwipeRefreshLayout.setupDefaults() {
    this.setColorSchemeResources(R.color.refresh_1, R.color.refresh_2, R.color.refresh_3)
}

fun Context.isLandscape() = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

fun Context.isPortrait() = resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT

fun <T : ViewModel> Fragment.getViewModel(factory: ViewModelFactory, modelClass: Class<T>) =
    ViewModelProviders.of(this, factory)[modelClass]
// endregion

// region COLORS
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
// endregion

// region FORMATS
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
// endregion