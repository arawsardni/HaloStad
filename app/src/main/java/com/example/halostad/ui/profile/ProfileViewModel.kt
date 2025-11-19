package com.example.halostad.ui.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.halostad.AppModule
import com.example.halostad.utils.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class ProfileViewModel : ViewModel() {
    private val repository = AppModule.authRepository

    private val _updateState = MutableStateFlow<UiState<Boolean>?>(null)
    val updateState: StateFlow<UiState<Boolean>?> = _updateState

    // Parameter image diubah jadi String? (Base64)
    fun updateProfile(name: String, photoBase64: String?) {
        viewModelScope.launch {
            repository.updateProfile(name, photoBase64).collect { state ->
                _updateState.value = state
            }
        }
    }

    fun resetState() {
        _updateState.value = null
    }
}