package com.pam.pertemuan12.service_api

import com.pam.pertemuan12.model.Pengembalian
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface PengembalianService {
    @Headers(
        "Accept: application/json",
        "Content-Type: application/json",
    )
    @GET("bacapengembalian.php")
    suspend fun getPengembalian(): List<Pengembalian>

    @GET("baca1pengembalian.php")
    suspend fun getPengembalianById(@Query("id_pengembalian") id_pengembalian: String): Pengembalian

    @POST("insertpengembalian.php")
    suspend fun insertPengembalian(@Body pengembalian: Pengembalian): Response<Void>

    @PUT("editpengembalian.php")
    suspend fun updatePengembalian(
        @Query("id_pengembalian") id_pengembalian: String,
        @Body pengembalian: Pengembalian
    ): Response<Void>

    @DELETE("deletepengembalian.php")
    suspend fun deletePengembalian(@Query("id_pengembalian") id_pengembalian: String): Response<Void>
}