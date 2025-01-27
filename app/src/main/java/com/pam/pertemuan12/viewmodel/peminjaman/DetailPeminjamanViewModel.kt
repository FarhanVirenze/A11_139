package com.pam.pertemuan12.viewmodel.peminjaman

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.network.HttpException
import com.pam.pertemuan12.model.Peminjaman
import com.pam.pertemuan12.repository.BukuRepository
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

class DetailPeminjamanViewModel(
    private val repository: PeminjamanRepository,
    private val bku: BukuRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState

    fun getPeminjamanById(id_peminjaman: String) {
        viewModelScope.launch {
            _uiState.value = DetailUiState.Loading
            try {
                // Ambil data peminjaman berdasarkan ID
                val peminjaman = repository.getPeminjamanById(id_peminjaman)

                // Ambil data buku berdasarkan ID buku di peminjaman
                val buku = bku.getBukuById(peminjaman.id_buku)

                // Update status peminjaman berdasarkan status buku
                val updatedPeminjaman = peminjaman.copy(
                    status = when (buku?.status) {
                        "Tersedia" -> "Tidak Aktif"    // Jika buku tersedia
                        "Tidak Tersedia" -> "Aktif"   // Jika buku sedang dipinjam
                        else -> "Status Tidak Valid"   // Default jika status tidak valid
                    }
                )

                // Perbarui UI state dengan data peminjaman yang sudah diperbarui
                _uiState.value = DetailUiState.Success(updatedPeminjaman)
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
