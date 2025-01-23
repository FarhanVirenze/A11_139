package com.pam.pertemuan12.viewmodel.Pengembalian

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.network.HttpException
import com.pam.pertemuan12.model.Pengembalian
import com.pam.pertemuan12.repository.PengembalianRepository
import kotlinx.coroutines.launch
import java.io.IOException

sealed class HomePengembalianUiState {
    data class Success(val pengembalian: List<Pengembalian>) : HomePengembalianUiState()
    object Error : HomePengembalianUiState()
    object Loading : HomePengembalianUiState()
}

class HomePengembalianViewModel(private val pgn: PengembalianRepository) : ViewModel() {
    var pgnUiState: HomePengembalianUiState by mutableStateOf(HomePengembalianUiState.Loading)
        private set

    init {
        getPgn()
    }

    fun getPgn() {
        viewModelScope.launch {
            pgnUiState = HomePengembalianUiState.Loading
            pgnUiState = try {
                HomePengembalianUiState.Success(pgn.getPengembalian())
            }catch (e: IOException) {
                HomePengembalianUiState.Error
            }catch (e: HttpException) {
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