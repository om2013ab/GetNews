package com.omarahmed.getnews.ui.search

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.omarahmed.getnews.R
import com.omarahmed.getnews.data.Repository
import com.omarahmed.getnews.models.NewsResponse
import com.omarahmed.getnews.util.NetworkResult
import kotlinx.coroutines.launch
import retrofit2.Response
import java.lang.Exception

class SearchViewModel @ViewModelInject constructor(
        val repository: Repository,
        application: Application
) : AndroidViewModel(application) {

    private val _searchNewsResponse = MutableLiveData<NetworkResult<NewsResponse>>()
    val searchNewsResponse: LiveData<NetworkResult<NewsResponse>> = _searchNewsResponse

    fun getSearchNews(apiKey: String, query: String) = viewModelScope.launch {
        _searchNewsResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.getSearchNews(apiKey, query)
                _searchNewsResponse.value = handleSearchNewsResponse(response)

            } catch (e: Exception) {
                _searchNewsResponse.value = NetworkResult.Error("Something went wrong")
            }
        } else {
            _searchNewsResponse.value = NetworkResult.Error("No Internet connection")
        }
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>): NetworkResult<NewsResponse> {
        return when {
            response.code() == 500 -> NetworkResult.Error("Api error")
            response.body()!!.articles.isNullOrEmpty() -> NetworkResult.Error(getApplication<Application>().getString(R.string.search_desc))
            response.isSuccessful -> NetworkResult.Success(response.body()!!)
            else -> NetworkResult.Error("Something went wrong")
        }

    }

    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<Application>()
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
}