package com.example.comuniaapp.data

import android.content.Context
import com.example.comuniaapp.model.Usuario

class SessionStore(context: Context) {
    private val preferencias = context.getSharedPreferences("comunia_sesion", Context.MODE_PRIVATE)
    fun guardar(usuario: Usuario) {
        preferencias.edit().putString("uid", usuario.uid).putString("nombre", usuario.nombre)
            .putString("apellido", usuario.apellido).putString("usuario", usuario.usuario)
            .putString("correo", usuario.correo).apply()
    }
    fun leer(uidAutenticado: String): Usuario? {
        if (preferencias.getString("uid", null) != uidAutenticado) return null
        return Usuario(uidAutenticado, preferencias.getString("nombre", "").orEmpty(),
            preferencias.getString("apellido", "").orEmpty(), preferencias.getString("usuario", "").orEmpty(),
            preferencias.getString("correo", "").orEmpty())
    }
    fun limpiar() { preferencias.edit().clear().apply() }
}
