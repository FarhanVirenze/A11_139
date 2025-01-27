package com.pam.pertemuan12.viewmodel.Pengembalian

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pam.pertemuan12.BukuApplications

object PenyediaPengembalianViewModel {
    val Factory = viewModelFactory {
        initializer { HomePengembalianViewModel(
            aplikasiPengembalian().container.pengembalianRepository,
            aplikasiPengembalian().container.peminjamanRepository,

            ) }
        initializer {
            InsertPengembalianViewModel(
                pgn = aplikasiPengembalian().container.pengembalianRepository,
                bku = aplikasiPengembalian().container.bukuRepository,
                pjm = aplikasiPengembalian().container.peminjamanRepository
                // Inject BukuRepository
            )
        }
        initializer { DetailPengembalianViewModel(
            aplikasiPengembalian().container.pengembalianRepository,
            aplikasiPengembalian().container.peminjamanRepository,
            ) }
        initializer { UpdatePengembalianViewModel(aplikasiPengembalian().container.pengembalianRepository) }
    }
}

fun CreationExtras.aplikasiPengembalian(): BukuApplications =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as BukuApplications)
