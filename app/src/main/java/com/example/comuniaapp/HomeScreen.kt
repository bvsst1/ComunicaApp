package com.example.comuniaapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    nombreUsuario: String,
    onCerrarSesion: () -> Unit,
    onAbrirPanel: () -> Unit,
    onAbrirAvisos: () -> Unit
) {
    var textoGrande by rememberSaveable { mutableStateOf(false) }
    var altoContraste by rememberSaveable { mutableStateOf(false) }
    val fondo = if (altoContraste) MaterialTheme.colorScheme.inverseOnSurface else MaterialTheme.colorScheme.background
    val texto = if (altoContraste) MaterialTheme.colorScheme.inverseSurface else MaterialTheme.colorScheme.onBackground
    val tamanoTitulo = if (textoGrande) 26.sp else 20.sp

    Scaffold(
        containerColor = fondo,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("ComunicaApp - Inicio", fontSize = tamanoTitulo) },
                actions = {
                    Button(
                        onClick = { textoGrande = !textoGrande },
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .semantics { contentDescription = "Aumentar o reducir tamaño del texto" }
                    ) { Text("A+") }
                    Button(
                        onClick = { altoContraste = !altoContraste },
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .semantics { contentDescription = "Activar o desactivar alto contraste" }
                    ) { Text("Contraste") }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = fondo)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "¡Hola, $nombreUsuario! Bienvenido a tu espacio accesible",
                    modifier = Modifier.padding(24.dp),
                    color = texto,
                    fontSize = if (textoGrande) 26.sp else 21.sp,
                    style = MaterialTheme.typography.titleLarge
                )
            }
            AccesoRapido(
                titulo = "Escribir para Comunicar",
                descripcion = "Abrir panel de frases y comunicación visual rápida",
                textoGrande = textoGrande,
                onClick = onAbrirPanel
            )
            AccesoRapido(
                titulo = "Panel de Noticias y Avisos",
                descripcion = "Abrir noticias y avisos accesibles",
                textoGrande = textoGrande,
                onClick = onAbrirAvisos
            )
            AccesoRapido(
                titulo = "Historial de Frases Rápidas",
                descripcion = "Abrir historial de frases rápidas",
                textoGrande = textoGrande,
                onClick = onAbrirPanel
            )
            OutlinedButton(onClick = onCerrarSesion, modifier = Modifier.fillMaxWidth()) {
                Text("Cerrar sesión", fontSize = if (textoGrande) 20.sp else 16.sp)
            }
        }
    }
}

@Composable
private fun AccesoRapido(
    titulo: String,
    descripcion: String,
    textoGrande: Boolean,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .semantics { contentDescription = descripcion },
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(titulo, fontSize = if (textoGrande) 22.sp else 18.sp, style = MaterialTheme.typography.titleMedium)
            Text(descripcion, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
