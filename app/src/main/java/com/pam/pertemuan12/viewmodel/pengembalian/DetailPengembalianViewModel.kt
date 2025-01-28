package com.pam.pertemuan12.viewmodel.Pengembalian

import android.util.Log
import com.pam.pertemuan12.model.Pengembalian
import com.pam.pertemuan12.repository.PengembalianRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.network.HttpException
import com.pam.pertemuan12.repository.PeminjamanRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

sealed class DetailUiState {
    object Loading : DetailUiState()
    data class Success(val pengembalian: Pengembalian) : DetailUiState()
    object Error : DetailUiState()
}

class DetailPengembalianViewModel(
    private val repository: PengembalianRepository,
    private val pjm: PeminjamanRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState

    fun getPengembalianById(id_pengembalian: String) {
        viewModelScope.launch {
            _uiState.value = DetailUiState.Loading
            try {
                // Fetch the pengembalian by ID
                val pengembalian = repository.getPengembalianById(id_pengembalian)

                // Now fetch the peminjaman for the given peminjaman ID
                try {
                    Log.d(
                        "DetailPengembalianViewModel",
                        "Fetching Anggota for Peminjaman ID: ${pengembalian.id_peminjaman}"
                    )

                    // Fetch peminjaman details
                    val peminjaman = pjm.getPeminjamanById(pengembalian.id_peminjaman)
                    Log.d(
                        "DetailPengembalianViewModel",
                        "Successfully fetched: ${peminjaman.id_anggota}"
                    )

                    // Format dates
                    val formattedTanggalPeminjaman = formatDate(peminjaman.tanggal_peminjaman)
                    val formattedTanggalPengembalian = formatDate(peminjaman.tanggal_pengembalian)

                    // Calculate denda
                    val denda = calculateDenda(
                        formattedTanggalPengembalian,
                        pengembalian.tanggal_dikembalikan
                    )

                    // Update the pengembalian object with formatted dates and denda
                    val updatedPengembalian = pengembalian.copy(
                        nama = peminjaman.id_anggota,
                        tanggal_peminjaman = formattedTanggalPeminjaman,
                        tanggal_pengembalian = formattedTanggalPengembalian,
                        denda = denda
                    )

                    _uiState.value = DetailUiState.Success(updatedPengembalian)
                } catch (e: Exception) {
                    Log.e("DetailPengembalianViewModel", "Error fetching Peminjaman: ${e.message}")
                    _uiState.value = DetailUiState.Error
                }
            } catch (e: IOException) {
                e.printStackTrace()
                _uiState.value = DetailUiState.Error
            } catch (e: HttpException) {
                e.printStackTrace()
                _uiState.value = DetailUiState.Error
            }
        }
    }

    fun formatDate(date: String?): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) // API date format
            val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) // Desired format
            date?.let { outputFormat.format(inputFormat.parse(it)) } ?: "Unknown"
        } catch (e: ParseException) {
            Log.e("formatDate", "Error formatting date: ${e.message}")
            date ?: "Unknown"
        }
    }

    fun calculateDenda(tanggalPengembalian: String, tanggalDikembalikan: String): String? {
        val format =
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) // Use the dd/MM/yyyy format

        return try {
            val datePengembalian = format.parse(tanggalPengembalian)
            val dateDikembalikan = format.parse(tanggalDikembalikan)

            if (dateDikembalikan != null && datePengembalian != null) {
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
}




