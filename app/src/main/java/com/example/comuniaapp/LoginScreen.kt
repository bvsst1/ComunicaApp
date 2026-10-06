package com.example.comuniaapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun LoginScreen(cargando: Boolean, onIniciarSesion: (String, String) -> Unit,
                onOlvideContrasena: () -> Unit, onCrearCuenta: () -> Unit) {
    var correo by rememberSaveable { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    FormularioCentrado {
        Encabezado("Inicio de sesión")
        Text("Accede a tus frases y herramientas de comunicación.")
        OutlinedTextField(correo, { correo = it }, Modifier.fillMaxWidth(), label = { Text("Correo electrónico") },
            singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))
        OutlinedTextField(contrasena, { contrasena = it }, Modifier.fillMaxWidth(), label = { Text("Contraseña") },
            singleLine = true, visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password))
        Button(onClick = { onIniciarSesion(correo, contrasena) }, enabled = !cargando, modifier = Modifier.fillMaxWidth()) {
            Text(if (cargando) "Ingresando…" else "Iniciar sesión")
        }
        OutlinedButton(onClick = onOlvideContrasena, enabled = !cargando, modifier = Modifier.fillMaxWidth()) { Text("Olvidé mi contraseña") }
        TextButton(onClick = onCrearCuenta, enabled = !cargando) { Text("Crear cuenta") }
    }
}

@Composable
fun FormularioCentrado(contenido: @Composable ColumnScope.() -> Unit) {
    Box(Modifier.fillMaxSize().imePadding(), contentAlignment = Alignment.TopCenter) {
        Column(Modifier.widthIn(max = 560.dp).fillMaxWidth().verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp), content = contenido)
    }
}
@Composable fun Encabezado(subtitulo: String) {
    Text("ComunicaApp", style = MaterialTheme.typography.headlineMedium)
    Text(subtitulo, style = MaterialTheme.typography.titleMedium)
}
@Composable fun MensajeError(mensaje: String) { Text(mensaje, color = MaterialTheme.colorScheme.error) }
