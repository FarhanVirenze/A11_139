package com.pam.pertemuan12.repository

import com.pam.pertemuan12.model.Anggota
import com.pam.pertemuan12.service_api.AnggotaService
import java.io.IOException

interface AnggotaRepository {
    suspend fun getAnggota(): List<Anggota>
    suspend fun insertAnggota(anggota: Anggota)
    suspend fun updateAnggota(id_anggota: String, anggota: Anggota)
    suspend fun deleteAnggota(id_anggota: String)
    suspend fun getAnggotaById(id_anggota: String): Anggota
}

class NetworkAnggotaRepository(
    private val anggotaAPIService: AnggotaService
) : AnggotaRepository {

    override suspend fun getAnggota(): List<Anggota> {
        try {
            return anggotaAPIService.getAnggota()
        } catch (e: IOException) {
            throw IOException("Failed to fetch buku list. Network error occurred.", e)
        }
    }

    override suspend fun getAnggotaById(id_anggota: String): Anggota {
        try {
            return anggotaAPIService.getAnggotaById(id_anggota)
        } catch (e: IOException) {
            throw IOException("Failed to fetch buku with id_buku: $id_anggota. Network error occurred.", e)
        }
    }

    override suspend fun insertAnggota(anggota: Anggota) {
        try {
            val response = anggotaAPIService.insertAnggota(anggota)
            if (!response.isSuccessful) {
                throw IOException("Failed to insert buku. HTTP Status code: ${response.code()}")
            }
        } catch (e: IOException) {
            throw IOException("Failed to insert buku. Network error occurred.", e)
        }
    }

    override suspend fun updateAnggota(id_anggota: String, anggota: Anggota) {
        try {
            val response = anggotaAPIService.updateAnggota(id_anggota, anggota)
            if (!response.isSuccessful) {
                throw IOException("Failed to update buku with id_buku: $id_anggota. HTTP Status code: ${response.code()}")
            }
        } catch (e: IOException) {
            throw IOException("Failed to update buku. Network error occurred.", e)
        }
    }

    override suspend fun deleteAnggota(id_anggota: String) {
        try {
            val response = anggotaAPIService.deleteAnggota(id_anggota)
            if (!response.isSuccessful) {
                throw IOException("Failed to delete buku with id_buku: $id_anggota. HTTP Status code: ${response.code()}")
            }
        } catch (e: IOException) {
            throw IOException("Failed to delete buku. Network error occurred.", e)
        }
    }
}