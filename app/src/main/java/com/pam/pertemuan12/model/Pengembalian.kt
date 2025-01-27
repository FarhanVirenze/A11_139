package com.pam.pertemuan12.model

import kotlinx.serialization.Serializable

@Serializable
data class Pengembalian(
    val id_pengembalian: String,
    val id_peminjaman: String,
    val tanggal_dikembalikan: String,
    val nama: String? = null
)