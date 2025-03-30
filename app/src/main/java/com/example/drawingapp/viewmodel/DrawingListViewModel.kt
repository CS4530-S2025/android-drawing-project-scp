package com.example.drawingapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.drawingapp.model.DrawingDatabase
import com.example.drawingapp.model.DrawingEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel that loads the list of saved drawings from the Room DB.
 */
class DrawingListViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = DrawingDatabase.getDatabase(application).drawingDao()

    private val _drawings = MutableStateFlow<List<DrawingEntity>>(emptyList())
    val drawings: StateFlow<List<DrawingEntity>> = _drawings

    init {
        refreshDrawings()
    }

    fun refreshDrawings() {
        viewModelScope.launch {
            _drawings.value = dao.getAll()
        }
    }
}

