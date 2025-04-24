package com.example.drawingapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drawingapp.model.Drawing
import com.example.drawingapp.network.DrawingApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SharedDrawingsViewModel : ViewModel() {
    private val _sharedDrawings = MutableStateFlow<List<Drawing>>(emptyList())
    val sharedDrawings: StateFlow<List<Drawing>> = _sharedDrawings

    fun loadSharedDrawings() {
        viewModelScope.launch {
            val result = DrawingApiService.getSharedDrawings()
            _sharedDrawings.value = result
        }
    }
}
