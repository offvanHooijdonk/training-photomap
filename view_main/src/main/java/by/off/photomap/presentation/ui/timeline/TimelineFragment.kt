package by.off.photomap.presentation.ui.timeline

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import by.off.photomap.core.ui.*
import by.off.photomap.core.utils.di.ViewModelFactory
import by.off.photomap.di.PhotoScreenComponent
import by.off.photomap.presentation.ui.R
import by.off.photomap.presentation.ui.databinding.ScreenTimelineBinding
import by.off.photomap.presentation.ui.photo.PhotoViewEditActivity
import by.off.photomap.presentation.ui.timeline.search.SearchTagsDialogFragment
import kotlinx.android.synthetic.main.screen_timeline.*
import javax.inject.Inject

class TimelineFragment : BaseFragment(), SearchTagsDialogFragment.OnTagPickedListener {
    @Inject
    override lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: TimelineViewModel
    private lateinit var timelineAdapter: TimelineAdapter

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
        timelineAdapter = TimelineAdapter(ctx, ::onItemClick)
        recyclerTimeline.apply {
            layoutManager = lm
            this.adapter = timelineAdapter
        }

        viewModel.liveData.observe({ this.lifecycle }, {})

        refreshLayout.setOnRefreshListener { viewModel.loadData() }
        refreshLayout.setupDefaults()
        chipTagFilter.setOnCloseIconClickListener { clearTagFilter() }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.timeline, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        super.onOptionsItemSelected(item)
        when (item?.itemId) {
            R.id.item_hash_tag_search -> {
                val itemView = requireActivity().findViewById<View>(R.id.item_hash_tag_search)
                SearchTagsDialogFragment.newInstance(itemView).show(childFragmentManager, "search_tags")
                return true
            }
        }
        return false
    }

    override fun onTagPicked(tagText: String) {
        viewModel.tagFilter = tagText
        viewModel.loadData()

        blockTagFilter.fadeIn(150) { }
    }

    private fun onItemClick(position: Int, id: String) {
        PhotoViewEditActivity.IntentBuilder(ctx)
            .withPhotoId(id)
            .start()
    }

    private fun clearTagFilter() {
        blockTagFilter.fadeAway(100) {
            viewModel.tagFilter = ""
            viewModel.loadData()
        }
    }
}