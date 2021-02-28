package com.omarahmed.getnews.ui.home

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.omarahmed.getnews.R
import com.omarahmed.getnews.data.Repository
import com.omarahmed.getnews.data.room.entities.ForYouNewsEntity
import com.omarahmed.getnews.data.room.entities.LatestNewsEntity
import com.omarahmed.getnews.models.NewsResponse
import com.omarahmed.getnews.util.NetworkResult
import kotlinx.coroutines.launch
import retrofit2.Response
import java.lang.Exception


class HomeViewModel @ViewModelInject constructor(
    private val repository: Repository,
    application: Application
) : AndroidViewModel(application) {

    /** ROOM DATABASE*/
    val readForYouNews = repository.readForYouNews().asLiveData()
    private fun insertForYouNews(forYouNewsEntity: ForYouNewsEntity) = viewModelScope.launch {
        repository.insertForYouNews(forYouNewsEntity)
    }

    val readLatestNews = repository.readLatestNews().asLiveData()
    private fun insertLatestNews(latestNewsEntity: LatestNewsEntity) = viewModelScope.launch {
        repository.insertLatestNews(latestNewsEntity)
    }

    /** API RESPONSE*/
    private val _forYouResponse: MutableLiveData<NetworkResult<NewsResponse>> = MutableLiveData()
    val forYouResponse: LiveData<NetworkResult<NewsResponse>> = _forYouResponse

    private val _latestNewsResponse: MutableLiveData<NetworkResult<NewsResponse>> =
        MutableLiveData()
    val latestNewsResponse: LiveData<NetworkResult<NewsResponse>> = _latestNewsResponse

    fun getForYouNews(apiKey: String) = viewModelScope.launch {
        _forYouResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.getForYouNewsFromRemote(apiKey)
                _forYouResponse.value = handleRemoteResponse(response)
                // cache locally
                val forYouNews = _forYouResponse.value?.data
                if (forYouNews != null) {
                    val forYouNewsEntity = ForYouNewsEntity(forYouNews)
                    insertForYouNews(forYouNewsEntity)
                }
            } catch (e: Exception) {
                _forYouResponse.value = NetworkResult.Error(e.message)
            }
        } else {
            _forYouResponse.value =
                NetworkResult.Error(getApplication<Application>().getString(R.string.connection_issue_message))
        }
    }

    fun getLatestNews(apiKey: String) = viewModelScope.launch {
        _latestNewsResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.getLatestNewsFromApi(apiKey)
                _latestNewsResponse.value = handleRemoteResponse(response)
                // cache locally
                val latestNews = _latestNewsResponse.value?.data
                if (latestNews != null) {
                    val latestNewsEntity = LatestNewsEntity(latestNews)
                    insertLatestNews(latestNewsEntity)
                }

            } catch (e: Exception) {
                _latestNewsResponse.value = NetworkResult.Error(e.message)
            }
        } else {
            _latestNewsResponse.value = NetworkResult.Error(getApplication<Application>().getString(R.string.connection_issue_message))
        }
    }

    private fun handleRemoteResponse(response: Response<NewsResponse>): NetworkResult<NewsResponse> {
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