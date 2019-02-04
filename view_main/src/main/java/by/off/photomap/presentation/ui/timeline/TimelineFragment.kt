package by.off.photomap.presentation.ui.timeline

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import by.off.photomap.core.ui.BaseFragment
import by.off.photomap.core.ui.ctx
import by.off.photomap.core.ui.show
import by.off.photomap.core.utils.di.ViewModelFactory
import by.off.photomap.di.PhotoScreenComponent
import by.off.photomap.model.PhotoInfo
import by.off.photomap.presentation.ui.R
import by.off.photomap.presentation.ui.databinding.ItemTimelineBinding
import by.off.photomap.presentation.ui.databinding.ScreenTimelineBinding
import by.off.photomap.presentation.ui.photo.PhotoViewEditActivity
import by.off.photomap.presentation.viewmodel.timeline.TimelineViewModel
import kotlinx.android.synthetic.main.screen_timeline.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class TimelineFragment : BaseFragment() {
    @Inject
    override lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: TimelineViewModel
    private lateinit var timelineAdapter: TimelineAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        PhotoScreenComponent.get(ctx).inject(this)

        viewModel = getViewModel(TimelineViewModel::class.java)

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