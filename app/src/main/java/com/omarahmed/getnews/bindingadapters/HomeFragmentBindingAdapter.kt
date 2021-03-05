package com.omarahmed.getnews.bindingadapters

import android.net.Network
import android.text.format.DateUtils
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.omarahmed.getnews.R
import com.omarahmed.getnews.data.room.entities.LatestNewsEntity
import com.omarahmed.getnews.models.NewsResponse
import com.omarahmed.getnews.util.NetworkResult
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class HomeFragmentBindingAdapter {
    companion object {
        @BindingAdapter("setImage")
        @JvmStatic
        fun loadImage(imageView: ImageView, imageUrl:String?){
            imageUrl?.let {
                imageView.load(it){
                    error(R.drawable.ic_error_placeholder)
                }
            }
        }

        @BindingAdapter("setTimeAgo")
        @JvmStatic
        fun getTimeAgo(textView: TextView, timeString: String?){
            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
            try {
                timeString?.let {
                    val time = format.parse(it)!!.time
                    val now = Calendar.getInstance().timeInMillis
                    val ago = DateUtils.getRelativeTimeSpanString(time,now,DateUtils.MINUTE_IN_MILLIS)
                    textView.text = ago.toString()
                }
            } catch (e:Exception){
                  throw e
            }
        }

        @BindingAdapter("readApiResponse", "readDatabase", requireAll = true)
        @JvmStatic
        fun handleReadDataErrors(
            view: View,
            newsResponse: NetworkResult<NewsResponse>?,
            database: List<LatestNewsEntity>?
        ){
            when(view){
                is ImageView -> {
                    view.isVisible = newsResponse is NetworkResult.Error && database.isNullOrEmpty()
                }
                is TextView -> {
                    view.isVisible = newsResponse is NetworkResult.Error && database.isNullOrEmpty()
                    view.text = newsResponse?.message.toString()
                }
                else -> view.isInvisible = newsResponse is NetworkResult.Error && database.isNullOrEmpty()
//                is ConstraintLayout -> {
//                    headerBinding.isVisible = newsResponse is NetworkResult.Success || database?.isNotEmpty() == true
//                }
            }
        }
    }
}