package com.pam.pertemuan12.viewmodel.Pengembalian

import com.pam.pertemuan12.model.Pengembalian
import com.pam.pertemuan12.repository.PengembalianRepository
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pam.pertemuan12.model.Peminjaman
import com.pam.pertemuan12.repository.BukuRepository
import com.pam.pertemuan12.repository.PeminjamanRepository
import kotlinx.coroutines.launch

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
                val pengembalian = uiState.insertUiEvent.toPgn()
                pgn.insertPengembalian(pengembalian) // Insert pengembalian data

                // Ambil informasi buku berdasarkan ID buku yang dipinjam
                val peminjaman =
                    pjm.getPeminjamanById(pengembalian.id_peminjaman) // Ensure to fetch the peminjaman by its ID
                val bukuId = peminjaman?.id_buku // Fetch the ID of the book borrowed

                if (bukuId != null) {
                    // Ambil informasi buku berdasarkan ID buku
                    val buku = bku.getBukuById(bukuId)
                    if (buku != null) {
                        // Update status buku menjadi "Tersedia"
                        val updatedBuku = buku.copy(status = "Tersedia")
                        bku.updateBuku(bukuId, updatedBuku)

                        // Hapus data peminjaman setelah pengembalian buku
                        pjm.deletePeminjaman(pengembalian.id_peminjaman) // Delete the peminjaman data using the correct peminjaman ID
                    } else {
                        throw Exception("Buku dengan ID $bukuId tidak ditemukan.")
                    }
                } else {
                    throw Exception("Peminjaman dengan ID ${pengembalian.id_peminjaman} tidak ditemukan.")
                }

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
    val id_buku: String = "",
    val id_pengembalian: String = "",
    val id_peminjaman: String = "",
    val tanggal_dikembalikan: String = ""
)

fun InsertUiEvent.toPgn(): Pengembalian = Pengembalian(
    id_pengembalian = id_pengembalian,
    id_peminjaman = id_peminjaman,
    tanggal_dikembalikan = tanggal_dikembalikan
)

fun Pengembalian.toUiStatePgn(): InsertUiState = InsertUiState(
    insertUiEvent = toInsertUiEvent()
)

fun Pengembalian.toInsertUiEvent(): InsertUiEvent = InsertUiEvent(
    id_buku = "",
    id_pengembalian = id_pengembalian,
    id_peminjaman = id_peminjaman,
    tanggal_dikembalikan = tanggal_dikembalikan
)
