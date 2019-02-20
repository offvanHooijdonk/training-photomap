package by.off.photomap.presentation.ui

import android.databinding.BindingAdapter
import android.graphics.Bitmap
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
import by.off.photomap.core.ui.*
import by.off.photomap.core.ui.dto.CategoryInfo
import by.off.photomap.core.utils.LOGCAT
import by.off.photomap.model.PhotoInfo
import by.off.photomap.presentation.ui.timeline.TimelineAdapter
import by.off.photomap.util.thumbs.Thumbs
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
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

private var tagColors: IntArray? = null

@BindingAdapter("tagText")
fun setChipTagText(chip: Chip, tag: String) {
    chip.text = tag

    val colors = tagColors ?: chip.context.resources.getIntArray(R.array.tag_colors).also { tagColors = it }
    val colorIndex = tag.hashCode() % colors.size

    chip.setChipBackgroundColorResource(colors[colorIndex])
}

@BindingAdapter("filePath")
fun setImageFile(imageView: ImageView, filePath: String?) {
    filePath?.let {
        val file = File(filePath)
        if (file.exists()) {
            CoroutineScope(Dispatchers.Main).launch {
                val targetView: ImageView? = imageView
                val bitmap: Bitmap = withContext(Dispatchers.IO) {
                    BitmapFactory.decodeFile(filePath)
                }
                targetView?.setImageBitmap(bitmap)
            }
        } else {
            imageView.setImageResource(R.drawable.ic_warning_24)
        }
    }
}

@BindingAdapter("thumbPhotoId")
fun setThumbPhotoId(imageView: ImageView, photoId: String) {
    Thumbs.loadById(photoId, imageView)
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
fun setTimelinestampShort(textView: TextView, timestamp: Date) {
    textView.text = DateHelper.formatDateShort(timestamp)
}

@BindingAdapter("period")
fun setTimelinePeriod(textView: TextView, timestamp: Date) {
    textView.text = DateHelper.formatTimelineDate(timestamp, textView.context)
}

@BindingAdapter("latitude")
fun setLatitudeText(textView: TextView, latitude: Double?) {
    latitude?.also { textView.text = formatLatitude(latitude, textView.context) }
}

@BindingAdapter("longitude")
fun setLongitudeText(textView: TextView, longitude: Double?) {
    longitude?.also { textView.text = formatLongitude(longitude, textView.context) }
}

@BindingAdapter("place")
fun setPlaceDescription(textView: TextView, description: String?) {
    if (description?.isNotEmpty() == true) {
        textView.text = description
        textView.fadeIn()
    }
}

@BindingAdapter("snackbar")
fun setErrorToSnackbar(view: View, msg: String?) {
    msg?.let {
        Snackbar.make(view, it, Snackbar.LENGTH_LONG).colorError().show()
    }
}