package com.example.comuniaapp

import com.example.comuniaapp.domain.Frases
import com.example.comuniaapp.model.FraseAccesible
import org.junit.Assert.*
import org.junit.Test

class FrasesTest {
    private val frases = listOf(
        FraseAccesible("1", "Salud", "Necesito ayuda", true),
        FraseAccesible("2", "Compras", "Una bolsa por favor", false),
        FraseAccesible("3", "Salud", "Escriba las indicaciones", false)
    )
    @Test fun buscaSinDistinguirMayusculasYRecortaEspacios() {
        assertEquals(listOf(frases[0]), Frases.filtrar(frases, " AYUDA ", "Todas", false))
    }
    @Test fun combinaCategoriaYFrecuencia() {
        assertEquals(listOf(frases[0]), Frases.filtrar(frases, "", "Salud", true))
    }
    @Test fun consultaVaciaDevuelveTodasYBuscaCategoria() {
        assertEquals(frases, Frases.filtrar(frases, "", "Todas", false))
        assertEquals(2, Frases.filtrar(frases, "salud", "Todas", false).size)
    }
    @Test fun sinCoincidenciasDevuelveListaVacia() {
        assertTrue(Frases.filtrar(frases, "xyz", "Todas", false).isEmpty())
    }
}
