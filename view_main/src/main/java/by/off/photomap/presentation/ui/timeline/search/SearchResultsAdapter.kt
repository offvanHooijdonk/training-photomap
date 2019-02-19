package by.off.photomap.presentation.ui.timeline.search

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import by.off.photomap.presentation.ui.R
import kotlinx.android.synthetic.main.item_search_history.view.*
import kotlinx.android.synthetic.main.item_search_tag.view.*

class SearchResultsAdapter(private val ctx: Context, private val items: List<Result>) : RecyclerView.Adapter<SearchResultsAdapter.ViewHolder>() {
    companion object {
        private const val TYPE_TAG = 0
        private const val TYPE_HISTORY = 1
    }

    override fun onCreateViewHolder(container: ViewGroup, type: Int): ViewHolder =
        ViewHolder(
            type, LayoutInflater.from(ctx).inflate(
                if (type == TYPE_HISTORY) R.layout.item_search_history else R.layout.item_search_tag
                , container, false
            )
        )

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(vh: ViewHolder, position: Int) {
        val result = items[position]
        when (vh.type) {
            TYPE_HISTORY -> {
                vh.itemView.txtHistory.text = result.historyItem
            }
            TYPE_TAG -> {
                vh.itemView.chipTag.text = result.tag
            }
        }
    }

    override fun getItemViewType(position: Int): Int =
        when {
            items[position].tag != null -> TYPE_TAG
            items[position].historyItem != null -> TYPE_HISTORY
            else -> TYPE_TAG
        }

    class ViewHolder(val type: Int, view: View) : RecyclerView.ViewHolder(view)
}

data class Result(val historyItem: String? = null, val tag: String? = null) {
    init {
        if (historyItem == null && tag == null) throw IllegalStateException("Cannot both be null: historyItem and tag")
    }
}