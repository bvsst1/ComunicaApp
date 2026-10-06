package com.example.comuniaapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.comuniaapp.model.Usuario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsuariosRegistradosScreen(usuario: Usuario, onVolver: () -> Unit) {
    Scaffold(topBar = { TopAppBar(title = { Text("Mi perfil") }, navigationIcon = {
        TextButton(onClick = onVolver) { Text("Volver") }
    }) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Datos de tu cuenta", style = MaterialTheme.typography.titleLarge)
            listOf("Nombre" to "${usuario.nombre} ${usuario.apellido}", "Usuario" to usuario.usuario, "Correo" to usuario.correo).forEach { (campo, valor) ->
                Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp)) {
                    Text(campo, style = MaterialTheme.typography.labelLarge)
                    Text(valor, style = MaterialTheme.typography.bodyLarge)
                } }
            }
            Text("Tus frases y datos de cuenta son privados. Puedes cambiar tu contraseña desde la recuperación de acceso.")
        }
    }
}
