package by.off.photomap.presentation.ui.timeline

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.databinding.BindingAdapter
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import by.off.photomap.core.ui.dto.CategoryInfo
import by.off.photomap.core.ui.show
import by.off.photomap.core.utils.LOGCAT
import by.off.photomap.core.utils.di.ViewModelFactory
import by.off.photomap.di.PhotoScreenComponent
import by.off.photomap.model.PhotoInfo
import by.off.photomap.presentation.ui.R
import by.off.photomap.presentation.ui.databinding.ItemTimelineBinding
import by.off.photomap.presentation.ui.databinding.ScreenTimelineBinding
import by.off.photomap.presentation.ui.photo.PhotoViewEditActivity
import by.off.photomap.presentation.viewmodel.timeline.TimelineViewModel
import kotlinx.android.synthetic.main.screen_timeline.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class TimelineFragment : Fragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var ctx: Context
    private lateinit var viewModel: TimelineViewModel
    private lateinit var timelineAdapter: TimelineAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        ctx = requireContext()
        PhotoScreenComponent.get(ctx).inject(this)

        viewModel = viewModelFactory.create(TimelineViewModel::class.java)

        val binding = DataBindingUtil.inflate<ScreenTimelineBinding>(LayoutInflater.from(ctx), R.layout.screen_timeline, container, false)
        binding.model = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val lm = LinearLayoutManager(ctx)
        timelineAdapter = TimelineAdapter(ctx, ::onItemClick)
        recyclerTimeline.apply {
            layoutManager = lm
            this.adapter = timelineAdapter
            addItemDecoration(DividerItemDecoration(ctx, lm.orientation))
        }

        viewModel.liveData.observe(this, Observer {})
    }

    override fun onStart() {
        super.onStart()

        viewModel.loadData()
    }

    private fun onItemClick(position: Int, id: String) {
        startActivity(
            Intent(ctx, PhotoViewEditActivity::class.java).apply {
                putExtra(PhotoViewEditActivity.EXTRA_PHOTO_ID, id)
            }
        )
        //Snackbar.make(recyclerTimeline, "$position", Snackbar.LENGTH_SHORT).show()
    }
}

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
                txtPeriod.text = SimpleDateFormat("MMMM yyyy").format(photo.shotTimestamp)
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

@BindingAdapter("items")
fun setTimelineList(rv: RecyclerView, list: List<PhotoInfo>?) {
    Log.i(LOGCAT, "Updating Recycler View, size: ${list?.size}")
    list?.let { (rv.adapter as TimelineAdapter).update(it) }
}

@BindingAdapter("category")
fun setCategoryLabelColor(textView: TextView, categoryId: Int) {
    textView.setText(CategoryInfo.getTitleRes(categoryId) ?: R.string.label_category_error)
    val catColor = textView.context.resources.getColor(CategoryInfo.getColorRes(categoryId) ?: R.color.category_unknown)
    textView.setTextColor(catColor)
}

@BindingAdapter("timestampShort")
fun setTimestampShort(textView: TextView, timestamp: Date) {
    textView.text = DateFormat.getDateInstance(DateFormat.MEDIUM).format(timestamp) // TODO move to separate helper
}