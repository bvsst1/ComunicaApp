package com.example.comuniaapp.data

import android.content.Context
import com.example.comuniaapp.BuildConfig
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseBackend private constructor(val auth: FirebaseAuth, val db: FirebaseFirestore) {
    companion object {
        private var instancia: FirebaseBackend? = null
        fun crear(context: Context): FirebaseBackend? {
            instancia?.let { return it }
            val app = if (BuildConfig.DEBUG && BuildConfig.FIREBASE_EMULATORS) {
                FirebaseApp.getApps(context).firstOrNull { it.name == "emuladores" }
                    ?: FirebaseApp.initializeApp(context, FirebaseOptions.Builder()
                        .setProjectId("demo-comunia").setApplicationId("1:1234567890:android:comunia")
                        .setApiKey("demo-emulator-key").build(), "emuladores")
            } else FirebaseApp.initializeApp(context) ?: return null
            val auth = FirebaseAuth.getInstance(app)
            val db = FirebaseFirestore.getInstance(app)
            if (BuildConfig.DEBUG && BuildConfig.FIREBASE_EMULATORS) {
                auth.useEmulator("10.0.2.2", 9099)
                db.useEmulator("10.0.2.2", 8080)
            }
            return FirebaseBackend(auth, db).also { instancia = it }
        }
    }
}
