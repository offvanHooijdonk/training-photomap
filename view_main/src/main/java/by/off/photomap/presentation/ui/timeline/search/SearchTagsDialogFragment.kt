package by.off.photomap.presentation.ui.timeline.search

import android.arch.lifecycle.Observer
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.DialogFragment
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.InputMethodManager
import by.off.photomap.core.ui.*
import by.off.photomap.core.utils.di.ViewModelFactory
import by.off.photomap.di.PhotoScreenComponent
import by.off.photomap.model.TagInfo
import by.off.photomap.presentation.ui.R
import by.off.photomap.presentation.ui.databinding.DialogSearchTagsBinding
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.dialog_search_tags.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SearchTagsDialogFragment : DialogFragment(), ViewTreeObserver.OnPreDrawListener {
    private val imm: InputMethodManager by lazy { ctx.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager }

    companion object {
        private const val ARG_ANIM_X = "anim_x"
        private const val ARG_ANIM_Y = "anim_y"
        private const val THROTTLE_LIVE = 250L

        fun newInstance(viewAnimateOver: View?): SearchTagsDialogFragment {
            var (animX, animY) = if (viewAnimateOver != null) SearchRevealAnimator.getViewCenterLocation(viewAnimateOver) else 0 to 0
            animY -= if (viewAnimateOver != null) viewAnimateOver.height / 2 else 0
            return SearchTagsDialogFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_ANIM_X, animX)
                    putInt(ARG_ANIM_Y, animY)
                }
            }
        }
    }

    private lateinit var revealAnimator: SearchRevealAnimator
    private val resultList = mutableListOf<TagInfo>()
    private val adapter by lazy { SearchResultsAdapter(ctx, resultList, ::onInferHistory, ::onItemClick) }
    private val liveQuerySubject = PublishSubject.create<String>().apply { throttleLast(THROTTLE_LIVE, TimeUnit.MILLISECONDS) }
    //private val obsFullQuery = PublishSubject.create<String>()
    private var fullSearchGoing = false
    private val cd = CompositeDisposable()

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: SearchTagViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(android.support.v4.app.DialogFragment.STYLE_NO_FRAME, R.style.AppTheme_SearchDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        PhotoScreenComponent.get(ctx).inject(this)
        viewModel = getViewModel(viewModelFactory, SearchTagViewModel::class.java)
        return DataBindingUtil.inflate<DialogSearchTagsBinding>(inflater, R.layout.dialog_search_tags, container, false)
            .apply {
                model = viewModel
            }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.resetData()
        viewModel.searchLiveData.observe(this, Observer { list ->
            list?.let {
                onFullSearchResult(list)
            }
        })
        viewModel.historyLiveData.observe(this, Observer { list ->
            list?.let {
                onLiveSearchResult(list)
            }
        })
        setupLayout()

        liveQuerySubject.subscribe { text ->
            viewModel.searchHistory(text)
        }.also { cd.add(it) }
    }

    override fun onStart() {
        super.onStart()

        setupDialog()
    }

    /**
     * Hook to start animation
     */
    override fun onPreDraw(): Boolean {
        imgSearch.viewTreeObserver.removeOnPreDrawListener(this)
        animateDialogIn()
        return true
    }

    private fun onItemClick(i: Int) {
        val item = resultList.getOrNull(i)
        item?.let {
            val text = item.text
            when (item.type) {
                TagInfo.TYPE_HISTORY -> onHistoryPicked(text)
                TagInfo.TYPE_TAG -> onTagPicked(text)
            }
        }
    }

    private fun onInferHistory(text: String) {
        //viewModel.filterTags(text)
        inputSearch.text.clear()
        inputSearch.text.insert(0, text)
    }

    private fun initSearch() {
        searchLive()
    }

    private fun searchLive() {
        liveQuerySubject.onNext(inputSearch.text.toString().trim())
    }

    private fun onLiveSearchResult(list: List<TagInfo>) {
        adapter.searchText = inputSearch.text.toString()
        resultList.clear()
        resultList.addAll(list)

        blockEmptyResults.hide()
        blockHint.hide()
        if (resultList.isEmpty()) listSearchResults.hide() else if (!listSearchResults.isVisible()) listSearchResults.fadeIn()
        adapter.notifyDataSetChanged()
    }

    private fun onHistoryPicked(text: String) {
        fullSearchGoing = true
        listSearchResults.hide()
        inputSearch.text.apply {
            clear()
            insert(0, text)
        }
        searchFull()
    }

    private fun onTagPicked(text: String) {
        (parentFragment as? OnTagPickedListener)?.onTagPicked(text)

        closeDialog()
    }

    private fun searchFull() {
        val text = inputSearch.text.toString().trim()
        if (text.isNotEmpty()) {
            viewModel.filterTags(text)
        }
    }

    private fun onFullSearchResult(searchList: List<String>) {
        if (searchList.isNotEmpty()) {
            blockEmptyResults.hide()
            listSearchResults.show()
        } else {
            listSearchResults.hide()
            blockEmptyResults.show()
        }
        resultList.clear()
        resultList.addAll(searchList.map { TagInfo(it, TagInfo.TYPE_TAG) })
        adapter.notifyDataSetChanged()
        fullSearchGoing = false
    }

    // region Layout
    private fun setupLayout() {
        overlayBack.setOnClickListener { closeDialog() }
        imgBack.setOnClickListener { closeDialog() }
        imgSearch.viewTreeObserver.addOnPreDrawListener(this)
        imgSearch.setOnClickListener { searchFull() }

        setupList()

        dialog.setOnKeyListener { _, keyCode, event ->
            when {
                (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) -> {
                    closeDialog(); true
                }
                (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) -> {
                    searchFull();true
                }
                else -> false
            }
        }
        inputSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { // todo validate for spaces?
                if (!fullSearchGoing) {
                    searchLive()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun setupList() {
        listSearchResults.layoutManager = LinearLayoutManager(ctx)
        listSearchResults.adapter = adapter
    }

    private fun animateDialogIn() {
        val animX = arguments?.getInt(ARG_ANIM_X) ?: 0
        val animY = arguments?.getInt(ARG_ANIM_Y) ?: 0

        revealAnimator = SearchRevealAnimator(blockSearchBar, animX, animY, {
            initSearch()
            showKeyBoard(true)
        }) { performCloseActions() }
        revealAnimator.animate(true)
    }

    private fun closeDialog() {
        val fadeAwayDuration = 100L
        listSearchResults.takeIf { isVisible }?.fadeAway(fadeAwayDuration)
        blockHint.takeIf { isVisible }?.fadeAway(fadeAwayDuration)
        blockEmptyResults.takeIf { isVisible }?.fadeAway(fadeAwayDuration)
        Handler().postDelayed({
            revealAnimator.animate(false)
        }, fadeAwayDuration)
    }

    private fun performCloseActions() {
        showKeyBoard(false)
        if (this.isVisible) {
            dismiss()
        }
    }
    // endregion

    private fun setupDialog() {
        dialog.window?.apply {
            setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
            setGravity(Gravity.TOP)
            isCancelable = true
            dialog.setCancelable(true)
            dialog.setCanceledOnTouchOutside(true)
            setWindowAnimations(R.style.DialogEmptyAnimation)
        }
    }

    private fun showKeyBoard(isShow: Boolean) {
        if (isShow) {
            imm.showSoftInput(inputSearch, InputMethodManager.RESULT_SHOWN)
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
        } else {
            imm.hideSoftInputFromWindow(inputSearch.windowToken, 0)
        }
    }

    interface OnTagPickedListener {
        fun onTagPicked(tagText: String)
    }
}