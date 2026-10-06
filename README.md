# ComunicaApp

Aplicación Android para apoyar la comunicación de personas con discapacidad auditiva. Mantiene las pantallas de las entregas anteriores y agrega autenticación, sesión persistente, CRUD privado de frases y lectura opcional de mensajes.

## Ejecutar en Android Studio

1. Abrir esta carpeta y sincronizar Gradle. Se mantienen Kotlin 2.2.10, AGP 9.3.2, Jetpack Compose y Android mínimo API 26.
2. El cliente Android está registrado en Firebase `comunicapp-5661e`, paquete `com.example.comuniaapp`. El ZIP de entrega incluye `app/google-services.json`; al clonar Git, descargarlo desde la configuración del proyecto Firebase.
3. Authentication con correo y contraseña y Firestore en `southamerica-west1` están configurados. Las reglas publicadas corresponden a `firestore.rules`.
4. Ejecutar la variante debug en un dispositivo Android. Para validar Firebase sin datos reales, usar los emuladores de la sección siguiente.

Sin google-services.json el proyecto compila y muestra la pantalla de acceso, pero las cuentas requieren configurar Firebase. No se incluye una configuración ficticia de producción.

## Modelo y sesión

- Firebase Authentication verifica el correo y la contraseña, registra usuarios y envía la recuperación de acceso.
- `usuarios/{uid}` contiene nombre, apellido, alias y correo. `usuarios/{uid}/frases/{id}` contiene texto, categoría y marca de frecuente.
- Las reglas permiten consultar y modificar únicamente el perfil y frases del UID autenticado; validan tipo, longitud y campos admitidos.
- SharedPreferences conserva UID y datos básicos del perfil. Firebase determina si existe sesión. No se guardan contraseñas ni tokens manualmente.
- La tabla antigua de cinco usuarios evoluciona a consulta del perfil propio. El registro ya no depende de un arreglo que se pierde al cerrar la app ni expone contraseñas de otras cuentas.
- Las preferencias de texto y contraste se conservan. Las frases consultadas quedan en la caché local de Firestore; las acciones que requieren sincronización muestran estado y errores.

## Pruebas

```powershell
.\gradlew.bat testDebugUnitTest lintDebug assembleDebug
```

JUnit prueba validaciones de registro, correo, contraseñas, longitud y contexto de frases, y búsqueda combinada con categoría/frecuencia. Los informes se generan en `app/build/reports/tests/testDebugUnitTest`.

Para Firebase Emulator Suite:

```powershell
firebase emulators:start --project demo-comunia --only auth,firestore
.\gradlew.bat connectedDebugAndroidTest -PfirebaseEmulators=true
```

La variante debug con `-PfirebaseEmulators=true` conecta a `10.0.2.2:9099` y `10.0.2.2:8080` desde un emulador Android. La variante release siempre usa el proyecto real. El proyecto `demo-comunia` se reserva a pruebas locales.

En Android 17 las pruebas locales conceden `ACCESS_LOCAL_NETWORK` a la variante debug. Para ejecutarla manualmente contra los emuladores, conceder ese permiso con `adb shell pm grant com.example.comuniaapp android.permission.ACCESS_LOCAL_NETWORK`. El APK release usa internet y no solicita este permiso local.

Validación de semana 8: 11 pruebas unitarias y 5 instrumentadas aprobadas en Android 17; lint sin errores, con advertencias de mantenimiento. La integración de registro, sesión y CRUD se ejecutó contra Firebase Emulator Suite. La configuración real está incorporada en el APK firmado. Ver `docs/verificacion-semana-8.md`.

## Firmar y distribuir

```powershell
.\scripts\crear-firma.ps1
.\gradlew.bat assembleRelease
```

La clave de distribución y sus contraseñas quedan en archivos locales ignorados por Git. Hay que respaldarlos para actualizar la aplicación con la misma firma. El APK se genera en `app/build/outputs/apk/release`.

Distribución: GitHub Releases del repositorio [ComunicaApp](https://github.com/bvsst1/ComunicaApp/releases). La clave de firma se verificó con apksigner y permanece local. El ZIP de entrega reúne código, informe PDF, APK y evidencia; no contiene claves privadas ni cachés. Quedan como validación adicional las pruebas en dispositivos físicos, la entrega efectiva de correos de recuperación y la evaluación con usuarios.

## Referencias técnicas

- [Configuración Android de Firebase](https://firebase.google.com/docs/android/setup)
- [Autenticación con correo y contraseña](https://firebase.google.com/docs/auth/android/password-auth)
- [Reglas de seguridad de Firestore](https://firebase.google.com/docs/firestore/security/rules-conditions)
