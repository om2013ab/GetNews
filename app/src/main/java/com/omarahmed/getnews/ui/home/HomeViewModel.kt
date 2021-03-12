package com.omarahmed.getnews.ui.home

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import com.omarahmed.getnews.R
import com.omarahmed.getnews.data.Repository
import com.omarahmed.getnews.data.room.entities.ForYouNewsEntity
import com.omarahmed.getnews.data.room.entities.LatestNewsEntity
import com.omarahmed.getnews.models.NewsResponse
import com.omarahmed.getnews.shared.RemoteConnection
import com.omarahmed.getnews.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: Repository,
    private val application: Application
) : ViewModel() {

    private val internetConnection = RemoteConnection(application)

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
        if (internetConnection.hasInternetConnection()) {
            try {
                val response = repository.getForYouNewsFromRemote(apiKey)
                _forYouResponse.value = internetConnection.handleRemoteResponse(response,"Not found")
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
                NetworkResult.Error(application.getString(R.string.connection_issue_message))
        }
    }

    fun getLatestNews(apiKey: String) = viewModelScope.launch {
        _latestNewsResponse.value = NetworkResult.Loading()
        if (internetConnection.hasInternetConnection()) {
            try {
                val response = repository.getLatestNewsFromApi(apiKey)
                _latestNewsResponse.value = internetConnection.handleRemoteResponse(response,"Not found")
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
            _latestNewsResponse.value = NetworkResult.Error(application.getString(R.string.connection_issue_message))
        }
    }
}