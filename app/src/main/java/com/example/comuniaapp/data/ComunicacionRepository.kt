package com.example.comuniaapp.data

import com.example.comuniaapp.domain.Validacion
import com.example.comuniaapp.model.FraseAccesible
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.tasks.await

class ComunicacionRepository(private val backend: FirebaseBackend, private val uid: String) {
    private val coleccion = backend.db.collection("usuarios").document(uid).collection("frases")
    private fun comprobarSesion() { check(backend.auth.currentUser?.uid == uid) { "La sesión cambió. Vuelve a iniciar sesión." } }
    fun observar(onCambio: (List<FraseAccesible>) -> Unit, onError: (Exception) -> Unit): ListenerRegistration {
        comprobarSesion()
        return coleccion.addSnapshotListener { datos, error ->
            if (error != null) onError(error)
            else onCambio(datos?.documents.orEmpty().map {
                FraseAccesible(it.id, it.getString("categoria") ?: "General", it.getString("texto").orEmpty(),
                    it.getBoolean("esFrecuente") ?: false)
            }.sortedWith(compareBy({ it.categoria }, { it.texto })))
        }
    }
    suspend fun guardar(texto: String, categoria: String, esFrecuente: Boolean, id: String? = null) {
        comprobarSesion()
        require(Validacion.frase(texto, categoria) == null) { Validacion.frase(texto, categoria).orEmpty() }
        val referencia = if (id == null) coleccion.document() else coleccion.document(id)
        val campos = mapOf("texto" to texto.trim(), "categoria" to categoria, "esFrecuente" to esFrecuente)
        if (id == null) referencia.set(campos).await() else referencia.update(campos).await()
    }
    suspend fun frecuencia(id: String, frecuente: Boolean) {
        comprobarSesion(); coleccion.document(id).update("esFrecuente", frecuente).await()
    }
    suspend fun eliminar(id: String) { comprobarSesion(); coleccion.document(id).delete().await() }
    suspend fun crearFrasesIniciales() {
        comprobarSesion()
        val iniciales = listOf(
            FraseAccesible("inicial-1", "Salud", "Necesito hablar con el médico, por favor.", true),
            FraseAccesible("inicial-2", "Salud", "¿Puede escribir las indicaciones?", true),
            FraseAccesible("inicial-3", "Compras", "¿Cuánto cuesta este producto?", true),
            FraseAccesible("inicial-4", "Compras", "Necesito una bolsa, por favor.", false),
            FraseAccesible("inicial-5", "Transporte", "¿Dónde está la parada de autobús?", true),
            FraseAccesible("inicial-6", "Transporte", "Avíseme cuando lleguemos, por favor.", false),
            FraseAccesible("inicial-7", "General", "No escucho. ¿Puede escribirlo?", true),
            FraseAccesible("inicial-8", "General", "Gracias por su ayuda.", false))
        val lote = backend.db.batch()
        iniciales.forEach { lote.set(coleccion.document(it.id), mapOf("texto" to it.texto, "categoria" to it.categoria, "esFrecuente" to it.esFrecuente)) }
        lote.commit().await()
    }
}
