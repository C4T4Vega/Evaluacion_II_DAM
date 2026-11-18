package com.dinamite.lexlector.remote

import com.dinamite.lexlector.model.Apunte
import com.dinamite.lexlector.model.Ley
import com.dinamite.lexlector.model.Usuario
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    //LEYES (3 endpoints)
    @GET("leyes")
    suspend fun obtenerLeyes(): List<Ley>

    @GET("leyes/{id}")
    suspend fun obtenerLeyPorId(@Path("id") id: Int): Ley

    @POST("leyes")
    suspend fun crearLey(@Body nuevaLey: Ley): Ley

    //APUNTES (4 endpoints)
    @GET("apuntes")
    suspend fun obtenerApuntes(): List<Apunte>

    @POST("apuntes")
    suspend fun crearApunte(@Body nuevoApunte: Apunte): Apunte

    @PATCH("apuntes/{id}")
    suspend fun actualizarApunte(@Path("id") id: Int, @Body apunte: Apunte): Apunte

    @DELETE("apuntes/{id}")
    suspend fun eliminarApunte(@Path("id") id: Int)

    //USUARIOS (2 endpoints)
    @GET("usuarios")
    suspend fun obtenerUsuarios(): List<Usuario>

    @POST("usuarios")
    suspend fun registrarUsuario(@Body nuevoUsuario: Usuario): Usuario
}