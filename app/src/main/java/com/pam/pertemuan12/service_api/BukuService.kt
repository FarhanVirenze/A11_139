package com.pam.pertemuan12.service_api

import com.pam.pertemuan12.model.Buku
import retrofit2.Response
import retrofit2.http.*

interface BukuService {
    @Headers(
        "Accept: application/json",
        "Content-Type: application/json",
    )
    @GET("bacabuku.php")
    suspend fun getBuku(): List<Buku>

    @GET("baca1buku.php")
    suspend fun getBukuById(@Query("id_buku") id_buku: String): Buku

    @POST("insertbuku.php")
    suspend fun insertBuku(@Body buku: Buku): Response<Void>

    @PUT("editbuku.php")
    suspend fun updateBuku(
        @Query("id_buku") id_buku: String,
        @Body buku: Buku
    ): Response<Void>

    @DELETE("deletebuku.php")
    suspend fun deleteBuku(@Query("id_buku") id_buku: String): Response<Void>
}