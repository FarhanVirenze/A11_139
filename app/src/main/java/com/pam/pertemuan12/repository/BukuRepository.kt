package com.pam.pertemuan12.repository

import com.pam.pertemuan12.model.Buku
import com.pam.pertemuan12.service_api.BukuService
import java.io.IOException

interface BukuRepository {
    suspend fun getBuku(): List<Buku>
    suspend fun insertBuku(buku: Buku)
    suspend fun updateBuku(id_buku: String, buku: Buku)
    suspend fun deleteBuku(id_buku: String)
    suspend fun getBukuById(id_buku: String): Buku
}

class NetworkBukuRepository(
    private val bukuAPIService: BukuService
) : BukuRepository {

    override suspend fun getBuku(): List<Buku> {
        try {
            return bukuAPIService.getBuku()
        } catch (e: IOException) {
            throw IOException("Failed to fetch buku list. Network error occurred.", e)
        }
    }

    override suspend fun getBukuById(id_buku: String): Buku {
        try {
            return bukuAPIService.getBukuById(id_buku)
        } catch (e: IOException) {
            throw IOException("Failed to fetch buku with id_buku: $id_buku. Network error occurred.", e)
        }
    }

    override suspend fun insertBuku(buku: Buku) {
        try {
            val response = bukuAPIService.insertBuku(buku)
            if (!response.isSuccessful) {
                throw IOException("Failed to insert buku. HTTP Status code: ${response.code()}")
            }
        } catch (e: IOException) {
            throw IOException("Failed to insert buku. Network error occurred.", e)
        }
    }

    override suspend fun updateBuku(id_buku: String, buku: Buku) {
        try {
            val response = bukuAPIService.updateBuku(id_buku, buku)
            if (!response.isSuccessful) {
                throw IOException("Failed to update buku with id_buku: $id_buku. HTTP Status code: ${response.code()}")
            }
        } catch (e: IOException) {
            throw IOException("Failed to update buku. Network error occurred.", e)
        }
    }

    override suspend fun deleteBuku(id_buku: String) {
        try {
            val response = bukuAPIService.deleteBuku(id_buku)
            if (!response.isSuccessful) {
                throw IOException("Failed to delete buku with id_buku: $id_buku. HTTP Status code: ${response.code()}")
            }
        } catch (e: IOException) {
            throw IOException("Failed to delete buku. Network error occurred.", e)
        }
    }
}