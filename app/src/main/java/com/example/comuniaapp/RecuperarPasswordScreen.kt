package com.example.comuniaapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp

@Composable
fun RecuperarPasswordScreen(onVolver: () -> Unit) {
    var correo by rememberSaveable { mutableStateOf("") }
    var mensaje by rememberSaveable { mutableStateOf<String?>(null) }

    FormularioCentrado {
        Encabezado("Recuperar contraseña")
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(24.dp)) {
                OutlinedTextField(
                    value = correo,
                    onValueChange = { correo = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Correo electrónico") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )
            }
        }
        Button(
            onClick = {
                mensaje = if (correo.isBlank()) "Ingresa un correo electrónico."
                else "Si el correo está registrado, recibirás las instrucciones de recuperación."
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Mandar correo de recuperación de contraseña") }
        mensaje?.let {
            Text(
                it,
                color = if (correo.isBlank()) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            )
        }
        Button(onClick = onVolver, modifier = Modifier.fillMaxWidth()) {
            Text("Volver a inicio de sesión")
        }
    }
}
