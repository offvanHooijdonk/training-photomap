package by.off.photomap.core.ui

import android.content.Context
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
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