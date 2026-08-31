package com.example.comuniaapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.comuniaapp.ui.theme.ComuniaAppTheme
import com.example.comuniaapp.ui.screens.PanelAccesibilidadScreen
import com.example.comuniaapp.ui.screens.PanelAvisosScreen

data class Usuario(
    val nombre: String,
    val apellido: String,
    val usuario: String,
    val correo: String,
    val contrasena: String
)

private enum class Destino {
    LOGIN, RECUPERAR_CONTRASENA, REGISTRO, INICIO, PANEL_ACCESIBILIDAD, PANEL_AVISOS
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComuniaAppTheme {
                ComunicaApp()
            }
        }
    }
}

@androidx.compose.runtime.Composable
private fun ComunicaApp() {
    val usuarios = androidx.compose.runtime.remember { mutableStateListOf<Usuario>() }
    var destino by androidx.compose.runtime.remember { mutableStateOf(Destino.LOGIN) }
    var usuarioActivo by androidx.compose.runtime.remember { mutableStateOf<Usuario?>(null) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when (destino) {
            Destino.LOGIN -> LoginScreen(
                onIniciarSesion = { usuario, contrasena ->
                    val encontrado = usuarios.firstOrNull {
                        it.usuario == usuario && it.contrasena == contrasena
                    }
                    usuarioActivo = encontrado
                    encontrado != null
                },
                onOlvideContrasena = { destino = Destino.RECUPERAR_CONTRASENA },
                onCrearCuenta = { destino = Destino.REGISTRO },
                onSesionIniciada = { destino = Destino.INICIO }
            )

            Destino.RECUPERAR_CONTRASENA -> RecuperarPasswordScreen(
                onVolver = { destino = Destino.LOGIN }
            )

            Destino.REGISTRO -> RegistroScreen(
                usuarios = usuarios,
                onRegistroExitoso = { nuevoUsuario ->
                    usuarioActivo = nuevoUsuario
                    destino = Destino.INICIO
                },
                onVolver = { destino = Destino.LOGIN }
            )

            Destino.INICIO -> HomeScreen(
                nombreUsuario = usuarioActivo?.nombre ?: usuarioActivo?.usuario.orEmpty(),
                onCerrarSesion = {
                    usuarioActivo = null
                    destino = Destino.LOGIN
                },
                onAbrirPanel = { destino = Destino.PANEL_ACCESIBILIDAD },
                onAbrirAvisos = { destino = Destino.PANEL_AVISOS }
            )

            Destino.PANEL_ACCESIBILIDAD -> PanelAccesibilidadScreen(
                onVolver = { destino = Destino.INICIO }
            )

            Destino.PANEL_AVISOS -> PanelAvisosScreen(
                onVolver = { destino = Destino.INICIO }
            )
        }
    }
}
