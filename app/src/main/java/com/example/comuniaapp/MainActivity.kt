package com.example.comuniaapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.comuniaapp.ui.theme.ComuniaAppTheme
import com.example.comuniaapp.ui.screens.*

private enum class Destino { LOGIN, RECUPERAR, REGISTRO, INICIO, PANEL, HISTORIAL, AVISOS, RECOMENDACIONES, PERFIL }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { ComunicaApp() }
    }
}

@Composable
private fun ComunicaApp(vm: ComuniaViewModel = viewModel()) {
    val context = LocalContext.current
    val preferencias = remember { context.getSharedPreferences("comunia_accesibilidad", 0) }
    var textoGrande by rememberSaveable { mutableStateOf(preferencias.getBoolean("textoGrande", false)) }
    var contraste by rememberSaveable { mutableStateOf(preferencias.getBoolean("contraste", false)) }
    var pantalla by rememberSaveable { mutableStateOf(Destino.LOGIN.name) }
    val destino = Destino.valueOf(pantalla)
    val usuario = vm.usuario
    LaunchedEffect(usuario?.uid) { pantalla = if (usuario != null) Destino.INICIO.name else Destino.LOGIN.name }
    fun ir(valor: Destino) { vm.limpiarMensaje(); pantalla = valor.name }
    BackHandler(enabled = destino != Destino.LOGIN && destino != Destino.INICIO && !vm.cargando) {
        ir(if (usuario == null) Destino.LOGIN else Destino.INICIO)
    }
    val densidad = LocalDensity.current
    CompositionLocalProvider(LocalDensity provides Density(densidad.density, densidad.fontScale * if (textoGrande) 1.2f else 1f)) {
        ComuniaAppTheme(altoContraste = contraste) {
            Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                Column(Modifier.fillMaxSize().safeDrawingPadding()) {
                    if (BuildConfig.FIREBASE_EMULATORS) Text("Entorno de pruebas local", Modifier.padding(8.dp), style = MaterialTheme.typography.labelMedium)
                    if (vm.cargando || vm.iniciando) LinearProgressIndicator(Modifier.fillMaxWidth())
                    vm.mensaje?.let { mensaje ->
                        Card(Modifier.fillMaxWidth().padding(8.dp)) {
                            Column(Modifier.padding(12.dp)) {
                                Text(mensaje)
                                TextButton(onClick = vm::limpiarMensaje) { Text("Entendido") }
                            }
                        }
                    }
                    Box(Modifier.weight(1f)) {
                        if (vm.iniciando) Box(Modifier.fillMaxSize()) { Text("Recuperando tu sesión…", Modifier.padding(24.dp)) }
                        else when {
                            destino == Destino.RECUPERAR && usuario == null -> RecuperarPasswordScreen(vm.cargando, vm::recuperar) { ir(Destino.LOGIN) }
                            destino == Destino.REGISTRO && usuario == null -> RegistroScreen(vm.cargando, vm::registrar) { ir(Destino.LOGIN) }
                            usuario == null -> LoginScreen(vm.cargando, vm::entrar, { ir(Destino.RECUPERAR) }, { ir(Destino.REGISTRO) })
                            destino == Destino.PANEL || destino == Destino.HISTORIAL -> PanelAccesibilidadScreen(
                                frases = vm.frases, cargando = vm.cargando,
                                titulo = if (destino == Destino.HISTORIAL) "Historial de frases" else "Panel de accesibilidad",
                                onVolver = { ir(Destino.INICIO) }, onGuardar = vm::guardar,
                                onFrecuencia = vm::frecuencia, onEliminar = vm::eliminar)
                            destino == Destino.AVISOS -> PanelAvisosScreen { ir(Destino.INICIO) }
                            destino == Destino.RECOMENDACIONES -> RecomendacionesScreen { ir(Destino.INICIO) }
                            destino == Destino.PERFIL -> UsuariosRegistradosScreen(usuario) { ir(Destino.INICIO) }
                            else -> HomeScreen(usuario.nombre.ifBlank { usuario.usuario }, textoGrande, contraste, vm.cargando,
                                onTextoGrande = { textoGrande = !textoGrande; preferencias.edit().putBoolean("textoGrande", textoGrande).apply() },
                                onContraste = { contraste = !contraste; preferencias.edit().putBoolean("contraste", contraste).apply() },
                                onCerrarSesion = vm::salir, onAbrirPanel = { ir(Destino.PANEL) },
                                onAbrirHistorial = { ir(Destino.HISTORIAL) }, onAbrirAvisos = { ir(Destino.AVISOS) },
                                onAbrirRecomendaciones = { ir(Destino.RECOMENDACIONES) }, onAbrirUsuariosRegistrados = { ir(Destino.PERFIL) })
                        }
                    }
                }
            }
        }
    }
}
