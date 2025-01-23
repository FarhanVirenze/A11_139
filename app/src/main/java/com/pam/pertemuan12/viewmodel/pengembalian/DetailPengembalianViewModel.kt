package com.pam.pertemuan12.viewmodel.Pengembalian

import com.pam.pertemuan12.model.Pengembalian
import com.pam.pertemuan12.repository.PengembalianRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.network.HttpException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException

sealed class DetailUiState {
    object Loading : DetailUiState()
    data class Success(val pengembalian: Pengembalian) : DetailUiState()
    object Error : DetailUiState()
}

class DetailPengembalianViewModel(private val repository: PengembalianRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState

    fun getPengembalianById(id_pengembalian: String) {
        viewModelScope.launch {
            _uiState.value = DetailUiState.Loading
            try {
                val pengembalian = repository.getPengembalianById(id_pengembalian)
                _uiState.value = DetailUiState.Success(pengembalian)
            } catch (e: IOException) {
                e.printStackTrace()
                _uiState.value = DetailUiState.Error
            } catch (e: HttpException) {
                e.printStackTrace()
                _uiState.value = DetailUiState.Error
            }
        }
    }
}
