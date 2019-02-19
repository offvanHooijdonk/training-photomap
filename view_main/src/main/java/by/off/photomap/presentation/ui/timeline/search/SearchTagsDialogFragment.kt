package by.off.photomap.presentation.ui.timeline.search

import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import android.view.inputmethod.InputMethodManager
import by.off.photomap.core.ui.ctx
import by.off.photomap.core.ui.hide
import by.off.photomap.core.ui.show
import by.off.photomap.core.utils.PrefHelper
import by.off.photomap.presentation.ui.R
import kotlinx.android.synthetic.main.dialog_search_tags.*

class SearchTagsDialogFragment : DialogFragment(), ViewTreeObserver.OnPreDrawListener {
    private val imm: InputMethodManager by lazy { ctx.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager }

    companion object {
        private const val ARG_ANIM_X = "anim_x"
        private const val ARG_ANIM_Y = "anim_y"

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
    private val adapter by lazy { SearchResultsAdapter(ctx, resultList) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(android.support.v4.app.DialogFragment.STYLE_NO_FRAME, R.style.AppTheme_SearchDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_search_tags, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupLayout()
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

    private fun startSearch() {

    }

    private fun searchFull() {
        PrefHelper.addSearchHistoryEntry(ctx, inputSearch.text.toString())
    }

    // region Layout
    private fun setupLayout() {
        overlayBack.setOnClickListener { closeDialog() }
        imgBack.setOnClickListener { closeDialog() }
        imgSearch.viewTreeObserver.addOnPreDrawListener(this)
        imgSearch.setOnClickListener { searchFull() }

        setupList()
        val historyEntries = PrefHelper.getSearchHistory(ctx)
        if (historyEntries.isEmpty()) {
            blockHint.show()
            listSearchResults.hide()
        } else {
            blockHint.hide()
            listSearchResults.show()
            displayHistoryItems()
        }

        dialog.setOnKeyListener { _, keyCode, event ->
            when {
                (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) -> {
                    closeDialog()
                    true
                }
                (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) -> {
                    searchFull()
                    true
                }
                else -> false
            }

        }
    }

    private fun setupList() {
        listSearchResults.layoutManager = LinearLayoutManager(ctx)
        listSearchResults.adapter = adapter
    }

    private fun displayHistoryItems() {
        val historyItems = PrefHelper.getSearchHistory(ctx).sorted().map { Result(historyItem = it) }.toList()
        resultList.apply { clear(); addAll(historyItems) }
        adapter.notifyDataSetChanged()
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
    // end region

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