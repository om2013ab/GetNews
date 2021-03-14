package com.omarahmed.getnews.ui.explore

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.omarahmed.getnews.databinding.ExploreItemsBinding
import com.omarahmed.getnews.models.Article
import com.omarahmed.getnews.shared.ShareClickListener
import com.omarahmed.getnews.ui.explore.ExploreAdapter.ExploreNewsViewHolder

class ExploreAdapter(val clickListener: ShareClickListener) :
    ListAdapter<Article, ExploreNewsViewHolder>(DiffCallback) {

    object DiffCallback : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    inner class ExploreNewsViewHolder(val binding: ExploreItemsBinding) : ViewHolder(binding.root) {
        fun bindExplore(article: Article) {
            binding.article = article
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExploreNewsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val newsView = ExploreItemsBinding.inflate(layoutInflater, parent, false)
        return ExploreNewsViewHolder(newsView)
    }

    override fun onBindViewHolder(holder: ExploreNewsViewHolder, position: Int) {
        val currentNews = getItem(position)
        holder.bindExplore(currentNews)
        holder.itemView.setOnClickListener {
            val action =
                ExploreFragmentDirections.actionExploreFragmentToDetailsFragment(currentNews)
            it.findNavController().navigate(action)
        }
    }
}
