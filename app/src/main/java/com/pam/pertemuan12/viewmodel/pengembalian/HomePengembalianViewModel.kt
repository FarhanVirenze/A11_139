package com.pam.pertemuan12.viewmodel.Pengembalian

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.network.HttpException
import com.pam.pertemuan12.model.Pengembalian
import com.pam.pertemuan12.repository.PeminjamanRepository
import com.pam.pertemuan12.repository.PengembalianRepository
import kotlinx.coroutines.launch
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

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
            try {
                // Fetch the pengembalian list
                val pengembalianList = pgn.getPengembalian()

                // Fetch the peminjaman data
                val peminjamanList = pjm.getPeminjaman()

                // Map over pengembalian to get the corresponding peminjaman data and calculate denda
                val resultList = pengembalianList.map { pengembalian ->
                    val peminjaman = peminjamanList.find { it.id_peminjaman == pengembalian.id_peminjaman }
                    val nama = peminjaman?.id_anggota
                    val tanggalPeminjaman = peminjaman?.tanggal_peminjaman
                    val tanggalPengembalian = peminjaman?.tanggal_pengembalian

                    val denda = if (!tanggalPengembalian.isNullOrEmpty() && !pengembalian.tanggal_dikembalikan.isNullOrEmpty()) {
                        calculateDenda(tanggalPengembalian, pengembalian.tanggal_dikembalikan)
                    } else {
                        null
                    }

                    pengembalian.copy(
                        nama = nama,
                        tanggal_peminjaman = tanggalPeminjaman,
                        tanggal_pengembalian = tanggalPengembalian,
                        denda = denda
                    )
                }

                pgnUiState = HomePengembalianUiState.Success(resultList)
            } catch (e: IOException) {
                pgnUiState = HomePengembalianUiState.Error
            } catch (e: HttpException) {
                pgnUiState = HomePengembalianUiState.Error
            }
        }
    }

    fun calculateDenda(tanggalPengembalian: String, tanggalDikembalikan: String): String? {
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) // Ensure the correct format

        return try {
            val datePengembalian = format.parse(tanggalPengembalian)
            val dateDikembalikan = format.parse(tanggalDikembalikan)

            if (dateDikembalikan != null && datePengembalian != null) {
                // Ensure tanggal_dikembalikan is after tanggal_pengembalian
                if (dateDikembalikan.after(datePengembalian)) {
                    val diff = dateDikembalikan.time - datePengembalian.time
                    val daysLate = TimeUnit.MILLISECONDS.toDays(diff).toInt()
                    "Rp ${daysLate * 1000}" // Calculate penalty: Rp 1,000 per day
                } else {
                    null // No penalty
                }
            } else {
                null // Invalid date format
            }
        } catch (e: ParseException) {
            Log.e("calculateDenda", "Error parsing dates: ${e.message}")
            null
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