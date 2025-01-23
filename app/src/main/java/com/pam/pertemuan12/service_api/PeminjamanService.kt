package com.pam.pertemuan12.service_api

import com.pam.pertemuan12.model.Peminjaman
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface PeminjamanService {
    @Headers(
        "Accept: application/json",
        "Content-Type: application/json",
    )
    @GET("bacapeminjaman.php")
    suspend fun getPeminjaman(): List<Peminjaman>

    @GET("baca1peminjaman.php")
    suspend fun getPeminjamanById(@Query("id_peminjaman") id_peminjaman: String): Peminjaman

    @POST("insertpeminjaman.php")
    suspend fun insertPeminjaman(@Body peminjaman: Peminjaman): Response<Void>

    @PUT("editpeminjaman.php")
    suspend fun updatePeminjaman(
        @Query("id_peminjaman") id_peminjaman: String,
        @Body peminjaman: Peminjaman
    ): Response<Void>

    @DELETE("deletepeminjaman.php")
    suspend fun deletePeminjaman(@Query("id_peminjaman") id_peminjaman: String): Response<Void>
}