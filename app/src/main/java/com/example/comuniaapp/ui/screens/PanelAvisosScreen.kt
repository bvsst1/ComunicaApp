package com.example.comuniaapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class Noticia(
    val id: Int,
    val titulo: String,
    val contenido: String,
    val fecha: String,
    val prioridad: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PanelAvisosScreen(onVolver: () -> Unit) {
    var tamanoFuente by rememberSaveable { mutableStateOf(1f) }

    val noticias = remember {
        listOf(
            Noticia(
                1,
                "Nueva actualización de accesibilidad",
                "Se ha lanzado la versión 2.1 de ComunicaApp con mejoras en el panel de frases, sincronización en la nube y nuevo modo alto contraste.",
                "15/08/2026",
                "Alta"
            ),
            Noticia(
                2,
                "Taller de comunicación inclusiva",
                "El próximo 25 de agosto se realizará un taller virtual gratuito sobre herramientas de comunicación para personas con discapacidad auditiva. Inscripciones abiertas.",
                "10/08/2026",
                "Media"
            ),
            Noticia(
                3,
                "Recordatorio: Actualiza tu perfil",
                "Para recibir notificaciones personalizadas, verifica que tu correo y preferencias de contacto estén actualizados en la configuración de la app.",
                "05/08/2026",
                "Baja"
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Noticias y Avisos", fontSize = 22.sp * tamanoFuente) },
                navigationIcon = {
                    OutlinedButton(onClick = onVolver, modifier = Modifier.padding(start = 8.dp)) {
                        Text("Volver", fontSize = 14.sp * tamanoFuente)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { tamanoFuente = 1f },
                    modifier = Modifier.semantics { contentDescription = "Tamaño de fuente normal" }
                ) { Text("A", fontSize = 16.sp) }
                OutlinedButton(
                    onClick = { tamanoFuente = 1.25f },
                    modifier = Modifier.semantics { contentDescription = "Tamaño de fuente grande" }
                ) { Text("A+", fontSize = 18.sp) }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(noticias) { noticia ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    noticia.fecha,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 12.sp * tamanoFuente
                                )
                                Box(
                                    modifier = Modifier
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                        .semantics { contentDescription = "Prioridad ${noticia.prioridad}" }
                                ) {
                                    Text(
                                        noticia.prioridad,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = when (noticia.prioridad) {
                                            "Alta" -> Color.Red
                                            "Media" -> Color(0xFFFF9800.toInt())
                                            else -> Color.Green
                                        },
                                        fontSize = 11.sp * tamanoFuente,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Text(
                                noticia.titulo,
                                style = MaterialTheme.typography.titleMedium,
                                fontSize = 18.sp * tamanoFuente,
                                fontWeight = FontWeight.Bold
                            )
                            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 4.dp))
                            Text(
                                noticia.contenido,
                                style = MaterialTheme.typography.bodyMedium,
                                fontSize = 15.sp * tamanoFuente
                            )
                        }
                    }
                }
            }
        }
    }
}