package com.example.comuniaapp

import android.app.Application
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.comuniaapp.data.*
import com.example.comuniaapp.domain.Validacion
import com.example.comuniaapp.model.FraseAccesible
import com.example.comuniaapp.model.Usuario
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

class ComuniaViewModel(application: Application) : AndroidViewModel(application) {
    private val backend = FirebaseBackend.crear(application)
    private val sesion = SessionStore(application)
    private val auth = backend?.let { AuthRepository(it, sesion) }
    private var repo: ComunicacionRepository? = null
    private var listener: ListenerRegistration? = null
    var usuario by mutableStateOf<Usuario?>(null); private set
    var frases by mutableStateOf(emptyList<FraseAccesible>()); private set
    var cargando by mutableStateOf(false); private set
    var iniciando by mutableStateOf(true); private set
    var mensaje by mutableStateOf<String?>(null); private set
    val configurado get() = backend != null

    init {
        val cuenta = backend?.auth?.currentUser
        if (cuenta == null) { sesion.limpiar(); iniciando = false }
        else {
            sesion.leer(cuenta.uid)?.let { activar(it) }
            viewModelScope.launch {
                try { activar(withTimeout(15000) { auth!!.perfilActual() }) }
                catch (e: Exception) { mensaje = mensajeError(e) }
                finally { iniciando = false }
            }
        }
    }
    private fun activar(perfil: Usuario) {
        listener?.remove()
        usuario = perfil
        frases = emptyList()
        repo = ComunicacionRepository(backend!!, perfil.uid)
        listener = repo!!.observar({ frases = it }, { mensaje = mensajeError(it) })
    }
    private fun conectado(): Boolean {
        val red = getApplication<Application>().getSystemService(ConnectivityManager::class.java)
        return red.getNetworkCapabilities(red.activeNetwork)?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }
    private fun ejecutar(accion: suspend () -> Unit) {
        if (cargando) return
        if (!configurado) { mensaje = "El servicio de cuentas aún no está disponible. Contacta al responsable de la aplicación."; return }
        if (!conectado()) { mensaje = "Sin conexión. Puedes consultar tus frases; conecta a internet para guardar cambios o gestionar tu cuenta."; return }
        cargando = true; mensaje = null
        viewModelScope.launch {
            try { withTimeout(20000) { accion() } }
            catch (e: TimeoutCancellationException) { mensaje = "El servicio tardó demasiado. Revisa tu conexión e inténtalo de nuevo." }
            catch (e: CancellationException) { throw e }
            catch (e: Exception) { mensaje = mensajeError(e) }
            finally { cargando = false }
        }
    }
    fun entrar(correo: String, clave: String) {
        val error = Validacion.correo(correo) ?: if (clave.isBlank()) "Ingresa tu contraseña." else null
        if (error != null) { mensaje = error; return }
        ejecutar { activar(auth!!.entrar(correo, clave)) }
    }
    fun registrar(nombre: String, apellido: String, usuario: String, correo: String, clave: String, confirmar: String) {
        val error = Validacion.registro(nombre, apellido, usuario, correo, clave, confirmar)
        if (error != null) { mensaje = error; return }
        ejecutar {
            val perfil = auth!!.registrar(nombre, apellido, usuario, correo, clave)
            activar(perfil)
            repo!!.crearFrasesIniciales()
        }
    }
    fun recuperar(correo: String) {
        Validacion.correo(correo)?.let { mensaje = it; return }
        ejecutar {
            auth!!.recuperar(correo)
            mensaje = "Si el correo tiene una cuenta, recibirás instrucciones para recuperar la contraseña."
        }
    }
    fun guardar(texto: String, categoria: String, frecuente: Boolean, id: String? = null, alGuardar: () -> Unit = {}) {
        Validacion.frase(texto, categoria)?.let { mensaje = it; return }
        ejecutar { (repo ?: error("Inicia sesión.")).guardar(texto, categoria, frecuente, id); alGuardar(); mensaje = "Frase guardada." }
    }
    fun frecuencia(id: String, frecuente: Boolean) = ejecutar { repo!!.frecuencia(id, frecuente) }
    fun eliminar(id: String) = ejecutar { repo!!.eliminar(id); mensaje = "Frase eliminada." }
    fun salir() {
        if (cargando) return
        listener?.remove(); listener = null; repo = null
        auth?.salir(); usuario = null; frases = emptyList(); mensaje = null
    }
    fun limpiarMensaje() { mensaje = null }
    override fun onCleared() { listener?.remove(); super.onCleared() }
    private fun mensajeError(e: Exception): String = when (e) {
        is FirebaseAuthException -> when (e.errorCode) {
            "ERROR_EMAIL_ALREADY_IN_USE" -> "Este correo ya tiene una cuenta. Inicia sesión o recupera tu contraseña."
            "ERROR_WEAK_PASSWORD" -> "Elige una contraseña más segura."
            "ERROR_TOO_MANY_REQUESTS" -> "Demasiados intentos. Espera unos minutos y vuelve a intentar."
            else -> "No se pudo acceder a la cuenta. Revisa el correo, la contraseña y tu conexión."
        }
        is FirebaseFirestoreException -> "No se pudo acceder a tus datos. Revisa tu conexión y vuelve a intentar."
        else -> "No se pudo completar la operación. Revisa tu conexión e inténtalo de nuevo."
    }
}
