package com.pam.pertemuan12.repository

import com.pam.pertemuan12.model.Pengembalian
import com.pam.pertemuan12.service_api.BukuService
import com.pam.pertemuan12.service_api.PengembalianService
import java.io.IOException

interface PengembalianRepository {
    suspend fun getPengembalian(): List<Pengembalian>
    suspend fun insertPengembalian(pengembalian: Pengembalian)
    suspend fun updatePengembalian(id_pengembalian: String, pengembalian: Pengembalian)
    suspend fun deletePengembalian(id_pengembalian: String)
    suspend fun getPengembalianById(id_pengembalian: String): Pengembalian
}

class NetworkPengembalianRepository(
    private val pengembalianAPIService: PengembalianService
) : PengembalianRepository {

    override suspend fun getPengembalian(): List<Pengembalian> {
        try {
            return pengembalianAPIService.getPengembalian()
        } catch (e: IOException) {
            throw IOException("Failed to fetch pengembalian list. Network error occurred.", e)
        }
    }

    override suspend fun getPengembalianById(id_pengembalian : String): Pengembalian {
        try {
            return pengembalianAPIService.getPengembalianById(id_pengembalian)
        } catch (e: IOException) {
            throw IOException("Failed to fetch pengembalian with id_pengembalian: $id_pengembalian. Network error occurred.", e)
        }
    }

    override suspend fun insertPengembalian(pengembalian: Pengembalian) {
        try {
            val response = pengembalianAPIService.insertPengembalian(pengembalian)
            if (!response.isSuccessful) {
                throw IOException("Failed to insert pengembalian. HTTP Status code: ${response.code()}")
            }
        } catch (e: IOException) {
            throw IOException("Failed to insert pengembalian. Network error occurred.", e)
        }
    }

    override suspend fun updatePengembalian(id_pengembalian: String, pengembalian: Pengembalian) {
        try {
            val response = pengembalianAPIService.updatePengembalian(id_pengembalian, pengembalian)
            if (!response.isSuccessful) {
                throw IOException("Failed to update pengembalian with id_pengembalian: $id_pengembalian. HTTP Status code: ${response.code()}")
            }
        } catch (e: IOException) {
            throw IOException("Failed to update pengembalian. Network error occurred.", e)
        }
    }

    override suspend fun deletePengembalian(id_pengembalian: String) {
        try {
            val response = pengembalianAPIService.deletePengembalian(id_pengembalian)
            if (!response.isSuccessful) {
                throw IOException("Failed to delete buku with id_buku: $id_pengembalian. HTTP Status code: ${response.code()}")
            }
        } catch (e: IOException) {
            throw IOException("Failed to delete buku. Network error occurred.", e)
        }
    }
}