package com.pam.pertemuan12.viewmodel.Pengembalian

import android.util.Log
import com.pam.pertemuan12.model.Pengembalian
import com.pam.pertemuan12.repository.PengembalianRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.network.HttpException
import com.pam.pertemuan12.repository.PeminjamanRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException

sealed class DetailUiState {
    object Loading : DetailUiState()
    data class Success(val pengembalian: Pengembalian) : DetailUiState()
    object Error : DetailUiState()
}

class DetailPengembalianViewModel(
    private val repository: PengembalianRepository,
    private val pjm: PeminjamanRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState

    fun getPengembalianById(id_pengembalian: String) {
        viewModelScope.launch {
            _uiState.value = DetailUiState.Loading
            try {
                // Fetch the pengembalian by ID
                val pengembalian = repository.getPengembalianById(id_pengembalian)

                // Now fetch the anggota for the given peminjaman ID
                try {
                    Log.d(
                        "DetailPengembalianViewModel",
                        "Fetching Anggota for Peminjaman ID: ${pengembalian.id_peminjaman}"
                    )

                    val anggota = pjm.getPeminjamanById(pengembalian.id_peminjaman)
                    Log.d(
                        "DetailPengembalianViewModel",
                        "Successfully fetched: ${anggota.id_anggota}"
                    )

                    // Add the anggota name to the pengembalian
                    val updatedPengembalian = pengembalian.copy(nama = anggota.id_anggota)

                    _uiState.value = DetailUiState.Success(updatedPengembalian)
                } catch (e: Exception) {
                    Log.e("DetailPengembalianViewModel", "Error fetching Anggota: ${e.message}")
                    _uiState.value = DetailUiState.Error
                }
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

