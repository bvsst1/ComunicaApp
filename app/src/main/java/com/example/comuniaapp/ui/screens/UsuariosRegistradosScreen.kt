package com.example.comuniaapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.comuniaapp.Usuario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsuariosRegistradosScreen(usuarios: List<Usuario>, onVolver: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Usuarios registrados") },
                navigationIcon = {
                    OutlinedButton(onClick = onVolver, modifier = Modifier.padding(start = 8.dp)) {
                        Text("Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                "Array en memoria con los ${usuarios.size} de 5 usuarios registrados desde el formulario de Registro.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            TablaUsuarios(usuarios)
        }
    }
}

@Composable
private fun TablaUsuarios(usuarios: List<Usuario>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline))
    ) {
        FilaTabla("Nombre", "Usuario", "Contraseña", esEncabezado = true)
        HorizontalDivider()
        if (usuarios.isEmpty()) {
            Text(
                "Aún no hay usuarios registrados.",
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            usuarios.forEachIndexed { index, usuario ->
                FilaTabla(usuario.nombre, usuario.usuario, usuario.contrasena)
                if (index < usuarios.lastIndex) HorizontalDivider()
            }
        }
    }
}

@Composable
private fun FilaTabla(nombre: String, usuario: String, contrasena: String, esEncabezado: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val peso = if (esEncabezado) FontWeight.Bold else FontWeight.Normal
        Text(nombre, modifier = Modifier.weight(1f), fontWeight = peso)
        Text(usuario, modifier = Modifier.weight(1f), fontWeight = peso)
        Text(contrasena, modifier = Modifier.weight(1f), fontWeight = peso)
    }
}
