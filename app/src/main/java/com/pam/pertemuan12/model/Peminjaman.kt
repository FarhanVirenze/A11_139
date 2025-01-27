package com.pam.pertemuan12.model

import kotlinx.serialization.Serializable

@Serializable
data class Peminjaman(
    val id_peminjaman: String,
    val id_buku: String,
    val id_anggota: String,
    val tanggal_peminjaman: String,
    val tanggal_pengembalian: String,
    val status: String? = null
)