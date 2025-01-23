package com.pam.pertemuan12.viewmodel.buku

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.network.HttpException
import com.pam.pertemuan12.model.Buku
import com.pam.pertemuan12.repository.BukuRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException

sealed class DetailUiState {
    object Loading : DetailUiState()
    data class Success(val buku: Buku) : DetailUiState()
    object Error : DetailUiState()
}

class DetailBukuViewModel(private val repository: BukuRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState

    fun getBukuById(id_buku: String) {
        viewModelScope.launch {
            _uiState.value = DetailUiState.Loading
            try {
                val buku = repository.getBukuById(id_buku)
                _uiState.value = DetailUiState.Success(buku)
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
