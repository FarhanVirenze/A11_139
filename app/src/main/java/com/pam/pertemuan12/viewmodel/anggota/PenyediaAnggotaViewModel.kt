package com.pam.pertemuan12.viewmodel.anggota

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pam.pertemuan12.BukuApplications

object PenyediaAnggotaViewModel {
    val Factory = viewModelFactory {
        initializer { HomeAnggotaViewModel(aplikasiAnggota().container.anggotaRepository) }
        initializer { DetailAnggotaViewModel(aplikasiAnggota().container.anggotaRepository) }
        initializer { InsertAnggotaViewModel(aplikasiAnggota().container.anggotaRepository) }
        initializer { UpdateAnggotaViewModel(aplikasiAnggota().container.anggotaRepository) }
    }
}

fun CreationExtras.aplikasiAnggota(): BukuApplications =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as BukuApplications)
