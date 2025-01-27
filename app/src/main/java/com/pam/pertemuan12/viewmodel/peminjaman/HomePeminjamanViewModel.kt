package com.pam.pertemuan12.viewmodel.peminjaman

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.network.HttpException
import com.pam.pertemuan12.model.Peminjaman
import com.pam.pertemuan12.repository.BukuRepository
import com.pam.pertemuan12.repository.PeminjamanRepository
import kotlinx.coroutines.launch
import java.io.IOException

sealed class HomePeminjamanUiState {
    data class Success(val peminjaman: List<Peminjaman>) : HomePeminjamanUiState()
    object Error : HomePeminjamanUiState()
    object Loading : HomePeminjamanUiState()
}

class HomePeminjamanViewModel(
    private val pjm: PeminjamanRepository,
    private val bku: BukuRepository
) : ViewModel() {
    var pjmUiState: HomePeminjamanUiState by mutableStateOf(HomePeminjamanUiState.Loading)
        private set

    init {
        getPjm()
    }

    fun getPjm() {
        viewModelScope.launch {
            pjmUiState = HomePeminjamanUiState.Loading
            try {
                val statusBku = pjm.getPeminjaman().map { peminjaman ->
                    val buku = bku.getBukuById(peminjaman.id_buku)
                    peminjaman.copy(
                        status = when (buku?.status) {
                            "Tidak Tersedia" -> "Aktif"    // Jika buku sedang dipinjam
                            "Tersedia" -> "Tidak Aktif"   // Jika buku tersedia
                            else -> "Aktif"              // Default status
                        }
                    )
                }
                pjmUiState = HomePeminjamanUiState.Success(statusBku)
            } catch (e: IOException) {
                pjmUiState = HomePeminjamanUiState.Error
            } catch (e: HttpException) {
                pjmUiState = HomePeminjamanUiState.Error
            }
        }
    }

    fun deletePjm(id_peminjaman: String) {
        viewModelScope.launch {
            try {
                pjm.deletePeminjaman(id_peminjaman)
                getPjm()
            }catch (e: IOException){
                HomePeminjamanUiState.Error
            }catch (e: HttpException){
                HomePeminjamanUiState.Error
            }
        }
    }
}