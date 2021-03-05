package com.omarahmed.getnews.ui.explore

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.omarahmed.getnews.R
import com.omarahmed.getnews.databinding.ExploreHeaderItemsBinding
import com.omarahmed.getnews.databinding.ExploreHeaderLayoutBinding
import java.util.*
import kotlin.collections.ArrayList

class HeaderAdapter(val list: ArrayList<ExploreHeaderModel>,
                    val listener: HeaderAdapterInterface
) : BaseAdapter() {
    private var selectedPosition = 0

    override fun getCount() = list.size
    override fun getItem(position: Int) = list[position]
    override fun getItemId(position: Int) = position.toLong()

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val currentNews = list[position]
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = ExploreHeaderItemsBinding.inflate(layoutInflater)

        view.ivExploreItems.setImageResource(currentNews.image)
        view.tvExploreItems.text = (currentNews.title)

        if (selectedPosition == position) {
            view.tvExploreItems.setTextColor(ContextCompat.getColor(
                    view.tvExploreItems.context,
                    R.color.green
            ))
            view.ivExploreItems.setColorFilter(ContextCompat.getColor(
                    view.ivExploreItems.context,
                    R.color.green
            ))
            listener.getCategory(position)
        }
        view.root.setOnClickListener {
            if (selectedPosition == position) {
                selectedPosition = -1
                notifyDataSetChanged()
            }
            selectedPosition = position
            notifyDataSetChanged()
        }
        return view.root
    }

    interface HeaderAdapterInterface {
        fun getCategory(position: Int)
    }

}