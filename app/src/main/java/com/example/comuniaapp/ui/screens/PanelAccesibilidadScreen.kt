package com.example.comuniaapp.ui.screens

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.comuniaapp.domain.Frases
import com.example.comuniaapp.domain.Validacion
import com.example.comuniaapp.model.FraseAccesible
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PanelAccesibilidadScreen(frases: List<FraseAccesible>, cargando: Boolean, titulo: String, onVolver: () -> Unit,
                            onGuardar: (String, String, Boolean, String?, () -> Unit) -> Unit,
                            onFrecuencia: (String, Boolean) -> Unit, onEliminar: (String) -> Unit) {
    var entrada by rememberSaveable { mutableStateOf("") }
    var consulta by rememberSaveable { mutableStateOf("") }
    var categoria by rememberSaveable { mutableStateOf("Todas") }
    var frecuentes by rememberSaveable { mutableStateOf(false) }
    var fuenteGrande by rememberSaveable { mutableStateOf(false) }
    var ayuda by remember { mutableStateOf(false) }
    var seleccion by remember { mutableStateOf<FraseAccesible?>(null) }
    var editar by remember { mutableStateOf<FraseAccesible?>(null) }
    var eliminar by remember { mutableStateOf<FraseAccesible?>(null) }
    var ttsListo by remember { mutableStateOf(false) }
    var avisoVoz by remember { mutableStateOf<String?>(null) }
    val contexto = LocalContext.current
    val tts = remember { TextToSpeech(contexto) { ttsListo = it == TextToSpeech.SUCCESS } }
    LaunchedEffect(ttsListo) {
        if (ttsListo) {
            val resultado = tts.setLanguage(Locale.forLanguageTag("es-CL"))
            ttsListo = resultado != TextToSpeech.LANG_MISSING_DATA && resultado != TextToSpeech.LANG_NOT_SUPPORTED
        }
    }
    DisposableEffect(tts) { onDispose { tts.stop(); tts.shutdown() } }
    fun hablar(texto: String) {
        avisoVoz = if (ttsListo && tts.speak(texto, TextToSpeech.QUEUE_FLUSH, null, "frase") == TextToSpeech.SUCCESS) null
        else "La voz en español no está disponible en este dispositivo. Puedes mostrar el mensaje en pantalla."
    }
    val filtradas = Frases.filtrar(frases, consulta, categoria, frecuentes)
    if (ayuda) AlertDialog(onDismissRequest = { ayuda = false }, title = { Text("Asistencia auditiva") },
        text = { Text("Puedes pedir que la información se escriba, mostrar una frase grande o leerla en voz alta con el botón Hablar. Todas las confirmaciones de la app son visuales.") },
        confirmButton = { TextButton(onClick = { ayuda = false }) { Text("Entendido") } })
    seleccion?.let { frase ->
        AlertDialog(onDismissRequest = { seleccion = null; tts.stop() }, title = { Text("Mensaje para comunicar") },
            text = { Column { Text(frase.texto, fontSize = 26.sp); avisoVoz?.let { Text(it) } } },
            confirmButton = { Button(onClick = { hablar(frase.texto) }) { Text("Hablar") } },
            dismissButton = { TextButton(onClick = { seleccion = null; tts.stop() }) { Text("Cerrar") } })
    }
    editar?.let { frase ->
        EditorFrase(frase, cargando, onCancelar = { editar = null }) { texto, contextoFrase ->
            onGuardar(texto, contextoFrase, frase.esFrecuente, frase.id) { editar = null }
        }
    }
    eliminar?.let { frase ->
        AlertDialog(onDismissRequest = { eliminar = null }, title = { Text("Eliminar frase") },
            text = { Text("¿Quieres eliminar «${frase.texto}»?") },
            confirmButton = { Button(enabled = !cargando, onClick = { onEliminar(frase.id); eliminar = null }) { Text("Eliminar") } },
            dismissButton = { TextButton(onClick = { eliminar = null }) { Text("Cancelar") } })
    }
    Scaffold(topBar = { TopAppBar(title = { Text(titulo, fontSize = 18.sp) }, navigationIcon = {
        TextButton(onClick = onVolver) { Text("Volver") }
    }) }) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding).imePadding().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item { Text("Escribe una frase para comunicarte", style = MaterialTheme.typography.titleMedium) }
            item { OutlinedTextField(entrada, { entrada = it }, Modifier.fillMaxWidth().testTag("textoFrase"),
                label = { Text("Nueva frase") }, minLines = 2, supportingText = { Text("${entrada.length}/500 caracteres") }) }
            item { Button(enabled = entrada.isNotBlank() && !cargando, modifier = Modifier.fillMaxWidth(), onClick = {
                onGuardar(entrada, if (categoria == "Todas") "General" else categoria, false, null) { entrada = "" }
            }) { Text("Agregar frase") } }
            item { Text("Contexto de las frases", style = MaterialTheme.typography.titleMedium) }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    (listOf("Todas") + Validacion.categorias).chunked(2).forEach { opciones ->
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            opciones.forEach { opcion ->
                                OutlinedButton(onClick = { categoria = opcion }, modifier = Modifier.weight(1f)) {
                                    Text(if (categoria == opcion) "✓ $opcion" else opcion)
                                }
                            }
                        }
                    }
                }
            }
            item { OutlinedTextField(consulta, { consulta = it }, Modifier.fillMaxWidth(), label = { Text("Buscar frase guardada") }, singleLine = true) }
            item { Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(frecuentes, { frecuentes = it }); Text("Solo uso frecuente")
            } }
            item { Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(!fuenteGrande, { fuenteGrande = false }); Text("Normal")
                RadioButton(fuenteGrande, { fuenteGrande = true }); Text("Grande")
            } }
            item { Text("Frases rápidas (${filtradas.size})", style = MaterialTheme.typography.titleMedium) }
            if (filtradas.isEmpty()) item { Text("No hay frases para este filtro. Agrega una frase o cambia la búsqueda.") }
            items(filtradas, key = { it.id }) { frase ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(frase.categoria, style = MaterialTheme.typography.labelLarge)
                        Text(frase.texto, fontSize = if (fuenteGrande) 22.sp else 18.sp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(frase.esFrecuente, { onFrecuencia(frase.id, it) }, enabled = !cargando)
                            Text("Uso frecuente")
                        }
                        OutlinedButton(onClick = { avisoVoz = null; seleccion = frase }, modifier = Modifier.fillMaxWidth()) { Text("Mostrar y hablar") }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            TextButton(enabled = !cargando, onClick = { editar = frase }) { Text("Editar") }
                            TextButton(enabled = !cargando, onClick = { eliminar = frase }) { Text("Eliminar") }
                        }
                    }
                }
            }
            item { TextButton(onClick = { ayuda = true }) { Text("Información sobre asistencia auditiva") } }
            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun EditorFrase(frase: FraseAccesible, cargando: Boolean, onCancelar: () -> Unit, onGuardar: (String, String) -> Unit) {
    var texto by rememberSaveable(frase.id) { mutableStateOf(frase.texto) }
    var contexto by rememberSaveable(frase.id) { mutableStateOf(frase.categoria) }
    AlertDialog(onDismissRequest = onCancelar, title = { Text("Editar frase") }, text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(texto, { texto = it }, label = { Text("Texto de la frase") }, modifier = Modifier.fillMaxWidth())
            Validacion.categorias.forEach { opcion ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(contexto == opcion, { contexto = opcion }); Text(opcion)
                }
            }
        }
    }, confirmButton = { Button(enabled = !cargando, onClick = { onGuardar(texto, contexto) }) { Text("Guardar cambios") } },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar") } })
}
