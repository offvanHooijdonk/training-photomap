package by.off.photomap.presentation.ui

import android.databinding.BindingAdapter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.design.chip.Chip
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.design.widget.TextInputLayout
import android.support.v7.widget.RecyclerView
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import by.off.photomap.core.ui.*
import by.off.photomap.core.ui.dto.CategoryInfo
import by.off.photomap.core.utils.findHashTags
import by.off.photomap.model.PhotoInfo
import by.off.photomap.presentation.ui.timeline.TimelineAdapter
import by.off.photomap.util.thumbs.Thumbs
import com.google.android.flexbox.FlexboxLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*
import kotlin.math.absoluteValue

@BindingAdapter("android:visibility")
fun setViewVisibility(view: View, visible: Boolean) {
    view.visibility = if (visible) View.VISIBLE else View.GONE
}

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

private var tagColors = listOf(
    R.color.tag_1, R.color.tag_2, R.color.tag_3, R.color.tag_4,
    R.color.tag_5, R.color.tag_6, R.color.tag_7, R.color.tag_8,
    R.color.tag_9, R.color.tag_10, R.color.tag_11, R.color.tag_12,
    R.color.tag_13, R.color.tag_14, R.color.tag_15, R.color.tag_16,
    R.color.tag_17, R.color.tag_18, R.color.tag_19, R.color.tag_20,
    R.color.tag_21, R.color.tag_22, R.color.tag_23, R.color.tag_24,
    R.color.tag_25
)

@BindingAdapter("tagText")
fun setChipTagText(chip: Chip, tag: String) {
    chip.text = tag

    val colorIndex = tag.toLowerCase().hashCode().absoluteValue % tagColors.size
    chip.setChipBackgroundColorResource(tagColors[colorIndex])
}

@BindingAdapter("searchText", "android:text")
fun setSearchTextHighlight(textView: TextView, searchText: String, text: String) {
    textView.text = decorateTextWithSearch(/*textView.text.toString()*/text, searchText.trim())
}

private fun decorateTextWithSearch(text: String, search: String) =
    if (search.isNotEmpty()) {
        val value = text.toLowerCase()
        val txt = search.toLowerCase()
        val ssb = SpannableStringBuilder(value)
        var lastIndex = 0
        while (value.indexOf(txt, lastIndex).also { lastIndex = it } != -1) {
            ssb.setSpan(StyleSpan(android.graphics.Typeface.BOLD), lastIndex, lastIndex + txt.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            lastIndex += txt.length
        }
        ssb
    } else text

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

@BindingAdapter("tagsContent")
fun setPhotoTagsList(layout: FlexboxLayout, textWithTags: String?) {
    textWithTags?.let {
        val tagsList = findHashTags(textWithTags)
        for (tag in tagsList) {
            val chip = LayoutInflater.from(layout.context)
                .inflate(R.layout.view_chip_tag, layout, false) as Chip
            setChipTagText(chip, tag)
            layout.addView(chip)
        }
    }
}

@BindingAdapter("thumbPhotoId")
fun setThumbPhotoId(imageView: ImageView, photoId: String) {
    Thumbs.loadById(photoId, imageView)
}

@BindingAdapter("items")
fun setTimelineList(rv: RecyclerView, list: List<PhotoInfo>?) {
    list?.let { (rv.adapter as? TimelineAdapter)?.update(it) }
}

@BindingAdapter("category")
fun setCategoryLabelColor(textView: TextView, categoryId: Int) {
    textView.setText(CategoryInfo(categoryId).labelRes)
    val catColor = textView.context.getColorVal(CategoryInfo(categoryId).textColorRes)
    textView.setTextColor(catColor)
}

@BindingAdapter("timestampShort")
fun setTimelineStampShort(textView: TextView, timestamp: Date) {
    textView.text = DateHelper.formatDateShort(timestamp)
}

@BindingAdapter("period")
fun setTimelinePeriod(textView: TextView, timestamp: Date) {
    textView.text = DateHelper.formatTimelineDate(timestamp, textView.context.resources.getStringArray(by.off.photomap.core.ui.R.array.months_full))
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

@BindingAdapter("fabVisibility")
fun setFABVisibility(fab: FloatingActionButton, visible: Boolean) {
    if (visible) fab.show() else fab.hide()
}