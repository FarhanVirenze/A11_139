package com.pam.pertemuan12.viewmodel.buku

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.network.HttpException
import com.pam.pertemuan12.model.Buku
import com.pam.pertemuan12.repository.BukuRepository
import kotlinx.coroutines.launch
import java.io.IOException

sealed class HomeUiState {
    data class Success(val buku: List<Buku>) : HomeUiState()
    data class Error(val message: String) : HomeUiState() // Tambahkan pesan error
    object Loading : HomeUiState()
}

class HomeBukuViewModel(private val bku: BukuRepository) : ViewModel() {

    var bkuUiState: HomeUiState by mutableStateOf(HomeUiState.Loading)
        private set

    init {
        getBku()
    }

    // Fungsi untuk mendapatkan data buku
    fun getBku() {
        viewModelScope.launch {
            safeApiCall(
                call = {
                    val bukuList = bku.getBuku()
                    bkuUiState = HomeUiState.Success(bukuList)
                },
                onError = { errorMessage ->
                    bkuUiState = HomeUiState.Error(errorMessage)
                }
            )
        }
    }

    // Fungsi untuk menghapus buku berdasarkan ID
    fun deleteBku(id_buku: String) {
        viewModelScope.launch {
            safeApiCall(
                call = {
                    bku.deleteBuku(id_buku)
                    getBku() // Refresh data setelah penghapusan
                },
                onError = { errorMessage ->
                    bkuUiState = HomeUiState.Error(errorMessage)
                }
            )
        }
    }


    // Fungsi untuk menangani error secara konsisten
    private suspend fun safeApiCall(call: suspend () -> Unit, onError: (String) -> Unit) {
        try {
            call()
        } catch (e: IOException) {
            onError("Network error: ${e.message}")
        } catch (e: HttpException) {
            onError("Server error: ${e.message}")
        } catch (e: Exception) {
            onError("Unexpected error: ${e.message}")
        }
    }
}
