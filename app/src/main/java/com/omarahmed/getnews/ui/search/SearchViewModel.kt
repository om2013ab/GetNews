package com.omarahmed.getnews.ui.search

import android.app.Application
import androidx.lifecycle.*
import com.omarahmed.getnews.R
import com.omarahmed.getnews.data.Repository
import com.omarahmed.getnews.models.NewsResponse
import com.omarahmed.getnews.shared.RemoteConnection
import com.omarahmed.getnews.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: Repository,
    private val application: Application
) : ViewModel() {
    private val internetConnection = RemoteConnection(application)

    private val _searchNewsResponse = MutableLiveData<NetworkResult<NewsResponse>>()
    val searchNewsResponse: LiveData<NetworkResult<NewsResponse>> = _searchNewsResponse

    fun getSearchNews(apiKey: String, query: String) = viewModelScope.launch {
        _searchNewsResponse.value = NetworkResult.Loading()
        if (internetConnection.hasInternetConnection()) {
            try {
                val response = repository.getSearchNews(apiKey, query)
                _searchNewsResponse.value = internetConnection.handleRemoteResponse(
                    response,
                    application.getString(R.string.search_desc)
                )

            } catch (e: Exception) {
                _searchNewsResponse.value = NetworkResult.Error("Something went wrong")
            }
        } else {
            _searchNewsResponse.value = NetworkResult.Error("No Internet connection")
        }
    }
}