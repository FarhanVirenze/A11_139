package com.pam.pertemuan12.viewmodel.Pengembalian

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.network.HttpException
import com.pam.pertemuan12.model.Pengembalian
import com.pam.pertemuan12.repository.AnggotaRepository
import com.pam.pertemuan12.repository.PeminjamanRepository
import com.pam.pertemuan12.repository.PengembalianRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import java.io.IOException

sealed class HomePengembalianUiState {
    data class Success(val pengembalian: List<Pengembalian>) : HomePengembalianUiState()
    object Error : HomePengembalianUiState()
    object Loading : HomePengembalianUiState()
}

class HomePengembalianViewModel(
    private val pgn: PengembalianRepository,
    private val pjm: PeminjamanRepository
) : ViewModel() {
    var pgnUiState: HomePengembalianUiState by mutableStateOf(HomePengembalianUiState.Loading)
        private set

    init {
        getPgn()
    }

    fun getPgn() {
        viewModelScope.launch {
            pgnUiState = HomePengembalianUiState.Loading
            pgnUiState = try {
                val pengembalianList = pgn.getPengembalian()

                val pengembalianWithNames = pengembalianList.map { pengembalian ->
                    try {
                        Log.d("HomePengembalianViewModel", "Fetching Anggota for Pengembalian ID: ${pengembalian.id_peminjaman}")

                        val anggota = pjm.getPeminjamanById(pengembalian.id_peminjaman)
                        Log.d("HomePengembalianViewModel", "Successfully fetched: ${anggota.id_anggota}")

                        pengembalian.copy(nama = anggota.id_anggota)
                    } catch (e: Exception) {
                        Log.e("HomePengembalianViewModel", "Error fetching Anggota: ${e.message}")
                        pengembalian.copy(nama = "Error") // Handle error by setting name to "Error"
                    }
                }

                HomePengembalianUiState.Success(pengembalianWithNames)
            } catch (e: Exception) {
                Log.e("HomePengembalianViewModel", "Error in fetching pengembalian: ${e.message}")
                HomePengembalianUiState.Error
            }
        }
    }

    fun deletePgn(id_pengembalian: String) {
        viewModelScope.launch {
            try {
                pgn.deletePengembalian(id_pengembalian)
                getPgn()
            }catch (e: IOException){
                HomePengembalianUiState.Error
            }catch (e: HttpException){
                HomePengembalianUiState.Error
            }
        }
    }
}