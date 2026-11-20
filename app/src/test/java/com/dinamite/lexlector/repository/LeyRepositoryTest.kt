package com.dinamite.lexlector.repository

import com.dinamite.lexlector.remote.ApiService
import com.dinamite.lexlector.remote.RetrofitInstance
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.lang.reflect.Field

//LeyRepository llama a RetrofitInstance.api (URL fija de Xano). Como no queremos
//modificar ese archivo, el test crea una API con RetrofitInstance.crearApi(...)
//apuntando al MockWebServer y reemplaza temporalmente el delegate del `val` lazy.
//Al terminar se restaura el original para no afectar a las otras pruebas.

@OptIn(ExperimentalCoroutinesApi::class)
class LeyRepositoryTest {

    private lateinit var servidor: MockWebServer

    // Campo generado por Kotlin para `val api by lazy { ... }`.
    private val campoApi = RetrofitInstance::class.java.getDeclaredField("api\$delegate").apply {
        isAccessible = true
    }
    private var delegadoOriginal: Any? = null

    @Before
    fun preparar() {
        servidor = MockWebServer().apply { start() }

        delegadoOriginal = campoApi.get(RetrofitInstance)

        val apiDePrueba = RetrofitInstance.crearApi(servidor.url("/").toString())
        escribirDelegate(apiDePrueba)
    }

    @After
    fun limpiar() {
        delegadoOriginal?.let { escribirDelegate(it) }
        servidor.shutdown()
    }

    /** Reemplaza el Lazy de RetrofitInstance.api sin tocar el código de producción. */
    @Suppress("UNCHECKED_CAST")
    private fun escribirDelegate(valor: Any) {
        val claseUnsafe = Class.forName("sun.misc.Unsafe")
        val theUnsafe = claseUnsafe.getDeclaredField("theUnsafe").apply { isAccessible = true }.get(null)

        val offset = claseUnsafe
            .getMethod("staticFieldOffset", Field::class.java)
            .invoke(theUnsafe, campoApi) as Long

        claseUnsafe
            .getMethod("putObject", Any::class.java, Long::class.javaPrimitiveType, Any::class.java)
            .invoke(theUnsafe, RetrofitInstance::class.java, offset, valor)
    }

    private fun escribirDelegate(api: ApiService) = escribirDelegate(lazyOf(api) as Any)

    @Test
    fun `obtenerLeyesApi del repositorio devuelve lo que responde la API`() = runTest {
        // 1. Preparación: dos leyes desde el servidor falso.
        servidor.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody(
                    """
                    [
                      {
                        "id": 1,
                        "nombre": "Código Civil",
                        "autor": "Ministerio de Justicia",
                        "anioPublicacion": "1855",
                        "anioActual": "2024",
                        "esReciente": true,
                        "descripcion": "Regula las relaciones civiles.",
                        "historial": []
                      },
                      {
                        "id": 2,
                        "nombre": "Código Penal",
                        "autor": "Ministerio de Justicia",
                        "anioPublicacion": "1874",
                        "anioActual": "2020",
                        "esReciente": false,
                        "descripcion": "Delitos y penas.",
                        "historial": []
                      }
                    ]
                    """.trimIndent()
                )
        )

        // 2. Acción: se llama al repositorio real, no a un repositorio falso.
        val leyes = LeyRepository.obtenerLeyesApi()

        // 3. Comprobación: el repositorio delega correctamente en la API.
        assertEquals(2, leyes.size)
        assertEquals("Código Civil", leyes[0].nombre)
        assertEquals("Código Penal", leyes[1].nombre)
        assertEquals(false, leyes[1].esReciente)

        val peticion = servidor.takeRequest()
        assertEquals("GET", peticion.method)
        assertEquals("/leyes", peticion.path)
    }
}
