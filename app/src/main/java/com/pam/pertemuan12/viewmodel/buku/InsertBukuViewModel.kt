package com.pam.pertemuan12.viewmodel.buku

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pam.pertemuan12.model.Buku
import com.pam.pertemuan12.repository.BukuRepository
import kotlinx.coroutines.launch

class InsertBukuViewModel(private val bku: BukuRepository) : ViewModel() {
    var uiState by mutableStateOf(InsertUiState())
        private set

    fun updateInsertMhsState(insertUiEvent: InsertUiEvent) {
        uiState = InsertUiState(insertUiEvent = insertUiEvent)
    }

    fun isFormValid(): Boolean {
        return uiState.insertUiEvent.run {
            id_buku.isNotBlank() &&
                    judul.isNotBlank() &&
                    penulis.isNotBlank() &&
                    kategori.isNotBlank() &&
                    status.isNotBlank()
        }
    }

    suspend fun insertBku() {
        viewModelScope.launch {
            try {
                bku.insertBuku(uiState.insertUiEvent.toBku())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

data class InsertUiState(
    val insertUiEvent: InsertUiEvent = InsertUiEvent()
)

data class InsertUiEvent(
    val id_buku: String ="",
    val judul: String ="",
    val penulis: String ="",
    val kategori: String ="",
    val status: String =""
)

fun InsertUiEvent.toBku(): Buku = Buku(
    id_buku = id_buku,
    judul = judul,
    penulis = penulis,
    kategori = kategori,
    status = status
)

fun Buku.toUiStateBku(): InsertUiState = InsertUiState(
    insertUiEvent = toInsertUiEvent()
)

fun Buku.toInsertUiEvent(): InsertUiEvent = InsertUiEvent(
    id_buku = id_buku,
    judul = judul,
    penulis = penulis,
    kategori = kategori,
    status = status
)