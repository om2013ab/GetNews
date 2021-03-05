package com.omarahmed.getnews.ui.explore

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.omarahmed.getnews.data.Repository
import com.omarahmed.getnews.models.NewsResponse
import com.omarahmed.getnews.util.NetworkResult
import kotlinx.coroutines.launch
import retrofit2.Response
import java.lang.Exception

class ExploreViewModel @ViewModelInject constructor(
        private val repository: Repository,
        application: Application
) : AndroidViewModel(application) {

    private val _exploreNewsResponse = MutableLiveData<NetworkResult<NewsResponse>>()
    val exploreNewsResponse: LiveData<NetworkResult<NewsResponse>> = _exploreNewsResponse

    fun getExploreNews(apiKey: String, category: String) = viewModelScope.launch {
        _exploreNewsResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.getExploreNews(apiKey, category)
                _exploreNewsResponse.value = handleExploreNewsResponse(response)

            } catch (e: Exception) {
                _exploreNewsResponse.value = NetworkResult.Error("No Internet connection")
            }
        }
    }

    private fun handleExploreNewsResponse(response: Response<NewsResponse>): NetworkResult<NewsResponse> {
        return when {
            response.code() == 500 -> NetworkResult.Error("Api error")
            response.body()!!.articles.isNullOrEmpty() -> NetworkResult.Error("Not found")
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