package com.example.comuniaapp.data

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.comuniaapp.model.FraseAccesible

class ComunicacionRepository {
    val categorias: List<String> = listOf("Todas", "Salud", "Compras", "Transporte", "General")

    private val frasesIniciales: List<FraseAccesible> = listOf(
        FraseAccesible(1, "Salud", "Necesito hablar con el médico, por favor.", true),
        FraseAccesible(2, "Salud", "¿Puede escribir las indicaciones?", true),
        FraseAccesible(3, "Compras", "¿Cuánto cuesta este producto?", true),
        FraseAccesible(4, "Compras", "Necesito una bolsa, por favor.", false),
        FraseAccesible(5, "Transporte", "¿Dónde está la parada de autobús?", true),
        FraseAccesible(6, "Transporte", "Avíseme cuando lleguemos, por favor.", false),
        FraseAccesible(7, "General", "No escucho. ¿Puede escribirlo?", true),
        FraseAccesible(8, "General", "Gracias por su ayuda.", false)
    )

    val frases: SnapshotStateList<FraseAccesible> = mutableStateListOf(*frasesIniciales.toTypedArray())

    private var siguienteId = (frasesIniciales.maxOfOrNull { it.id } ?: 0) + 1

    fun buscarFrases(
        consulta: String = "",
        categoria: String = "Todas",
        soloFrecuentes: Boolean = false
    ): List<FraseAccesible> {
        val textoBuscado = consulta.trim()
        return frases.filter { frase ->
            val coincideCategoria = categoria == "Todas" || frase.categoria == categoria
            val coincideBusqueda = textoBuscado.isBlank() ||
                frase.texto.contains(textoBuscado, ignoreCase = true) ||
                frase.categoria.contains(textoBuscado, ignoreCase = true)
            val coincideFrecuencia = !soloFrecuentes || frase.esFrecuente
            coincideCategoria && coincideBusqueda && coincideFrecuencia
        }
    }

    fun agregarFrase(texto: String, categoria: String = "General", esFrecuente: Boolean = false): Boolean {
        val textoLimpio = texto.trim()
        if (textoLimpio.isBlank()) return false

        frases.add(FraseAccesible(siguienteId++, categoria, textoLimpio, esFrecuente))
        return true
    }

    fun actualizarFrecuencia(id: Int, esFrecuente: Boolean) {
        val indice = frases.indexOfFirst { it.id == id }
        if (indice >= 0) {
            frases[indice] = frases[indice].copy(esFrecuente = esFrecuente)
        }
    }
}
