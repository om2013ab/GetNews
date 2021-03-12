package com.omarahmed.getnews.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.omarahmed.getnews.data.Repository
import com.omarahmed.getnews.models.NewsResponse
import com.omarahmed.getnews.shared.RemoteConnection
import com.omarahmed.getnews.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject
@HiltViewModel
class ExploreViewModel @Inject constructor(
        private val repository: Repository,
        application: Application
) : ViewModel() {
    private val internetConnection = RemoteConnection(application)

    private val _exploreNewsResponse = MutableLiveData<NetworkResult<NewsResponse>>()
    val exploreNewsResponse: LiveData<NetworkResult<NewsResponse>> = _exploreNewsResponse

    fun getExploreNews(apiKey: String, category: String) = viewModelScope.launch {
        _exploreNewsResponse.value = NetworkResult.Loading()
        if (internetConnection.hasInternetConnection()) {
            try {
                val response = repository.getExploreNews(apiKey, category)
                _exploreNewsResponse.value = internetConnection.handleRemoteResponse(response,"Not found")

            } catch (e: Exception) {
                _exploreNewsResponse.value = NetworkResult.Error("Something went wrong")
            }
        } else {
            _exploreNewsResponse.value = NetworkResult.Error("No Internet connection")
        }
    }
}