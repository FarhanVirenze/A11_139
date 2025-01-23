package com.pam.pertemuan12.viewmodel.anggota

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.network.HttpException
import com.pam.pertemuan12.model.Anggota
import com.pam.pertemuan12.repository.AnggotaRepository
import kotlinx.coroutines.launch
import java.io.IOException

sealed class HomeAnggotaUiState {
    data class Success(val anggota: List<Anggota>) : HomeAnggotaUiState()
    object Error : HomeAnggotaUiState()
    object Loading : HomeAnggotaUiState()
}

class HomeAnggotaViewModel(private val ago: AnggotaRepository) : ViewModel() {
    var agoUiState: HomeAnggotaUiState by mutableStateOf(HomeAnggotaUiState.Loading)
        private set

    init {
        getAgo()
    }

    fun getAgo() {
        viewModelScope.launch {
            agoUiState = HomeAnggotaUiState.Loading
            agoUiState = try {
                HomeAnggotaUiState.Success(ago.getAnggota())
            }catch (e: IOException) {
                HomeAnggotaUiState.Error
            }catch (e: HttpException) {
                HomeAnggotaUiState.Error
            }
        }
    }

    fun deleteAgo(id_anggota: String) {
        viewModelScope.launch {
            try {
                ago.deleteAnggota(id_anggota)
                getAgo()
            }catch (e: IOException){
                HomeAnggotaUiState.Error
            }catch (e: HttpException){
                HomeAnggotaUiState.Error
            }
        }
    }
}