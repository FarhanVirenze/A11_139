package com.pam.pertemuan12.viewmodel.peminjaman

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.network.HttpException
import com.pam.pertemuan12.model.Peminjaman
import com.pam.pertemuan12.repository.PeminjamanRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException

sealed class DetailUiState {
    object Loading : DetailUiState()
    data class Success(val peminjaman: Peminjaman) : DetailUiState()
    object Error : DetailUiState()
}

class DetailPeminjamanViewModel(private val repository: PeminjamanRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState

    fun getPeminjamanById(id_peminjaman: String) {
        viewModelScope.launch {
            _uiState.value = DetailUiState.Loading
            try {
                val peminjaman = repository.getPeminjamanById(id_peminjaman)
                _uiState.value = DetailUiState.Success(peminjaman)
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
