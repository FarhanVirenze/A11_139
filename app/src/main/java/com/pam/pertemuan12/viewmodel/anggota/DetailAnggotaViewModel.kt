package com.pam.pertemuan12.viewmodel.anggota

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.network.HttpException
import com.pam.pertemuan12.model.Anggota
import com.pam.pertemuan12.repository.AnggotaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException

sealed class DetailAnggotaUiState {
    object Loading : DetailAnggotaUiState()
    data class Success(val anggota: Anggota) : DetailAnggotaUiState()
    object Error : DetailAnggotaUiState()
}

class DetailAnggotaViewModel(private val repository: AnggotaRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<DetailAnggotaUiState>(DetailAnggotaUiState.Loading)
    val uiState: StateFlow<DetailAnggotaUiState> = _uiState

    fun getAnggotaById(id_anggota: String) {
        viewModelScope.launch {
            _uiState.value = DetailAnggotaUiState.Loading
            try {
                val anggota = repository.getAnggotaById(id_anggota)
                _uiState.value = DetailAnggotaUiState.Success(anggota)
            } catch (e: IOException) {
                e.printStackTrace()
                _uiState.value = DetailAnggotaUiState.Error
            } catch (e: HttpException) {
                e.printStackTrace()
                _uiState.value = DetailAnggotaUiState.Error
            }
        }
    }
}
