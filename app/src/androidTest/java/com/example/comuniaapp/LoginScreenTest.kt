package com.example.comuniaapp

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class LoginScreenTest {
    @get:Rule val compose = createComposeRule()
    @Test fun formularioEnviaCorreoYClave() {
        var enviados: Pair<String, String>? = null
        compose.setContent { MaterialTheme { LoginScreen(false, { correo, clave -> enviados = correo to clave }, {}, {}) } }
        compose.onNodeWithText("Correo electrónico").performTextInput("ana@example.cl")
        compose.onNode(hasSetTextAction() and hasText("Contraseña")).performTextInput("Seguro123!")
        compose.onNodeWithText("Iniciar sesión", useUnmergedTree = true).performClick()
        assertEquals("ana@example.cl" to "Seguro123!", enviados)
    }
    @Test fun bloqueaEnvioDuranteLaCarga() {
        compose.setContent { MaterialTheme { LoginScreen(true, { _, _ -> fail("No debe enviar durante la carga") }, {}, {}) } }
        compose.onNodeWithText("Ingresando…").assertIsNotEnabled()
    }
    @Test fun navegaARegistroYRecuperacion() {
        var registro = false
        var recuperacion = false
        compose.setContent { MaterialTheme { LoginScreen(false, { _, _ -> }, { recuperacion = true }, { registro = true }) } }
        compose.onNodeWithText("Crear cuenta").performClick()
        compose.onNodeWithText("Olvidé mi contraseña").performClick()
        assertTrue(registro); assertTrue(recuperacion)
    }
}
