package com.omarahmed.getnews.ui.saved

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.omarahmed.getnews.data.room.entities.SavedNewsEntity
import com.omarahmed.getnews.databinding.SavedNewsRowBinding
import com.omarahmed.getnews.ui.saved.SavedNewsAdapter.SavedNewsViewHolder

class SavedNewsAdapter(
        private val listener: OnClickListener
) : androidx.recyclerview.widget.ListAdapter<SavedNewsEntity, SavedNewsViewHolder>(DiffCallback) {
    object DiffCallback : DiffUtil.ItemCallback<SavedNewsEntity>() {
        override fun areItemsTheSame(oldItem: SavedNewsEntity, newItem: SavedNewsEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SavedNewsEntity, newItem: SavedNewsEntity): Boolean {
            return oldItem == newItem
        }
    }

    inner class SavedNewsViewHolder(val binding: SavedNewsRowBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(savedNewsEntity: SavedNewsEntity) {
            binding.savedNews = savedNewsEntity
            binding.executePendingBindings()
        }
        init {
            binding.ivSavedNewsUnsaved.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION){
                    listener.onUnsavedClick(getItem(adapterPosition))
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedNewsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = SavedNewsRowBinding.inflate(layoutInflater)
        return SavedNewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: SavedNewsViewHolder, position: Int) {
        val currentNews = getItem(position)
        holder.bind(currentNews)
    }

    interface OnClickListener{
        fun onUnsavedClick(savedNewsEntity: SavedNewsEntity)
    }
}