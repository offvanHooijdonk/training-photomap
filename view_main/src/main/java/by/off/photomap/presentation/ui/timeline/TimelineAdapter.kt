package by.off.photomap.presentation.ui.timeline

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import by.off.photomap.core.ui.DateHelper
import by.off.photomap.core.ui.show
import by.off.photomap.model.PhotoInfo
import by.off.photomap.presentation.ui.R
import by.off.photomap.presentation.ui.databinding.ItemTimelineBinding
import java.util.*

class TimelineAdapter(private val ctx: Context, private val onClick: (position: Int, photoId: String) -> Unit) :
    RecyclerView.Adapter<TimelineAdapter.ViewHolder>() {
    companion object {
        const val TYPE_JUST_DATA = 0
        const val TYPE_WITH_PERIOD = 1
    }

    private var photos: List<PhotoInfo> = emptyList()

    override fun onCreateViewHolder(container: ViewGroup, type: Int): ViewHolder =
        ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(ctx), R.layout.item_timeline, container, false))

    override fun getItemCount(): Int = photos.size

    override fun getItemViewType(position: Int): Int {
        return when {
            position == 0 -> TYPE_WITH_PERIOD
            !isSameMonth(photos[position], photos[position - 1]) -> TYPE_WITH_PERIOD
            else -> TYPE_JUST_DATA
        }
    }

    override fun onBindViewHolder(vh: ViewHolder, position: Int) {
        val photo = photos[position]
        val showDateHeader = getItemViewType(position) == TYPE_WITH_PERIOD
        vh.bind(photo)
        with(vh.binding) {
            if (showDateHeader) {
                txtPeriod.show()
                txtPeriod.text = DateHelper.formatTimelineDate(photo.shotTimestamp)
            }
            itemRoot.setOnClickListener {
                onClick(position, photo.id)
            }
        }
    }

    class ViewHolder(val binding: ItemTimelineBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PhotoInfo) {
            binding.item = item
        }
    }

    fun update(list: List<PhotoInfo>) {
        photos = list
        notifyDataSetChanged()
    }

    private fun isSameMonth(p1: PhotoInfo, p2: PhotoInfo): Boolean {
        val c1 = Calendar.getInstance().apply { time = p1.shotTimestamp }
        val c2 = Calendar.getInstance().apply { time = p2.shotTimestamp }

        return c2[Calendar.MONTH] == c1[Calendar.MONTH] && c2[Calendar.YEAR] == c1[Calendar.YEAR]
    }
}