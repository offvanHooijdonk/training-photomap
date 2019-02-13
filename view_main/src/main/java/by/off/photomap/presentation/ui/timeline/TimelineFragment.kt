package by.off.photomap.presentation.ui.timeline

import android.arch.lifecycle.Observer
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import by.off.photomap.core.ui.BaseFragment
import by.off.photomap.core.ui.CallbackHolder
import by.off.photomap.core.ui.ctx
import by.off.photomap.core.ui.setupDefaults
import by.off.photomap.core.utils.di.ViewModelFactory
import by.off.photomap.di.PhotoScreenComponent
import by.off.photomap.presentation.ui.R
import by.off.photomap.presentation.ui.databinding.ScreenTimelineBinding
import by.off.photomap.presentation.ui.photo.PhotoViewEditActivity
import kotlinx.android.synthetic.main.screen_timeline.*
import javax.inject.Inject

class TimelineFragment : BaseFragment() {
    @Inject
    override lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: TimelineViewModel
    private lateinit var timelineAdapter: TimelineAdapter
    private val callbacks = mutableMapOf<String, CallbackHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        PhotoScreenComponent.get(ctx).inject(this)

        viewModel = getViewModel(TimelineViewModel::class.java)

        val binding = DataBindingUtil.inflate<ScreenTimelineBinding>(LayoutInflater.from(ctx), R.layout.screen_timeline, container, false)
        binding.model = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //setHasOptionsMenu(true)
        val lm = LinearLayoutManager(ctx)
        timelineAdapter = TimelineAdapter(ctx, ::onItemClick, ::requestThumbnail)
        recyclerTimeline.apply {
            layoutManager = lm
            this.adapter = timelineAdapter
        }

        viewModel.liveData.observe({ this.lifecycle }, {})
        viewModel.thumbnailLiveData.observe(this, Observer { data ->
            if (data != null) {
                val id = data.first
                val callback = callbacks[id]
                callback?.let {
                    callbacks.remove(id)
                    callback.callback(id, data.second)
                }
            }
        })
        refreshLayout.setOnRefreshListener { viewModel.loadData() }
        refreshLayout.setupDefaults()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.timeline, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        super.onOptionsItemSelected(item)
        when (item?.itemId) {
            R.id.item_hash_tag_search -> {
                SearchTagsDialogFragment().show(childFragmentManager, "search_tags")
                return true
            }
        }
        return false
    }

    private fun requestThumbnail(photoId: String, callback: (photoId: String, filePath: String?) -> Unit) {
        callbacks[photoId] = CallbackHolder(photoId, callback)
        viewModel.requestThumbnail(photoId)
    }

    private fun onItemClick(position: Int, id: String) {
        startActivity(
            Intent(ctx, PhotoViewEditActivity::class.java).apply {
                putExtra(PhotoViewEditActivity.EXTRA_PHOTO_ID, id)
            }
        )
    }
}