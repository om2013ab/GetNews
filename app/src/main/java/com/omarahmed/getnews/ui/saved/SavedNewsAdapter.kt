package com.omarahmed.getnews.ui.saved

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.omarahmed.getnews.data.room.entities.SavedNewsEntity
import com.omarahmed.getnews.databinding.SavedNewsRowBinding
import com.omarahmed.getnews.models.Article
import com.omarahmed.getnews.shared.SavedDiffCallback
import com.omarahmed.getnews.shared.ShareClickListener
import com.omarahmed.getnews.shared.UnsavedClickListener
import com.omarahmed.getnews.ui.saved.SavedNewsAdapter.SavedNewsViewHolder

class SavedNewsAdapter(
        private val shareListener: ShareClickListener,
        private val unsavedListener: UnsavedClickListener
) : androidx.recyclerview.widget.ListAdapter<SavedNewsEntity, SavedNewsViewHolder>(SavedDiffCallback) {

    inner class SavedNewsViewHolder(val binding: SavedNewsRowBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(savedNewsEntity: SavedNewsEntity) {
            binding.apply {
                savedNews = savedNewsEntity
                shareClickListener = shareListener
                unsavedClickListener = unsavedListener
                executePendingBindings()
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
        holder.itemView.setOnClickListener {
            val action = SavedFragmentDirections.actionSavedFragmentToDetailsFragment(currentNews.article)
            it.findNavController().navigate(action)
        }
    }
}