package com.example.exceedit.adapter

import android.transition.TransitionInflater
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.exceedit.R
import com.example.exceedit.databinding.HolderExpandableListBinding
import com.example.exceedit.executeAfter

class ExpandableListAdapter :
    ListAdapter<Int, ExpandableListAdapter.ViewHolder>(DiffCallBack) {
    private var expandedIds = mutableSetOf<Int>()

    inner class ViewHolder(private val binding: HolderExpandableListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            holder: ViewHolder,
            item: Int
        ) {

            binding.executePendingBindings()
            binding.expandMore.setOnClickListener {
                val parent = holder.itemView.parent as? ViewGroup ?: return@setOnClickListener
                val expanded = binding.isExpanded ?: false
                if (expanded) {
                    expandedIds.remove(item)
                } else {
                    expandedIds.add(item)
                }
                val transition = TransitionInflater.from(binding.textView5.context)
                    .inflateTransition(R.transition.item_toggle)
                TransitionManager.beginDelayedTransition(parent, transition)
                holder.binding.executeAfter {
                    isExpanded = !expanded
                }
            }

        }
    }

    companion object DiffCallBack : DiffUtil.ItemCallback<Int>() {
        override fun areItemsTheSame(
            oldItem: Int,
            newItem: Int

        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: Int,
            newItem: Int

        ): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ViewHolder {
        val binding = DataBindingUtil.inflate<HolderExpandableListBinding>(
            LayoutInflater.from(parent.context),
            R.layout.holder_expandable_list,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder, position: Int
    ) {
        holder.bind(holder, getItem(position))
    }
}