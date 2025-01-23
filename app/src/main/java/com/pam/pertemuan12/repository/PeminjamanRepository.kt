package com.pam.pertemuan12.repository

import com.pam.pertemuan12.model.Peminjaman
import com.pam.pertemuan12.service_api.PeminjamanService
import java.io.IOException

interface PeminjamanRepository {
    suspend fun getPeminjaman(): List<Peminjaman>
    suspend fun insertPeminjaman(peminjaman: Peminjaman)
    suspend fun updatePeminjaman(id_peminjaman: String, peminjaman: Peminjaman)
    suspend fun deletePeminjaman(id_peminjaman: String)
    suspend fun getPeminjamanById(id_peminjaman: String): Peminjaman
}

class NetworkPeminjamanRepository(
    private val peminjamanAPIService: PeminjamanService
) : PeminjamanRepository {

    override suspend fun getPeminjaman(): List<Peminjaman> {
        try {
            return peminjamanAPIService.getPeminjaman()
        } catch (e: IOException) {
            throw IOException("Failed to fetch peminjaman list. Network error occurred.", e)
        }
    }

    override suspend fun getPeminjamanById(id_peminjaman: String): Peminjaman {
        try {
            return peminjamanAPIService.getPeminjamanById(id_peminjaman)
        } catch (e: IOException) {
            throw IOException("Failed to fetch peminjaman with id_peminjaman: $id_peminjaman. Network error occurred.", e)
        }
    }

    override suspend fun insertPeminjaman(peminjaman: Peminjaman) {
        try {
            val response = peminjamanAPIService.insertPeminjaman(peminjaman)
            if (!response.isSuccessful) {
                throw IOException("Failed to insert peminjaman. HTTP Status code: ${response.code()}")
            }
        } catch (e: IOException) {
            throw IOException("Failed to insert peminjaman. Network error occurred.", e)
        }
    }

    override suspend fun updatePeminjaman(id_peminjaman: String, peminjaman: Peminjaman) {
        try {
            val response = peminjamanAPIService.updatePeminjaman(id_peminjaman, peminjaman)
            if (!response.isSuccessful) {
                throw IOException("Failed to update peminjaman with id_buku: $id_peminjaman. HTTP Status code: ${response.code()}")
            }
        } catch (e: IOException) {
            throw IOException("Failed to update peminjaman. Network error occurred.", e)
        }
    }

    override suspend fun deletePeminjaman(id_peminjaman: String) {
        try {
            val response = peminjamanAPIService.deletePeminjaman(id_peminjaman)
            if (!response.isSuccessful) {
                throw IOException("Failed to delete peminjaman with id_buku: $id_peminjaman. HTTP Status code: ${response.code()}")
            }
        } catch (e: IOException) {
            throw IOException("Failed to delete peminjaman. Network error occurred.", e)
        }
    }
}