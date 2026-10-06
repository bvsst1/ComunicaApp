package com.example.comuniaapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(nombreUsuario: String, textoGrande: Boolean, altoContraste: Boolean, cargando: Boolean,
               onTextoGrande: () -> Unit, onContraste: () -> Unit, onCerrarSesion: () -> Unit,
               onAbrirPanel: () -> Unit, onAbrirHistorial: () -> Unit, onAbrirAvisos: () -> Unit,
               onAbrirRecomendaciones: () -> Unit, onAbrirUsuariosRegistrados: () -> Unit) {
    Scaffold(topBar = { CenterAlignedTopAppBar(title = { Text("ComunicaApp") }) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("¡Hola, $nombreUsuario!", style = MaterialTheme.typography.headlineSmall)
            Text("Tu espacio para comunicarte con claridad.")
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onTextoGrande, modifier = Modifier.weight(1f)) { Text(if (textoGrande) "Texto normal" else "Texto grande") }
                OutlinedButton(onClick = onContraste, modifier = Modifier.weight(1f)) { Text(if (altoContraste) "Contraste normal" else "Alto contraste") }
            }
            AccesoRapido("Escribir para comunicar", "Escribe, muestra y lee tus frases", onAbrirPanel)
            AccesoRapido("Panel de noticias y avisos", "Información accesible de la aplicación", onAbrirAvisos)
            AccesoRapido("Historial de frases rápidas", "Consulta y administra tus frases guardadas", onAbrirHistorial)
            AccesoRapido("Recomendaciones de la semana", "Consejos para facilitar la comunicación", onAbrirRecomendaciones)
            AccesoRapido("Mi perfil", "Consulta los datos de tu cuenta", onAbrirUsuariosRegistrados)
            OutlinedButton(onClick = onCerrarSesion, enabled = !cargando, modifier = Modifier.fillMaxWidth()) { Text("Cerrar sesión") }
        }
    }
}
@Composable
private fun AccesoRapido(titulo: String, descripcion: String, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(titulo, style = MaterialTheme.typography.titleMedium)
            Text(descripcion, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
