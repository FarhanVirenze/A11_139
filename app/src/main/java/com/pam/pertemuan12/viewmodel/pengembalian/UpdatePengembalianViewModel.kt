package com.pam.pertemuan12.viewmodel.Pengembalian

import com.pam.pertemuan12.model.Pengembalian
import com.pam.pertemuan12.repository.PengembalianRepository
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class UpdatePengembalianViewModel(private val pgn: PengembalianRepository) : ViewModel() {
    var uiState by mutableStateOf(UpdateUiState())
        private set

    fun loadPengembalian(id_pengembalian: String) {
        viewModelScope.launch {
            try {
                val pengembalian = pgn.getPengembalianById(id_pengembalian)
                uiState = UpdateUiState(updateUiEvent = pengembalian.toUpdateUiEvent())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun isFormValid(): Boolean {
        return uiState.updateUiEvent.run {
            id_pengembalian.isNotBlank() &&
                    id_peminjaman.isNotBlank() &&
                    tanggal_dikembalikan.isNotBlank()
        }
    }

    fun updatePengembalianState(updateUiEvent: UpdateUiEvent) {
        uiState = UpdateUiState(updateUiEvent = updateUiEvent)
    }

    suspend fun updatePengembalian(id_pengembalian: String) {
        viewModelScope.launch {
            try {
                pgn.updatePengembalian(id_pengembalian, uiState.updateUiEvent.toPengembalian())
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
    val id_pengembalian: String = "",
    val id_peminjaman: String = "",
    val tanggal_dikembalikan: String = ""
)

fun UpdateUiEvent.toPengembalian(): Pengembalian = Pengembalian(
    id_pengembalian = id_pengembalian,
    id_peminjaman = id_peminjaman,
    tanggal_dikembalikan = tanggal_dikembalikan
)

fun Pengembalian.toUpdateUiEvent(): UpdateUiEvent = UpdateUiEvent(
    id_pengembalian = id_pengembalian,
    id_peminjaman = id_peminjaman,
    tanggal_dikembalikan = tanggal_dikembalikan
)
