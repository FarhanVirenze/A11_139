package com.pam.pertemuan12.viewmodel.peminjaman

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pam.pertemuan12.model.Peminjaman
import com.pam.pertemuan12.repository.BukuRepository
import com.pam.pertemuan12.repository.PeminjamanRepository
import com.pam.pertemuan12.repository.PengembalianRepository
import kotlinx.coroutines.launch

class InsertPeminjamanViewModel(
    private val pjm: PeminjamanRepository,
    private val bku: BukuRepository, // Repository untuk Buku
) : ViewModel() {

    var uiState by mutableStateOf(InsertUiState())
        private set

    // Fungsi untuk menambahkan peminjaman, memperbarui status buku, dan menghapus data pengembalian
    fun insertPjm() {
        viewModelScope.launch {
            try {
                // Konversi event UI menjadi model Peminjaman
                val peminjaman = uiState.insertUiEvent.toPjm()

                // Tambahkan data peminjaman ke repository
                pjm.insertPeminjaman(peminjaman)

                // Dapatkan data buku berdasarkan ID
                val buku = bku.getBukuById(peminjaman.id_buku)

                // Validasi apakah buku tersedia
                if (buku != null) {
                    if (buku.status == "Tersedia") {
                        // Perbarui status buku menjadi "Tidak Tersedia"
                        val updatedBuku = buku.copy(status = "Tidak Tersedia")
                        bku.updateBuku(peminjaman.id_buku, updatedBuku)

                        // Update status peminjaman menjadi "Aktif"
                        val updatedPeminjaman = peminjaman.copy(status = "Aktif")
                        pjm.updatePeminjaman(peminjaman.id_peminjaman, updatedPeminjaman)

                    } else if (buku.status == "Tidak Tersedia") {
                        // Perbarui status buku menjadi "Tersedia"
                        val updatedBuku = buku.copy(status = "Tersedia")
                        bku.updateBuku(peminjaman.id_buku, updatedBuku)

                        // Update status peminjaman menjadi "Tidak Aktif"
                        val updatedPeminjaman = peminjaman.copy(status = "Tidak Aktif")
                        pjm.updatePeminjaman(peminjaman.id_peminjaman, updatedPeminjaman)

                    } else {
                        throw Exception("Buku memiliki status tidak valid.")
                    }
                } else {
                    throw Exception("Buku tidak ditemukan.")
                }

                // Reset UI state setelah operasi berhasil
                uiState = InsertUiState()
            } catch (e: Exception) {
                // Tangani kesalahan yang terjadi
                e.printStackTrace()
            }
        }
    }

    // Fungsi untuk memperbarui UI state berdasarkan input
    fun updateInsertPjmState(insertUiEvent: InsertUiEvent) {
        uiState = InsertUiState(insertUiEvent = insertUiEvent)
    }

    // Fungsi untuk memvalidasi form
    fun isFormValid(): Boolean {
        return uiState.insertUiEvent.run {
            id_peminjaman.isNotBlank() &&
                    id_buku.isNotBlank() &&
                    id_anggota.isNotBlank() &&
                    tanggal_peminjaman.isNotBlank() &&
                    tanggal_pengembalian.isNotBlank()
        }
    }

    // Data class untuk UI state
    data class InsertUiState(
        val insertUiEvent: InsertUiEvent = InsertUiEvent()
    )

    // Data class untuk input event
    data class InsertUiEvent(
        val id_peminjaman: String = "",
        val id_buku: String = "",
        val id_anggota: String = "",
        val tanggal_peminjaman: String = "",
        val tanggal_pengembalian: String = ""
    )

    // Ekstensi untuk konversi ke model Peminjaman
    fun InsertUiEvent.toPjm(): Peminjaman = Peminjaman(
        id_peminjaman = id_peminjaman,
        id_buku = id_buku,
        id_anggota = id_anggota,
        tanggal_peminjaman = tanggal_peminjaman,
        tanggal_pengembalian = tanggal_pengembalian
    )
}

