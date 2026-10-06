package com.example.comuniaapp.domain

import com.example.comuniaapp.model.FraseAccesible

object Frases {
    fun filtrar(frases: List<FraseAccesible>, consulta: String, categoria: String, soloFrecuentes: Boolean): List<FraseAccesible> {
        val busqueda = consulta.trim()
        return frases.filter {
            (categoria == "Todas" || it.categoria == categoria) &&
                (busqueda.isEmpty() || it.texto.contains(busqueda, true) || it.categoria.contains(busqueda, true)) &&
                (!soloFrecuentes || it.esFrecuente)
        }
    }
}
