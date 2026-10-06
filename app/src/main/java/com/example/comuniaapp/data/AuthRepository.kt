package com.example.comuniaapp.data

import com.example.comuniaapp.domain.Validacion
import com.example.comuniaapp.model.Usuario
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await

class AuthRepository(private val backend: FirebaseBackend, private val sesion: SessionStore) {
    suspend fun entrar(correo: String, clave: String): Usuario {
        backend.auth.signInWithEmailAndPassword(Validacion.normalizarCorreo(correo), clave).await()
        return perfilActual()
    }
    suspend fun registrar(nombre: String, apellido: String, usuario: String, correo: String, clave: String): Usuario {
        val cuenta = backend.auth.createUserWithEmailAndPassword(Validacion.normalizarCorreo(correo), clave).await().user
            ?: error("No se pudo crear la cuenta.")
        val perfil = Usuario(cuenta.uid, nombre.trim(), apellido.trim(), usuario.trim(), Validacion.normalizarCorreo(correo))
        try {
            backend.db.collection("usuarios").document(cuenta.uid).set(mapOf(
                "nombre" to perfil.nombre, "apellido" to perfil.apellido,
                "usuario" to perfil.usuario, "correo" to perfil.correo)).await()
            cuenta.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(perfil.nombre).build()).await()
        } catch (e: Exception) {
            runCatching { cuenta.delete().await() }
            backend.auth.signOut(); sesion.limpiar(); throw e
        }
        sesion.guardar(perfil)
        return perfil
    }
    suspend fun perfilActual(): Usuario {
        val cuenta = backend.auth.currentUser ?: error("Inicia sesión para continuar.")
        val datos = backend.db.collection("usuarios").document(cuenta.uid).get().await()
        val perfil = Usuario(cuenta.uid, datos.getString("nombre") ?: cuenta.displayName.orEmpty(),
            datos.getString("apellido").orEmpty(), datos.getString("usuario").orEmpty(), cuenta.email.orEmpty())
        sesion.guardar(perfil)
        return perfil
    }
    suspend fun recuperar(correo: String) {
        backend.auth.sendPasswordResetEmail(Validacion.normalizarCorreo(correo)).await()
    }
    fun salir() { backend.auth.signOut(); sesion.limpiar() }
}
