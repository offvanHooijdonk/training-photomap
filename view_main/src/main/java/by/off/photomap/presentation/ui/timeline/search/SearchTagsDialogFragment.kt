package by.off.photomap.presentation.ui.timeline.search

import android.arch.lifecycle.Observer
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.InputMethodManager
import by.off.photomap.core.ui.BaseFragment
import by.off.photomap.core.ui.ctx
import by.off.photomap.core.ui.hide
import by.off.photomap.core.ui.show
import by.off.photomap.core.utils.PrefHelper
import by.off.photomap.core.utils.di.ViewModelFactory
import by.off.photomap.di.PhotoScreenComponent
import by.off.photomap.presentation.ui.R
import by.off.photomap.presentation.ui.databinding.DialogSearchTagsBinding
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
    private val resultList = mutableListOf<Result>()
    private val adapter by lazy { SearchResultsAdapter(ctx, resultList, ::onInferHistory, ::onItemClick) }
    private val liveQuerySubject = PublishSubject.create<String>().apply { throttleLast(THROTTLE_LIVE, TimeUnit.MILLISECONDS) }
    private val obsFullQuery = PublishSubject.create<String>()

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: SearchTagViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(android.support.v4.app.DialogFragment.STYLE_NO_FRAME, R.style.AppTheme_SearchDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        PhotoScreenComponent.get(ctx).inject(this)
        viewModel = BaseFragment.getViewModel(this, viewModelFactory, SearchTagViewModel::class.java)
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
                updateSearchList(list)
            }
            adapter.notifyDataSetChanged()
        })
        setupLayout()

        liveQuerySubject.subscribe { text ->
            adapter.searchText = text;
            adapter.notifyDataSetChanged()
        }
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

    }

    private fun onInferHistory(text: String) {
        //viewModel.filterTags(text)
        inputSearch.text.clear()
        inputSearch.text.insert(0, text)
    }

    private fun startSearch() {

    }

    private fun searchLive() {
        liveQuerySubject.onNext(inputSearch.text.toString().trim())
    }

    private fun searchFull() {
        val text = inputSearch.text.toString().trim()
        if (text.isNotEmpty()) PrefHelper.addSearchHistoryEntry(ctx, inputSearch.text.toString())

        viewModel.filterTags(text)
    }

    // region Layout
    private fun setupLayout() {
        overlayBack.setOnClickListener { closeDialog() }
        imgBack.setOnClickListener { closeDialog() }
        imgSearch.viewTreeObserver.addOnPreDrawListener(this)
        imgSearch.setOnClickListener { searchFull() }

        setupList()
        updateSearchList(emptyList())

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
            override fun afterTextChanged(s: Editable?) {
                searchLive()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun setupList() {
        listSearchResults.layoutManager = LinearLayoutManager(ctx)
        listSearchResults.adapter = adapter
    }

    private fun updateSearchList(searchList: List<String>) {
        if (searchList.isNotEmpty()) {
            resultList.clear()
            resultList.addAll(searchList.map { s -> Result(null, s) })
        } else {
            val historyEntries = PrefHelper.getSearchHistory(ctx)
            if (historyEntries.isEmpty()) {
                blockHint.show()
                listSearchResults.hide()
            } else {
                blockHint.hide()
                listSearchResults.show()

                resultList.apply {
                    clear()
                    addAll(historyEntries.map { Result(historyItem = it) })
                }
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun animateDialogIn() {
        val animX = arguments?.getInt(ARG_ANIM_X) ?: 0
        val animY = arguments?.getInt(ARG_ANIM_Y) ?: 0

        revealAnimator = SearchRevealAnimator(blockSearchContent, animX, animY, {
            startSearch()
            showKeyBoard(true)
        }) { performCloseActions() }
        revealAnimator.animate(true)
    }

    private fun closeDialog() {
        revealAnimator.animate(false)
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
}