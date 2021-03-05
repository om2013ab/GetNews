package com.omarahmed.getnews.shared

import androidx.recyclerview.widget.DiffUtil

class DiffCallback : DiffUtil.ItemCallback<DataItems>() {
    override fun areItemsTheSame(oldItem: DataItems, newItem: DataItems): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: DataItems, newItem: DataItems): Boolean {
        return oldItem == newItem
    }
}
