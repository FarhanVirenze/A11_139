package com.pam.pertemuan12.viewmodel.peminjaman

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pam.pertemuan12.BukuApplications

object PenyediaPeminjamanViewModel {
    val Factory = viewModelFactory {
        initializer { HomePeminjamanViewModel(aplikasiPeminjaman().container.peminjamanRepository) }
        initializer { DetailPeminjamanViewModel(aplikasiPeminjaman().container.peminjamanRepository) }
        initializer {
            InsertPeminjamanViewModel(
                pjm = aplikasiPeminjaman().container.peminjamanRepository,
                bku = aplikasiPeminjaman().container.bukuRepository,
                pgn = aplikasiPeminjaman().container.pengembalianRepository
            )
        }
        initializer { UpdatePeminjamanViewModel(aplikasiPeminjaman().container.peminjamanRepository) }
    }
}

fun CreationExtras.aplikasiPeminjaman(): BukuApplications =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as BukuApplications)
