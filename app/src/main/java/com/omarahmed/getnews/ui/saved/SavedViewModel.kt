package com.omarahmed.getnews.ui.saved

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.omarahmed.getnews.data.Repository
import com.omarahmed.getnews.data.room.entities.SavedNewsEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedViewModel @Inject constructor(
        private val repository: Repository
) : ViewModel() {

    val readSavedNews = repository.readSavedNews().asLiveData()
    fun insertSavedNews(savedNewsEntity: SavedNewsEntity) = viewModelScope.launch {
        repository.insertSavedNews(savedNewsEntity)
    }

    fun deleteSavedNews(savedNewsEntity: SavedNewsEntity) = viewModelScope.launch {
        repository.deleteSavedNews(savedNewsEntity)
    }
}