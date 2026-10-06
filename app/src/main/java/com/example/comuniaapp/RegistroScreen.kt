package com.example.comuniaapp

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.*

@Composable
fun RegistroScreen(cargando: Boolean, onRegistrar: (String, String, String, String, String, String) -> Unit, onVolver: () -> Unit) {
    var nombre by rememberSaveable { mutableStateOf("") }
    var apellido by rememberSaveable { mutableStateOf("") }
    var usuario by rememberSaveable { mutableStateOf("") }
    var correo by rememberSaveable { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    var confirmar by remember { mutableStateOf("") }
    FormularioCentrado {
        Encabezado("Creación de cuenta")
        CampoRegistro("Nombre", nombre) { nombre = it }
        CampoRegistro("Apellido", apellido) { apellido = it }
        CampoRegistro("Usuario", usuario) { usuario = it }
        CampoRegistro("Correo electrónico", correo, KeyboardType.Email) { correo = it }
        CampoRegistro("Contraseña", clave, KeyboardType.Password) { clave = it }
        CampoRegistro("Confirmar contraseña", confirmar, KeyboardType.Password) { confirmar = it }
        Text("Usa al menos 8 caracteres. El correo será tu identificador para iniciar sesión.")
        Button(onClick = { onRegistrar(nombre, apellido, usuario, correo, clave, confirmar) }, enabled = !cargando,
            modifier = Modifier.fillMaxWidth()) { Text(if (cargando) "Creando cuenta…" else "Crear cuenta") }
        OutlinedButton(onClick = onVolver, enabled = !cargando, modifier = Modifier.fillMaxWidth()) { Text("Volver a inicio de sesión") }
    }
}
@Composable
private fun CampoRegistro(etiqueta: String, valor: String, tipo: KeyboardType = KeyboardType.Text, alCambiar: (String) -> Unit) {
    OutlinedTextField(valor, alCambiar, Modifier.fillMaxWidth(), label = { Text(etiqueta) }, singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = tipo),
        visualTransformation = if (tipo == KeyboardType.Password) PasswordVisualTransformation() else VisualTransformation.None)
}
