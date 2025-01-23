package com.pam.pertemuan12.dependenciesinjection

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.pam.pertemuan12.repository.AnggotaRepository
import com.pam.pertemuan12.repository.BukuRepository
import com.pam.pertemuan12.repository.NetworkBukuRepository
import com.pam.pertemuan12.repository.NetworkAnggotaRepository // Pastikan ini ada
import com.pam.pertemuan12.repository.NetworkPeminjamanRepository
import com.pam.pertemuan12.repository.NetworkPengembalianRepository
import com.pam.pertemuan12.repository.PeminjamanRepository
import com.pam.pertemuan12.repository.PengembalianRepository
import com.pam.pertemuan12.service_api.AnggotaService
import com.pam.pertemuan12.service_api.BukuService
import com.pam.pertemuan12.service_api.PeminjamanService
import com.pam.pertemuan12.service_api.PengembalianService
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

interface AppContainer {
    val bukuRepository: BukuRepository
    val anggotaRepository: AnggotaRepository
    val peminjamanRepository: PeminjamanRepository
    val pengembalianRepository: PengembalianRepository
}

class Container : AppContainer {
    private val baseUrl = "http://10.0.2.2/umyTI/" // Pastikan base URL sesuai dengan folder struktur API
    private val json = Json { ignoreUnknownKeys = true }

    private val retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(baseUrl)
        .build()

    private val bukuService: BukuService by lazy {
        retrofit.create(BukuService::class.java)
    }

    override val bukuRepository: BukuRepository by lazy {
        NetworkBukuRepository(bukuService)
    }

    private val anggotaService: AnggotaService by lazy {
        retrofit.create(AnggotaService::class.java)
    }

    override val anggotaRepository: AnggotaRepository by lazy {
        NetworkAnggotaRepository(anggotaService)
    }

    private val peminjamanService: PeminjamanService by lazy {
        retrofit.create(PeminjamanService::class.java)
    }

    override val peminjamanRepository: PeminjamanRepository by lazy {
        NetworkPeminjamanRepository(peminjamanService)
    }

    private val pengembalianService: PengembalianService by lazy {
        retrofit.create(PengembalianService::class.java)
    }

    override val pengembalianRepository: PengembalianRepository by lazy {
        NetworkPengembalianRepository(pengembalianService)
    }
}
