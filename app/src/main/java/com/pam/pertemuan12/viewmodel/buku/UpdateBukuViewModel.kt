package com.pam.pertemuan12.viewmodel.buku

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pam.pertemuan12.model.Buku
import com.pam.pertemuan12.repository.BukuRepository
import kotlinx.coroutines.launch

class UpdateBukuViewModel(private val bku: BukuRepository) : ViewModel() {
    var uiState by mutableStateOf(UpdateUiState())
        private set

    fun loadBuku(id_buku: String) {
        viewModelScope.launch {
            try {
                val buku = bku.getBukuById(id_buku)
                uiState = UpdateUiState(updateUiEvent = buku.toUpdateUiEvent())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateMahasiswaState(updateUiEvent: UpdateUiEvent) {
        uiState = UpdateUiState(updateUiEvent = updateUiEvent)
    }

    suspend fun updateBuku(id_buku: String) {
        viewModelScope.launch {
            try {
                bku.updateBuku(id_buku, uiState.updateUiEvent.toBuku())
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
    val id_buku: String = "",
    val judul: String = "",
    val penulis: String = "",
    val kategori: String = "",
    val status: String = ""
)

fun UpdateUiEvent.toBuku(): Buku = Buku(
    id_buku = id_buku,
    judul = judul,
    penulis = penulis,
    kategori = kategori,
    status = status
)

fun Buku.toUpdateUiEvent(): UpdateUiEvent = UpdateUiEvent(
    id_buku = id_buku,
    judul = judul,
    penulis = penulis,
    kategori = kategori,
    status = status
)
