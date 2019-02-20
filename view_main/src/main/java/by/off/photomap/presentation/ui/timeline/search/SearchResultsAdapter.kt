package by.off.photomap.presentation.ui.timeline.search

import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import by.off.photomap.presentation.ui.R
import by.off.photomap.presentation.ui.databinding.ItemSearchHistoryBinding
import by.off.photomap.presentation.ui.databinding.ItemSearchTagBinding

class SearchResultsAdapter(private val ctx: Context, private val items: List<Result>) : RecyclerView.Adapter<SearchResultsAdapter.ViewHolder>() {
    companion object {
        private const val TYPE_TAG = 0
        private const val TYPE_HISTORY = 1
    }

    override fun onCreateViewHolder(container: ViewGroup, type: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<ViewDataBinding>(
            LayoutInflater.from(ctx),
            if (type == TYPE_HISTORY) R.layout.item_search_history else R.layout.item_search_tag,
            container, false
        )

        return ViewHolder(binding, ResultItemViewModel(), type, binding.root)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(vh: ViewHolder, position: Int) {
        val result = items[position]
        val value = if (getItemViewType(position) == TYPE_TAG) result.tag!! else result.historyItem!!
        vh.bind(value)
    }

    override fun getItemViewType(position: Int): Int =
        when {
            items[position].tag != null -> TYPE_TAG
            items[position].historyItem != null -> TYPE_HISTORY
            else -> TYPE_TAG
        }

    class ViewHolder(val binding: ViewDataBinding, private val viewModel: ResultItemViewModel, val type: Int, view: View) : RecyclerView.ViewHolder(view) {
        fun bind(value: String) {
            binding.invalidateAll()

            if (type == TYPE_TAG) viewModel.tagText.set(value) else viewModel.historyItemText.set(value)
            when (binding) {
                is ItemSearchTagBinding -> binding.model = viewModel
                is ItemSearchHistoryBinding -> binding.model = viewModel
            }
        }
    }
}


data class Result(val historyItem: String? = null, val tag: String? = null) {
    init {
        if (historyItem == null && tag == null) throw IllegalStateException("Cannot both be null: historyItem and tag")
    }
}