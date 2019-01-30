package by.off.photomap.presentation.ui.timeline

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import by.off.photomap.core.ui.dto.CategoryInfo
import by.off.photomap.core.utils.di.ViewModelFactory
import by.off.photomap.di.PhotoScreenComponent
import by.off.photomap.model.PhotoInfo
import by.off.photomap.presentation.ui.R
import by.off.photomap.presentation.ui.photo.PhotoViewEditActivity
import by.off.photomap.presentation.viewmodel.photo.timeline.TimelineViewModel
import kotlinx.android.synthetic.main.item_timeline.view.*
import kotlinx.android.synthetic.main.screen_timeline.*
import java.util.zip.Inflater
import javax.inject.Inject

class TimelineFragment : Fragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var ctx: Context
    private lateinit var viewModel: TimelineViewModel
    private val photoList = mutableListOf<PhotoInfo>()
    private lateinit var adapter: TimelineAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        ctx = requireContext()
        PhotoScreenComponent.get(ctx).inject(this)

        viewModel = viewModelFactory.create(TimelineViewModel::class.java)

        return inflater.inflate(R.layout.screen_timeline, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerTimeline.layoutManager = LinearLayoutManager(ctx)
        adapter = TimelineAdapter(photoList) { onItemClick(it) }
        recyclerTimeline.adapter = adapter

        viewModel.liveData.observe(this, Observer { response ->
            val data = response?.data
            if (data != null) {
                photoList.add(data)
                adapter.notifyDataSetChanged()
            }
        })
        viewModel.getDate()
    }

    private fun onItemClick(position: Int) {
        startActivity(
            Intent(ctx, PhotoViewEditActivity::class.java).apply {
                putExtra(PhotoViewEditActivity.EXTRA_PHOTO_ID, photoList[position].id)
            }
        )
    }
}

class TimelineAdapter(private val photos: List<PhotoInfo>, val onClick: (position: Int) -> Unit) : RecyclerView.Adapter<TimelineAdapter.ViewHolder>() {
    override fun onCreateViewHolder(container: ViewGroup, type: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(container.context).inflate(R.layout.item_timeline, container, false))

    override fun getItemCount(): Int = photos.size


    override fun onBindViewHolder(vh: ViewHolder, position: Int) {
        val photo = photos[position]
        with(vh.itemView) {
            txtPhotoDescription.text = photo.description
            txtPhotoCategory.setText(CategoryInfo.getTitleRes(photo.category) ?: R.string.label_category_error)
            itemRoot.setOnClickListener {
                onClick(position)
            }
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}