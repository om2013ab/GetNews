package com.omarahmed.getnews.ui.home.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.omarahmed.getnews.databinding.ViewPagerLayoutBinding
import com.omarahmed.getnews.models.Article
import com.omarahmed.getnews.ui.home.HomeFragmentDirections

class ViewPagerAdapter(): RecyclerView.Adapter<ViewPagerAdapter.PagerViewHolder>() {
    object DiffCallback: DiffUtil.ItemCallback<Article>(){
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == oldItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    val forYouList = AsyncListDiffer(this, DiffCallback)

    inner class PagerViewHolder(private val binding: ViewPagerLayoutBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(article: Article){
            binding.article = article
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder {
        val view = ViewPagerLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PagerViewHolder(view)
    }

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
        val currentNews = forYouList.currentList[position]
        holder.bind(currentNews)
        val action = HomeFragmentDirections.actionHomeFragmentToDetailsFragment(currentNews)
        holder.itemView.setOnClickListener {
            it.findNavController().navigate(action)
        }
    }

    override fun getItemCount() = forYouList.currentList.size
}