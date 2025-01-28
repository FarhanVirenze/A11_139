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
            pgnUiState = try {
                val pengembalianList = pgn.getPengembalian()

                val pengembalianWithDetails = pengembalianList.map { pengembalian ->
                    try {
                        Log.d("HomePengembalianViewModel", "Fetching Anggota for Pengembalian ID: ${pengembalian.id_peminjaman}")

                        // Fetch peminjaman based on ID
                        val peminjaman = pjm.getPeminjamanById(pengembalian.id_peminjaman)
                        Log.d("HomePengembalianViewModel", "Successfully fetched: ${peminjaman.id_anggota}")

                        // Format tanggal_pengembalian from peminjaman to dd/MM/yyyy if needed
                        val formattedTanggalPengembalian = try {
                            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) // Format from API
                            val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) // Desired format
                            outputFormat.format(inputFormat.parse(peminjaman.tanggal_pengembalian))
                        } catch (e: ParseException) {
                            Log.e("getPgn", "Error formatting tanggal_pengembalian: ${e.message}")
                            peminjaman.tanggal_pengembalian // Use original format if failed
                        }

                        // Format tanggal_peminjaman from peminjaman to dd/MM/yyyy if needed
                        val formattedTanggalPeminjaman = try {
                            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) // Format from API
                            val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) // Desired format
                            outputFormat.format(inputFormat.parse(peminjaman.tanggal_peminjaman))
                        } catch (e: ParseException) {
                            Log.e("getPgn", "Error formatting tanggal_peminjaman: ${e.message}")
                            peminjaman.tanggal_peminjaman // Use original format if failed
                        }

                        // Calculate the penalty (denda) if tanggal_dikembalikan is after tanggal_pengembalian
                        val denda = calculateDenda(formattedTanggalPengembalian, pengembalian.tanggal_dikembalikan)

                        // Return pengembalian with nama, tanggal_peminjaman, tanggal_pengembalian, and denda
                        pengembalian.copy(
                            nama = peminjaman.id_anggota,
                            tanggal_peminjaman = formattedTanggalPeminjaman,
                            tanggal_pengembalian = formattedTanggalPengembalian,
                            denda = denda
                        )
                    } catch (e: Exception) {
                        Log.e("HomePengembalianViewModel", "Error fetching Anggota: ${e.message}")
                        pengembalian.copy(nama = "Error", denda = null, tanggal_peminjaman = "Error", tanggal_pengembalian = "Error")
                    }
                }

                HomePengembalianUiState.Success(pengembalianWithDetails)
            } catch (e: Exception) {
                Log.e("HomePengembalianViewModel", "Error in fetching pengembalian: ${e.message}")
                HomePengembalianUiState.Error
            }
        }
    }

    fun calculateDenda(tanggalPengembalian: String, tanggalDikembalikan: String): String? {
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) // Use the dd/MM/yyyy format

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