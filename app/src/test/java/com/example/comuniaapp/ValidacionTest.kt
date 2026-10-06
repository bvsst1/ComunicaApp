package com.example.comuniaapp

import com.example.comuniaapp.domain.Validacion
import org.junit.Assert.*
import org.junit.Test

class ValidacionTest {
    @Test fun aceptaRegistroValido() {
        assertNull(Validacion.registro("Ana", "Pérez", "ana", "ana@example.cl", "Seguro123", "Seguro123"))
    }
    @Test fun rechazaCamposVacios() {
        assertNotNull(Validacion.registro(" ", "Pérez", "ana", "ana@example.cl", "Seguro123", "Seguro123"))
    }
    @Test fun rechazaCorreoInvalido() {
        listOf("", "ana", "ana@", "@ejemplo.cl", "ana @ejemplo.cl").forEach {
            assertNotNull(Validacion.correo(it))
        }
    }
    @Test fun normalizaCorreo() {
        assertEquals("ana@example.cl", Validacion.normalizarCorreo("  ANA@Example.CL "))
    }
    @Test fun rechazaClaveDebilYConfirmacionDistinta() {
        assertNotNull(Validacion.registro("Ana", "Pérez", "ana", "ana@example.cl", "123", "123"))
        assertNotNull(Validacion.registro("Ana", "Pérez", "ana", "ana@example.cl", "Seguro123", "Otro123"))
    }
    @Test fun validaLongitudYCategoriaDeFrase() {
        assertNotNull(Validacion.frase("  ", "General"))
        assertNotNull(Validacion.frase("a".repeat(501), "General"))
        assertNotNull(Validacion.frase("Hola", "Todas"))
        assertNull(Validacion.frase("a".repeat(500), "Salud"))
    }
}
