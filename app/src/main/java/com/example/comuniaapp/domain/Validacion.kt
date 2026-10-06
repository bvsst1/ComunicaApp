package com.example.comuniaapp.domain

import java.util.Locale

object Validacion {
    val categorias = listOf("Salud", "Compras", "Transporte", "General")
    fun normalizarCorreo(valor: String) = valor.trim().lowercase(Locale.ROOT)
    fun correo(valor: String): String? =
        if (Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$").matches(valor.trim())) null
        else "Ingresa un correo electrónico válido."
    fun registro(nombre: String, apellido: String, usuario: String, correo: String,
                 clave: String, confirmacion: String): String? = when {
        listOf(nombre, apellido, usuario, correo, clave, confirmacion).any { it.isBlank() } -> "Completa todos los campos."
        listOf(nombre, apellido, usuario).any { it.trim().length > 80 } -> "Nombre, apellido y usuario admiten hasta 80 caracteres."
        correo(correo) != null -> correo(correo)
        clave.length < 8 -> "La contraseña debe tener al menos 8 caracteres."
        clave != confirmacion -> "Las contraseñas no coinciden."
        else -> null
    }
    fun frase(texto: String, categoria: String): String? = when {
        texto.isBlank() -> "Escribe una frase antes de guardarla."
        texto.trim().length > 500 -> "La frase admite hasta 500 caracteres."
        categoria !in categorias -> "Selecciona un contexto válido."
        else -> null
    }
}
