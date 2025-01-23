package com.pam.pertemuan12.model

import kotlinx.serialization.Serializable

@Serializable
data class Anggota(
    val id_anggota: String,
    val nama: String,
    val email: String,
    val nomor_telepon: String
)