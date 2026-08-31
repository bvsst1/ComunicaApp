package com.example.comuniaapp.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.comuniaapp.data.ComunicacionRepository
import com.example.comuniaapp.model.FraseAccesible
import kotlinx.coroutines.launch

private enum class TamanoFuente(val etiqueta: String, val escala: Float) {
    Normal("Normal", 1f),
    Grande("Grande", 1.25f)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PanelAccesibilidadScreen(
    onVolver: () -> Unit,
    repository: ComunicacionRepository = remember { ComunicacionRepository() }
) {
    var entrada by rememberSaveable { mutableStateOf("") }
    var categoria by rememberSaveable { mutableStateOf("Todas") }
    var menuAbierto by remember { mutableStateOf(false) }
    var soloFrecuentes by rememberSaveable { mutableStateOf(false) }
    var tamanoFuente by rememberSaveable { mutableStateOf(TamanoFuente.Normal.name) }
    var mostrarAyuda by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val fuente = TamanoFuente.valueOf(tamanoFuente)
    val frases = repository.buscarFrases(entrada, categoria, soloFrecuentes)

    if (mostrarAyuda) {
        AlertDialog(
            onDismissRequest = { mostrarAyuda = false },
            title = { Text("Asistencia auditiva") },
            text = {
                Text(
                    "Puedes solicitar que la información se escriba, usar audífonos o pedir apoyo a una persona de confianza. " +
                        "ComunicaApp facilita el intercambio visual de mensajes."
                )
            },
            confirmButton = {
                Button(onClick = { mostrarAyuda = false }) { Text("Entendido") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel de accesibilidad", fontSize = 22.sp * fuente.escala) },
                navigationIcon = {
                    OutlinedButton(onClick = onVolver, modifier = Modifier.padding(start = 8.dp)) {
                        Text("Volver", fontSize = 14.sp * fuente.escala)
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    "Encuentra una frase o crea un mensaje nuevo",
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 18.sp * fuente.escala,
                    fontWeight = FontWeight.Bold
                )
            }
            item {
                OutlinedTextField(
                    value = entrada,
                    onValueChange = { entrada = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { contentDescription = "Buscar o escribir una frase" },
                    label = { Text("Buscar o escribir nueva frase") },
                    placeholder = { Text("Ej: Necesito ayuda") },
                    singleLine = false,
                    minLines = 2
                )
            }
            item {
                ExposedDropdownMenuBox(
                    expanded = menuAbierto,
                    onExpandedChange = { menuAbierto = !menuAbierto }
                ) {
                    OutlinedTextField(
                        value = categoria,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        label = { Text("Contexto") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuAbierto)
                        }
                    )
                    ExposedDropdownMenu(
                        expanded = menuAbierto,
                        onDismissRequest = { menuAbierto = false }
                    ) {
                        repository.categorias.forEach { opcion ->
                            DropdownMenuItem(
                                text = { Text(opcion) },
                                onClick = {
                                    categoria = opcion
                                    menuAbierto = false
                                }
                            )
                        }
                    }
                }
            }
            item {
                Button(
                    onClick = {
                        val categoriaNueva = if (categoria == "Todas") "General" else categoria
                        if (repository.agregarFrase(entrada, categoriaNueva)) {
                            entrada = ""
                            scope.launch { snackbarHostState.showSnackbar("Frase agregada") }
                        } else {
                            scope.launch { snackbarHostState.showSnackbar("Escribe una frase antes de agregarla") }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    enabled = entrada.isNotBlank()
                ) {
                    Text("Agregar frase", fontSize = 17.sp * fuente.escala)
                }
            }
            item {
                Text(
                    "Selecciona una categoría",
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 18.sp * fuente.escala
                )
            }
            item {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(132.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    userScrollEnabled = false
                ) {
                    items(repository.categorias.drop(1)) { opcion ->
                        CategoryButton(
                            categoria = opcion,
                            seleccionada = categoria == opcion,
                            fuente = fuente,
                            onClick = { categoria = opcion }
                        )
                    }
                }
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Solo uso frecuente", fontSize = 17.sp * fuente.escala)
                    Checkbox(
                        checked = soloFrecuentes,
                        onCheckedChange = { soloFrecuentes = it },
                        modifier = Modifier.semantics {
                            contentDescription = "Mostrar solo frases de uso frecuente"
                        }
                    )
                }
            }
            item {
                Text(
                    "Frases rápidas (${frases.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 18.sp * fuente.escala
                )
            }
            item {
                Box(modifier = Modifier.fillMaxWidth().height(300.dp)) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(frases, key = { it.id }) { frase ->
                            FraseCard(
                                frase = frase,
                                fuente = fuente,
                                onFrecuenciaChanged = { marcada ->
                                    repository.actualizarFrecuencia(frase.id, marcada)
                                }
                            )
                        }
                    }
                }
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Tamaño de fuente", fontSize = 17.sp * fuente.escala)
                    TamanoFuente.values().forEach { opcion ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { tamanoFuente = opcion.name }
                                .padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = tamanoFuente == opcion.name,
                                onClick = { tamanoFuente = opcion.name }
                            )
                            Text(opcion.etiqueta, fontSize = 16.sp * fuente.escala)
                        }
                    }
                }
            }
            item {
                ClickableText(
                    text = AnnotatedString("Información sobre asistencia auditiva"),
                    onClick = { mostrarAyuda = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .semantics { contentDescription = "Abrir información de asistencia auditiva" },
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 16.sp * fuente.escala
                    )
                )
            }
            item { Spacer(modifier = Modifier.size(8.dp)) }
        }
    }
}

@Composable
private fun CategoryButton(
    categoria: String,
    seleccionada: Boolean,
    fuente: TamanoFuente,
    onClick: () -> Unit
) {
    if (seleccionada) {
        Button(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
            Text(categoria, fontSize = 15.sp * fuente.escala)
        }
    } else {
        OutlinedButton(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
            Text(categoria, fontSize = 15.sp * fuente.escala)
        }
    }
}

@Composable
private fun FraseCard(
    frase: FraseAccesible,
    fuente: TamanoFuente,
    onFrecuenciaChanged: (Boolean) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    frase.categoria,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(frase.texto, fontSize = 17.sp * fuente.escala)
            }
            Checkbox(
                checked = frase.esFrecuente,
                onCheckedChange = onFrecuenciaChanged,
                modifier = Modifier.semantics {
                    contentDescription = "Marcar frase como de uso frecuente"
                }
            )
        }
    }
}
