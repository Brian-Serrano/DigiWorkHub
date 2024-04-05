package com.serrano.dictproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serrano.dictproject.datastore.PreferencesRepository
import com.serrano.dictproject.room.Dao
import com.serrano.dictproject.utils.SharedViewModelState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
    private val dao: Dao
): ViewModel() {

    val preferences = preferencesRepository.getData()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    private val _sharedState = MutableStateFlow(SharedViewModelState())
    val sharedState: StateFlow<SharedViewModelState> = _sharedState.asStateFlow()

    fun updateSharedState(newState: SharedViewModelState) {
        _sharedState.value = newState
    }

    fun logout() {
        viewModelScope.launch {
            dao.logout()
            preferencesRepository.logout()
        }
    }
}