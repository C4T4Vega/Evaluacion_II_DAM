package com.dinamite.lexlector.viewmodel

import com.dinamite.lexlector.model.Ley
import com.dinamite.lexlector.repository.LeyesDataSource
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LeyViewModelTest {

    // Reemplaza el hilo principal de Android para poder probar corrutinas en el PC
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun preparar() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun limpiar() {
        Dispatchers.resetMain()
    }

    @Test
    fun `carga leyes y filtra las recientes`() = runTest {
        // 1. Preparación: repositorio falso sin internet
        val viewModel = LeyViewModel(FakeRepository(simularError = false))

        // 2. Acción: esperar que termine la corrutina de carga
        advanceUntilIdle()

        // 3. Comprobación: cargó las 2 leyes falsas y dejó solo 1 reciente
        assertEquals(2, viewModel.todasLasLeyes.size)
        assertEquals(1, viewModel.leyesRecientes.size)
        assertEquals("Ley del Consumidor", viewModel.leyesRecientes.first().nombre)
    }

    @Test
    fun `si falla internet mantiene las leyes locales`() = runTest {
        // 1. Preparación: repositorio falso que lanza error de red
        val viewModel = LeyViewModel(FakeRepository(simularError = true))

        // 2. Acción
        advanceUntilIdle()

        // 3. Comprobación: no se cae y conserva las 3 leyes locales de respaldo
        assertEquals(3, viewModel.todasLasLeyes.size)
        assertEquals("Código Civil", viewModel.todasLasLeyes.first().nombre)
    }

    @Test
    fun `buscador encuentra ley por nombre`() = runTest {
        val viewModel = LeyViewModel(FakeRepository(simularError = false))
        advanceUntilIdle()

        val resultado = viewModel.buscarLeyes("penal")

        assertEquals(1, resultado.size)
        assertEquals("Código Penal", resultado.first().nombre)
    }

    // Repositorio falso en memoria: si simularError = true falla, si es false devuelve 2 leyes
    private class FakeRepository(
        private val simularError: Boolean
    ) : LeyesDataSource {

        override suspend fun obtenerLeyesApi(): List<Ley> {
            if (simularError) throw IOException("Sin internet")
            return listOf(
                Ley(10, "Ley del Consumidor", "SERNAC", "1997", "2024", true, "Derechos."),
                Ley(11, "Código Penal", "Justicia", "1874", "2020", false, "Delitos.")
            )
        }

        override suspend fun obtenerLeyPorIdApi(id: Int): Ley = obtenerLeyesApi().first()

        override suspend fun crearLeyApi(ley: Ley): Ley = ley
    }
}