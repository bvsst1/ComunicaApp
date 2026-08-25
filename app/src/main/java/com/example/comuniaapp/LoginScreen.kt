package com.example.comuniaapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun LoginScreen(
    onIniciarSesion: (String, String) -> Boolean,
    onOlvideContrasena: () -> Unit,
    onCrearCuenta: () -> Unit,
    onSesionIniciada: () -> Unit
) {
    var usuario by rememberSaveable { mutableStateOf("") }
    var contrasena by rememberSaveable { mutableStateOf("") }
    var mensajeError by rememberSaveable { mutableStateOf<String?>(null) }

    FormularioCentrado {
        Encabezado("Inicio de sesión")
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                OutlinedTextField(
                    value = usuario,
                    onValueChange = { usuario = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("USUARIO") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = contrasena,
                    onValueChange = { contrasena = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("CONTRASEÑA") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    if (onIniciarSesion(usuario.trim(), contrasena)) onSesionIniciada()
                    else mensajeError = "Usuario o contraseña incorrectos."
                },
                modifier = Modifier.weight(1f)
            ) { Text("Iniciar sesión") }
            Button(
                onClick = onOlvideContrasena,
                modifier = Modifier.weight(1f)
            ) { Text("Olvidé mi contraseña") }
        }
        mensajeError?.let { MensajeError(it) }
        Text("¿No tienes cuenta?", style = MaterialTheme.typography.bodyLarge)
        Button(onClick = onCrearCuenta) { Text("Crear cuenta") }
    }
}

@Composable
fun FormularioCentrado(contenido: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 32.dp, vertical = 56.dp)
            .widthIn(max = 460.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        content = contenido
    )
}

@Composable
fun Encabezado(subtitulo: String) {
    Text("ComunicaAPP", style = MaterialTheme.typography.headlineMedium)
    Text(subtitulo, style = MaterialTheme.typography.titleMedium)
}

@Composable
fun MensajeError(mensaje: String) {
    Text(mensaje, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
}
