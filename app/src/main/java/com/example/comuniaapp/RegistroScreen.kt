package com.example.comuniaapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun RegistroScreen(
    usuarios: MutableList<Usuario>,
    onRegistroExitoso: (Usuario) -> Unit,
    onVolver: () -> Unit
) {
    var nombre by rememberSaveable { mutableStateOf("") }
    var apellido by rememberSaveable { mutableStateOf("") }
    var usuario by rememberSaveable { mutableStateOf("") }
    var correo by rememberSaveable { mutableStateOf("") }
    var contrasena by rememberSaveable { mutableStateOf("") }
    var confirmarContrasena by rememberSaveable { mutableStateOf("") }
    var mensajeError by rememberSaveable { mutableStateOf<String?>(null) }

    FormularioCentrado {
        Encabezado("Creación de cuenta")
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CampoRegistro("Nombre", nombre) { nombre = it }
                CampoRegistro("Apellido", apellido) { apellido = it }
                CampoRegistro("Usuario", usuario) { usuario = it }
                CampoRegistro("Correo electrónico", correo, KeyboardType.Email) { correo = it }
                CampoRegistro("Contraseña", contrasena, KeyboardType.Password, true) { contrasena = it }
                CampoRegistro("Confirmar contraseña", confirmarContrasena, KeyboardType.Password, true) {
                    confirmarContrasena = it
                }
            }
        }
        mensajeError?.let { MensajeError(it) }
        Button(
            onClick = {
                mensajeError = when {
                    listOf(nombre, apellido, usuario, correo, contrasena, confirmarContrasena).any { it.isBlank() } ->
                        "Completa todos los campos."
                    contrasena != confirmarContrasena -> "Las contraseñas no coinciden."
                    usuarios.size >= 5 -> "Se alcanzó el límite de 5 usuarios registrados."
                    usuarios.any { it.usuario.equals(usuario.trim(), ignoreCase = true) } -> "El usuario ya está registrado."
                    else -> null
                }
                if (mensajeError == null) {
                    val nuevoUsuario = Usuario(nombre.trim(), apellido.trim(), usuario.trim(), correo.trim(), contrasena)
                    usuarios.add(nuevoUsuario)
                    onRegistroExitoso(nuevoUsuario)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Crear cuenta") }
        Button(onClick = onVolver, modifier = Modifier.fillMaxWidth()) {
            Text("Volver a inicio de sesión")
        }
    }
}

@Composable
private fun CampoRegistro(
    etiqueta: String,
    valor: String,
    tipoTeclado: KeyboardType = KeyboardType.Text,
    esContrasena: Boolean = false,
    alCambiar: (String) -> Unit
) {
    OutlinedTextField(
        value = valor,
        onValueChange = alCambiar,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(etiqueta) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = tipoTeclado),
        visualTransformation = if (esContrasena) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None
    )
}
