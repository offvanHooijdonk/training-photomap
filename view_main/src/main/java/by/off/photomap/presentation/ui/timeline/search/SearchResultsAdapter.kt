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
import kotlinx.android.synthetic.main.item_search_history.view.*

class SearchResultsAdapter(
    private val ctx: Context,
    private val items: List<Result>,
    private val onInfer: (String) -> Unit,
    private val onClick: (Int) -> Unit
) : RecyclerView.Adapter<SearchResultsAdapter.ViewHolder>() {
    companion object {
        private const val TYPE_TAG = 0
        private const val TYPE_HISTORY = 1
    }

    var searchText: String = ""

    override fun onCreateViewHolder(container: ViewGroup, type: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<ViewDataBinding>(
            LayoutInflater.from(ctx),
            if (type == TYPE_HISTORY) R.layout.item_search_history else R.layout.item_search_tag,
            container,
            false
        )

        return ViewHolder(binding, ResultItemViewModel(), type, binding.root)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(vh: ViewHolder, position: Int) {
        val result = items[position]
        val value: String
        if (getItemViewType(position) == TYPE_TAG) {
            value = result.tag!!
        } else {
            value = result.historyItem!!
            vh.itemView.imgInferHistory.setOnClickListener { onInfer(value) }
        }
        vh.bind(value, searchText)
    }

    override fun getItemViewType(position: Int): Int =
        when {
            items[position].tag != null -> TYPE_TAG
            items[position].historyItem != null -> TYPE_HISTORY
            else -> TYPE_TAG
        }

    class ViewHolder(val binding: ViewDataBinding, private val viewModel: ResultItemViewModel, val type: Int, view: View) : RecyclerView.ViewHolder(view) {
        fun bind(value: String, searchText: String) {
            binding.invalidateAll()

            viewModel.valueText.set(value)
            viewModel.searchText.set(searchText)

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