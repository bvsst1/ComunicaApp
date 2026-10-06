package com.example.comuniaapp

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun RecuperarPasswordScreen(cargando: Boolean, onRecuperar: (String) -> Unit, onVolver: () -> Unit) {
    var correo by rememberSaveable { mutableStateOf("") }
    FormularioCentrado {
        Encabezado("Recuperar contraseña")
        Text("Escribe el correo con el que creaste tu cuenta.")
        OutlinedTextField(correo, { correo = it }, Modifier.fillMaxWidth(), label = { Text("Correo electrónico") },
            singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))
        Button(onClick = { onRecuperar(correo) }, enabled = !cargando, modifier = Modifier.fillMaxWidth()) { Text("Enviar instrucciones") }
        OutlinedButton(onClick = onVolver, enabled = !cargando, modifier = Modifier.fillMaxWidth()) { Text("Volver a inicio de sesión") }
    }
}
