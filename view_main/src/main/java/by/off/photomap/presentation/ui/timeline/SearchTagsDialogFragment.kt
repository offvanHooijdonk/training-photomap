package by.off.photomap.presentation.ui.timeline

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.*
import by.off.photomap.presentation.ui.R
import kotlinx.android.synthetic.main.dialog_search_tags.*

class SearchTagsDialogFragment : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(android.support.v4.app.DialogFragment.STYLE_NO_FRAME, R.style.AppTheme_SearchDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_search_tags, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        overlayBack.setOnClickListener { dismiss() }
    }

    override fun onStart() {
        super.onStart()

        setupDialog()
    }

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
}