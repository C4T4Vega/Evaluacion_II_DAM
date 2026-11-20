package com.dinamite.lexlector.remote

import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test


 //Levanta servidor HTTP falso (MockWebServer) en localhost y apunta Retrofit
 //a esa dirección con RetrofitInstance.crearApi(baseUrl) para comprobar que
 //el JSON se transforma correctamente en objetos Ley

@OptIn(ExperimentalCoroutinesApi::class)
class ApiServiceTest {

    private lateinit var servidor: MockWebServer
    private lateinit var api: ApiService

    @Before
    fun preparar() {
        servidor = MockWebServer().apply { start() }
        api = RetrofitInstance.crearApi(servidor.url("/").toString())
    }

    @After
    fun limpiar() {
        servidor.shutdown()
    }

    @Test
    fun `obtenerLeyes convierte el JSON del servidor en objetos Ley`() = runTest {
        // 1-.el servidor responde con una ley en formato Xano.
        servidor.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody(
                    """
                    [
                      {
                        "id": 10,
                        "nombre": "Ley del Consumidor",
                        "autor": "SERNAC",
                        "anioPublicacion": "1997",
                        "anioActual": "2024",
                        "esReciente": true,
                        "descripcion": "Protege los derechos de los consumidores.",
                        "historial": [{"anio": "2024", "detalle": "Actualización."}]
                      }
                    ]
                    """.trimIndent()
                )
        )

        // 2-.llamada real a Retrofit, pero contra el servidor falso.
        val leyes = api.obtenerLeyes()

        // 3. comprueba q JSON -> Kotlin y petición HTTP correcta.
        assertEquals(1, leyes.size)
        assertEquals(10, leyes[0].id)
        assertEquals("Ley del Consumidor", leyes[0].nombre)
        assertEquals("SERNAC", leyes[0].autor)
        assertEquals(true, leyes[0].esReciente)
        assertEquals("Actualización.", leyes[0].historial?.first()?.detalle)

        val peticion = servidor.takeRequest()
        assertEquals("GET", peticion.method)
        assertEquals("/leyes", peticion.path)
    }
}
