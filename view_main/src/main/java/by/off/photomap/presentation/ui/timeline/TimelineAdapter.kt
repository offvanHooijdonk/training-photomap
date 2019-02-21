package by.off.photomap.presentation.ui.timeline

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import by.off.photomap.model.PhotoInfo
import by.off.photomap.presentation.ui.R
import by.off.photomap.presentation.ui.databinding.ItemTimelineBinding
import java.util.*

class TimelineAdapter(
    private val ctx: Context,
    private val onClick: (position: Int, photoId: String) -> Unit
) :
    RecyclerView.Adapter<TimelineAdapter.ViewHolder>() {

    private var photos: List<PhotoInfo> = emptyList()

    override fun onCreateViewHolder(container: ViewGroup, type: Int): ViewHolder =
        ViewHolder(
            DataBindingUtil.inflate(LayoutInflater.from(ctx), R.layout.item_timeline, container, false),
            ItemViewModel(null, type)
        )

    override fun getItemCount(): Int = photos.size

    override fun getItemViewType(position: Int): Int {
        return when {
            position == 0 -> ItemViewModel.TYPE_WITH_PERIOD
            !isSameMonth(photos[position], photos[position - 1]) -> ItemViewModel.TYPE_WITH_PERIOD
            else -> ItemViewModel.TYPE_JUST_DATA
        }
    }

    override fun onBindViewHolder(vh: ViewHolder, position: Int) {
        val photo = photos[position]
        vh.bind(photo, getItemViewType(position))

        vh.binding.itemRoot.setOnClickListener {
            onClick(position, photo.id)
        }
    }

    inner class ViewHolder(val binding: ItemTimelineBinding, val model: ItemViewModel) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PhotoInfo, type: Int) {
            binding.invalidateAll()
            model.photoInfo = item
            model.itemType = type
            binding.model = model
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