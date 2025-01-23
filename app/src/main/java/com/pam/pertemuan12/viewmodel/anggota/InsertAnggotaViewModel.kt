package com.pam.pertemuan12.viewmodel.anggota

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pam.pertemuan12.model.Anggota
import com.pam.pertemuan12.repository.AnggotaRepository
import kotlinx.coroutines.launch

class InsertAnggotaViewModel(private val anggotaRepository: AnggotaRepository) : ViewModel() {
    var uiState by mutableStateOf(InsertUiState())
        private set

    fun updateInsertAgoState(insertUiEvent: InsertUiEvent) {
        uiState = InsertUiState(insertUiEvent = insertUiEvent)
    }

    fun isFormValid(): Boolean {
        return uiState.insertUiEvent.run {
            id_anggota.isNotBlank() &&
                    nama.isNotBlank() &&
                    email.isNotBlank() &&
                    nomor_telepon.isNotBlank()
        }
    }

    fun insertAgo() {
        viewModelScope.launch {
            try {
                anggotaRepository.insertAnggota(uiState.insertUiEvent.toAnggota())
            } catch (e: Exception) {
                e.printStackTrace()  // Log the error
            }
        }
    }
}

data class InsertUiState(
    val insertUiEvent: InsertUiEvent = InsertUiEvent()
)

data class InsertUiEvent(
    val id_anggota: String = "",
    val nama: String = "",
    val email: String = "",
    val nomor_telepon: String = ""
)

fun InsertUiEvent.toAnggota(): Anggota = Anggota(
    id_anggota = id_anggota,
    nama = nama,
    email = email,
    nomor_telepon = nomor_telepon
)

fun Anggota.toUiState(): InsertUiState = InsertUiState(
    insertUiEvent = InsertUiEvent(
        id_anggota = id_anggota,
        nama = nama,
        email = email,
        nomor_telepon = nomor_telepon
    )
)
