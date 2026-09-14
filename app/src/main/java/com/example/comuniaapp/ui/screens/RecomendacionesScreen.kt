package com.example.comuniaapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class ConsejoComunicacion(
    val id: Int,
    val dia: String,
    val titulo: String,
    val recomendacion: String
)

val consejosSemana: List<ConsejoComunicacion> = listOf(
    ConsejoComunicacion(
        1,
        "Lunes",
        "Anticipa el tema de la conversación",
        "Antes de hablar, muestra o escribe el tema principal para que la otra persona pueda seguir el hilo con más facilidad."
    ),
    ConsejoComunicacion(
        2,
        "Martes",
        "Usa frases cortas y directas",
        "Prefiere oraciones breves y evita varias ideas juntas; así se reduce la carga de comprensión en cada mensaje."
    ),
    ConsejoComunicacion(
        3,
        "Miércoles",
        "Confirma que el mensaje se entendió",
        "Pide que te repitan la idea con sus palabras o usa una pregunta simple de sí/no para verificar la comprensión."
    ),
    ConsejoComunicacion(
        4,
        "Jueves",
        "Apóyate en apoyos visuales",
        "Complementa lo hablado con gestos, imágenes o texto escrito; los apoyos visuales refuerzan el mensaje."
    ),
    ConsejoComunicacion(
        5,
        "Viernes",
        "Da tiempo para responder",
        "Evita interrumpir o completar la frase por la otra persona; respeta las pausas naturales de la conversación."
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecomendacionesScreen(onVolver: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recomendaciones de la semana") },
                navigationIcon = {
                    OutlinedButton(onClick = onVolver, modifier = Modifier.padding(start = 8.dp)) {
                        Text("Volver")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(consejosSemana, key = { it.id }) { consejo ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            consejo.dia,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            consejo.titulo,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(consejo.recomendacion, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}
