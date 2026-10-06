package com.example.comuniaapp

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.comuniaapp.data.*
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Source
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import org.junit.Assert.*
import org.junit.Assume.assumeTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class FirebaseCrudTest {
    @Test fun registroSesionCrudYAislamientoEntreCuentas() = runBlocking {
        assumeTrue("Ejecutar solo contra Firebase Emulator Suite", BuildConfig.FIREBASE_EMULATORS)
        withTimeout(60000) {
            val contexto = InstrumentationRegistry.getInstrumentation().targetContext
            if (android.os.Build.VERSION.SDK_INT >= 37) {
                InstrumentationRegistry.getInstrumentation().uiAutomation.grantRuntimePermission(
                    contexto.packageName, "android.permission.ACCESS_LOCAL_NETWORK")
            }
            val backend = FirebaseBackend.crear(contexto)!!
            val sesion = SessionStore(contexto)
            val auth = AuthRepository(backend, sesion)
            val correo = "crud-${UUID.randomUUID()}@example.test"
            val cuenta = auth.registrar("Prueba", "Local", "prueba", correo, "Seguro123!")
            assertEquals(cuenta, sesion.leer(cuenta.uid))
            assertNull(sesion.leer("otro-uid"))
            val repo = ComunicacionRepository(backend, cuenta.uid)
            val coleccion = backend.db.collection("usuarios").document(cuenta.uid).collection("frases")
            repo.guardar("Necesito ayuda", "Salud", false)
            val primera = coleccion.get(Source.SERVER).await().documents.single()
            assertEquals("Necesito ayuda", primera.getString("texto"))
            repo.guardar("Escriba las indicaciones", "Salud", true, primera.id)
            assertEquals("Escriba las indicaciones", primera.reference.get(Source.SERVER).await().getString("texto"))
            repo.frecuencia(primera.id, false)
            assertEquals(false, primera.reference.get(Source.SERVER).await().getBoolean("esFrecuente"))
            auth.salir()
            assertNull(backend.auth.currentUser)
            assertNull(sesion.leer(cuenta.uid))
            val restaurada = auth.entrar(correo, "Seguro123!")
            assertEquals(cuenta.uid, restaurada.uid)
            assertEquals(1, coleccion.get(Source.SERVER).await().size())
            repo.eliminar(primera.id)
            assertTrue(coleccion.get(Source.SERVER).await().isEmpty)
            // Intenta leer un documento de la primera cuenta desde otra identidad.
            val otro = auth.registrar("Otra", "Prueba", "otra", "otro-${UUID.randomUUID()}@example.test", "Seguro123!")
            try {
                backend.db.collection("usuarios").document(cuenta.uid).get(Source.SERVER).await()
                fail("Las reglas deben impedir acceder al perfil de otra cuenta")
            } catch (e: FirebaseFirestoreException) { assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, e.code) }
            assertEquals(otro.uid, backend.auth.currentUser?.uid)
            auth.salir()
        }
    }
}
