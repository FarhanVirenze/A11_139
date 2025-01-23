package com.pam.pertemuan12.service_api

import com.pam.pertemuan12.model.Anggota
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface AnggotaService {
    @Headers(
        "Accept: application/json",
        "Content-Type: application/json",
    )
    @GET("bacaanggota.php")
    suspend fun getAnggota(): List<Anggota>

    @GET("baca1anggota.php")
    suspend fun getAnggotaById(@Query("id_anggota") id_anggota: String): Anggota

    @POST("insertanggota.php")
    suspend fun insertAnggota(@Body anggota: Anggota): Response<Void>

    @PUT("editanggota.php")
    suspend fun updateAnggota(
        @Query("id_anggota") id_anggota: String,
        @Body anggota: Anggota
    ): Response<Void>

    @DELETE("deleteanggota.php")
    suspend fun deleteAnggota(@Query("id_anggota") id_anggota: String): Response<Void>
}