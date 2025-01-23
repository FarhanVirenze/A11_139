package com.pam.pertemuan12.viewmodel.anggota

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pam.pertemuan12.model.Anggota
import com.pam.pertemuan12.repository.AnggotaRepository
import kotlinx.coroutines.launch

class UpdateAnggotaViewModel(private val ago: AnggotaRepository) : ViewModel() {
    var uiState by mutableStateOf(UpdateUiState())
        private set

    fun loadAnggota(id_buku: String) {
        viewModelScope.launch {
            try {
                val anggota = ago.getAnggotaById(id_buku)
                uiState = UpdateUiState(updateUiEvent = anggota.toUpdateUiEvent())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateAnggotaState(updateUiEvent: UpdateUiEvent) {
        uiState = UpdateUiState(updateUiEvent = updateUiEvent)
    }

    suspend fun updateAnggota(id_anggota: String) {
        viewModelScope.launch {
            try {
                ago.updateAnggota(id_anggota, uiState.updateUiEvent.toAnggota())
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
    val id_anggota: String = "",
    val nama: String = "",
    val email: String = "",
    val nomor_telepon: String = ""
)

fun UpdateUiEvent.toAnggota(): Anggota = Anggota(
    id_anggota = id_anggota,
    nama = nama,
    email = email,
    nomor_telepon = nomor_telepon
)

fun Anggota.toUpdateUiEvent(): UpdateUiEvent = UpdateUiEvent(
    id_anggota = id_anggota,
    nama = nama,
    email = email,
    nomor_telepon = nomor_telepon
)