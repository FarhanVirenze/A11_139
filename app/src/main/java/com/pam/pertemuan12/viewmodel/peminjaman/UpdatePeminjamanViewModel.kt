package com.pam.pertemuan12.viewmodel.peminjaman

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pam.pertemuan12.model.Peminjaman
import com.pam.pertemuan12.repository.PeminjamanRepository
import kotlinx.coroutines.launch

class UpdatePeminjamanViewModel(private val pjm: PeminjamanRepository) : ViewModel() {
    var uiState by mutableStateOf(UpdateUiState())
        private set

    fun loadPeminjaman(id_peminjaman: String) {
        viewModelScope.launch {
            try {
                val peminjaman = pjm.getPeminjamanById(id_peminjaman)
                uiState = UpdateUiState(updateUiEvent = peminjaman.toUpdateUiEvent())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun isFormValid(): Boolean {
        return uiState.updateUiEvent.run {
            id_peminjaman.isNotBlank() &&
                    id_buku.isNotBlank() &&
                    id_anggota.isNotBlank() &&
                    tanggal_peminjaman.isNotBlank() &&
                    tanggal_pengembalian.isNotBlank()
        }
    }

    fun updatePeminjamanState(updateUiEvent: UpdateUiEvent) {
        uiState = UpdateUiState(updateUiEvent = updateUiEvent)
    }

    suspend fun updatePeminjaman(id_peminjaman: String) {
        viewModelScope.launch {
            try {
                pjm.updatePeminjaman(id_peminjaman, uiState.updateUiEvent.toPeminjaman())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

data class UpdateUiState(
    val updateUiEvent: UpdateUiEvent = UpdateUiEvent()
)

data class UpdateUiEvent(
    val id_peminjaman: String ="",
    val id_buku: String ="",
    val id_anggota: String ="",
    val tanggal_peminjaman: String ="",
    val tanggal_pengembalian: String =""
)

fun UpdateUiEvent.toPeminjaman(): Peminjaman = Peminjaman(
    id_peminjaman = id_peminjaman,
    id_buku = id_buku,
    id_anggota = id_anggota,
    tanggal_peminjaman = tanggal_peminjaman,
    tanggal_pengembalian = tanggal_pengembalian
)

fun Peminjaman.toUpdateUiEvent(): UpdateUiEvent = UpdateUiEvent(
    id_peminjaman = id_peminjaman,
    id_buku = id_buku,
    id_anggota = id_anggota,
    tanggal_peminjaman = tanggal_peminjaman,
    tanggal_pengembalian = tanggal_pengembalian
)