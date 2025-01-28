package com.pam.pertemuan12.viewmodel.Pengembalian

import android.util.Log
import com.pam.pertemuan12.model.Pengembalian
import com.pam.pertemuan12.repository.PengembalianRepository
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pam.pertemuan12.repository.BukuRepository
import com.pam.pertemuan12.repository.PeminjamanRepository
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

class InsertPengembalianViewModel(
    private val pgn: PengembalianRepository,
    private val bku: BukuRepository,
    private val pjm: PeminjamanRepository
) : ViewModel() {
    var uiState by mutableStateOf(InsertUiState())
        private set

    fun updateInsertPgnState(insertUiEvent: InsertUiEvent) {
        uiState = InsertUiState(insertUiEvent = insertUiEvent)
    }

    fun isFormValid(): Boolean {
        return uiState.insertUiEvent.run {
            id_pengembalian.isNotBlank() &&
                    id_peminjaman.isNotBlank() &&
                    tanggal_dikembalikan.isNotBlank()
        }
    }

    fun insertPgn() {
        viewModelScope.launch {
            try {
                // Get Pengembalian data from UI state
                val pengembalian = uiState.insertUiEvent.toPgn()

                // Log the original data
                Log.d("InsertPengembalian", "Original Pengembalian: $pengembalian")

                // Fetch the related peminjaman data using the id_peminjaman
                val peminjaman = pjm.getPeminjamanById(pengembalian.id_peminjaman)

                if (peminjaman == null) {
                    throw Exception("Peminjaman with ID ${pengembalian.id_peminjaman} not found.")
                }

                // Check if bukuId is not null
                val bukuId = peminjaman.id_buku
                if (bukuId != null) {
                    // Fetch book information using the bukuId
                    val buku = bku.getBukuById(bukuId)
                    if (buku != null) {
                        // Update the status of the book to "Tersedia"
                        val updatedBuku = buku.copy(status = "Tersedia")
                        bku.updateBuku(bukuId, updatedBuku)
                    } else {
                        throw Exception("Book with ID $bukuId not found.")
                    }
                }

                // Ensure tanggal_dikembalikan has the updated value from UI state
                val updatedTanggalDikembalikan = pengembalian.tanggal_dikembalikan

                // Calculate denda (penalty)
                val denda = calculateDenda(peminjaman.tanggal_pengembalian, updatedTanggalDikembalikan)

                // Log denda calculation
                Log.d("InsertPengembalian", "Calculated Denda: $denda")

                // Update the Pengembalian fields (nama, tanggal_peminjaman, tanggal_pengembalian, denda)
                val updatedPengembalian = pengembalian.copy(
                    tanggal_dikembalikan = updatedTanggalDikembalikan,  // Use updated date
                    tanggal_peminjaman = peminjaman.tanggal_peminjaman,
                    tanggal_pengembalian = peminjaman.tanggal_pengembalian,
                    nama = peminjaman.id_anggota,
                    denda = denda ?: "0" // Ensure denda is correctly updated, use 0 if null
                )

                // Log updated pengembalian
                Log.d("InsertPengembalian", "Updated Pengembalian: $updatedPengembalian")

                // Update the pengembalian data with the new values
                pgn.updatePengembalian(pengembalian.id_pengembalian, updatedPengembalian)

            } catch (e: Exception) {
                Log.e("InsertPengembalian", "Error during insert: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    // Calculate denda (penalty) if the book is returned late
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
                    null // No penalty if returned on time
                }
            } else {
                null // Invalid date format
            }
        } catch (e: ParseException) {
            Log.e("calculateDenda", "Error parsing dates: ${e.message}")
            null
        }
    }

    // UI State handling
    data class InsertUiState(
        val insertUiEvent: InsertUiEvent = InsertUiEvent()
    )

    data class InsertUiEvent(
        val id_buku: String = "",
        val id_pengembalian: String = "",
        val id_peminjaman: String = "",
        val tanggal_dikembalikan: String = ""
    )

    // Extension function to convert InsertUiEvent to Pengembalian
    fun InsertUiEvent.toPgn(): Pengembalian = Pengembalian(
        id_pengembalian = id_pengembalian,
        id_peminjaman = id_peminjaman,
        tanggal_dikembalikan = tanggal_dikembalikan
    )

    // Convert Pengembalian to UI State
    fun Pengembalian.toUiStatePgn(): InsertUiState = InsertUiState(
        insertUiEvent = toInsertUiEvent()
    )

    fun Pengembalian.toInsertUiEvent(): InsertUiEvent = InsertUiEvent(
        id_buku = "",
        id_pengembalian = id_pengembalian,
        id_peminjaman = id_peminjaman,
        tanggal_dikembalikan = tanggal_dikembalikan
    )
}
