package com.omarahmed.getnews.bindingadapters

import android.view.View
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.omarahmed.getnews.data.room.entities.SavedNewsEntity

class SavedFragmentBindingAdapter {
    companion object{

        @BindingAdapter("setVisibility")
        @JvmStatic
        fun setVisibility(view:View, database: List<SavedNewsEntity>?){
            when(view){
                is RecyclerView -> view.isInvisible = database.isNullOrEmpty()
                else -> view.isVisible = database.isNullOrEmpty()
            }

        }
    }
}