package com.dinamite.lexlector.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Configura Retrofit para conectarse a Xano y traducir JSON a objetos de Kotlin
object RetrofitInstance {

    private const val BASE_URL = "https://x8ki-letl-twmt.n7.xano.io/api:C8WEkoLD/"

    val api: ApiService by lazy {
        crearApi(BASE_URL)
    }

    // Separamos crearApi para reutilizarla en las pruebas unitarias con MockWebServer
    fun crearApi(baseUrl: String): ApiService {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}