package by.off.photomap.presentation.ui

import android.databinding.BindingAdapter
import android.graphics.BitmapFactory
import android.support.design.chip.Chip
import android.support.design.widget.Snackbar
import android.support.design.widget.TextInputLayout
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import by.off.photomap.core.ui.DateHelper
import by.off.photomap.core.ui.colorError
import by.off.photomap.core.ui.dto.CategoryInfo
import by.off.photomap.core.utils.LOGCAT
import by.off.photomap.model.PhotoInfo
import by.off.photomap.presentation.ui.timeline.TimelineAdapter
import kotlinx.android.synthetic.main.act_photo_view_edit.*
import java.io.File
import java.text.DateFormat
import java.util.*

@BindingAdapter("enabled")
fun setSpinnerEnabled(spinner: Spinner, enabledFlag: Boolean) {
    spinner.isEnabled = enabledFlag
}

@BindingAdapter("timestamp")
fun setPhotoTimestamp(textView: TextView, date: Date?) {
    textView.text = if (date != null) DateHelper.formatDateFull(date) else null
}

@BindingAdapter("errorMessage")
fun setTextInpuLayoutError(til: TextInputLayout, errorMessage: String?) {
    til.error = errorMessage
}

@BindingAdapter("category")
fun setChipCategoryLabelColor(chip: Chip, categoryId: Int) {
    chip.setText(CategoryInfo(categoryId).labelRes)
    chip.setChipBackgroundColorResource(CategoryInfo(categoryId).backColorRes)
}

@BindingAdapter("filePath")
fun setImageFile(imageView: ImageView, filePath: String?) {
    filePath?.let {
        val file = File(filePath)
        if (file.exists()) {
            imageView.setImageBitmap(BitmapFactory.decodeFile(filePath))
        } else {
            imageView.setImageResource(R.drawable.ic_warning_24)
        }
    }
}

@BindingAdapter("items")
fun setTimelineList(rv: RecyclerView, list: List<PhotoInfo>?) {
    Log.i(LOGCAT, "Updating Recycler View, size: ${list?.size}")
    list?.let { (rv.adapter as TimelineAdapter).update(it) }
}

@BindingAdapter("category")
fun setCategoryLabelColor(textView: TextView, categoryId: Int) {
    textView.setText(CategoryInfo(categoryId).labelRes)
    val catColor = textView.context.resources.getColor(CategoryInfo(categoryId).textColorRes)
    textView.setTextColor(catColor)
}

@BindingAdapter("timestampShort")
fun setTimestampShort(textView: TextView, timestamp: Date) {
    textView.text = DateHelper.formatDateShort(timestamp)
}

@BindingAdapter("snackbar")
fun setErrorToSnackbar(view: View, msg: String?) {
    msg?.let {
        Snackbar.make(view, it, Snackbar.LENGTH_LONG).colorError().show()
    }
}