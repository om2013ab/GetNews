package com.omarahmed.getnews.shared

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.omarahmed.getnews.models.NewsResponse
import com.omarahmed.getnews.util.NetworkResult
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.Response
import javax.inject.Inject

class RemoteConnection @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun hasInternetConnection(): Boolean {
        val connectivityManager = context
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

    fun handleRemoteResponse(
        response: Response<NewsResponse>,
        emptyMessage: String
    ): NetworkResult<NewsResponse> {
        return when {
            response.code() == 500 -> NetworkResult.Error("Api error")
            response.body()!!.articles.isNullOrEmpty() -> NetworkResult.Error(emptyMessage)
            response.isSuccessful -> NetworkResult.Success(response.body()!!)
            else -> NetworkResult.Error("Something went wrong")
        }
    }
}