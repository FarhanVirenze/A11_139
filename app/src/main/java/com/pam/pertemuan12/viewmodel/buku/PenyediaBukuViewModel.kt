package com.pam.pertemuan12.viewmodel.buku

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pam.pertemuan12.BukuApplications

object PenyediaBukuViewModel {
    val Factory = viewModelFactory {
        initializer { HomeBukuViewModel(aplikasiBuku().container.bukuRepository) }
        initializer { InsertBukuViewModel(aplikasiBuku().container.bukuRepository) }
        initializer { DetailBukuViewModel(aplikasiBuku().container.bukuRepository) }
        initializer { UpdateBukuViewModel(aplikasiBuku().container.bukuRepository) }
    }
}

fun CreationExtras.aplikasiBuku(): BukuApplications =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as BukuApplications)
