# Verificación de ComunicaApp para la semana 8

Fecha: 05-10-2026. Proyecto Firebase: `comunicapp-5661e`. Paquete: `com.example.comuniaapp`. Dispositivo de pruebas: emulador independiente ComuniaPruebas, Android 17 (API 37).

Se ejecutaron `connectedDebugAndroidTest testDebugUnitTest lintDebug assembleRelease` con los emuladores locales Auth y Firestore y la propiedad `firebaseEmulators=true`. La compilación terminó con BUILD SUCCESSFUL. Release siempre conserva `FIREBASE_EMULATORS=false` y `FIREBASE_CONFIGURADO=true`.

| Comprobación | Resultado |
| --- | --- |
| Validaciones de registro y frases | 6 casos JUnit aprobados |
| Filtros por texto, categoría y frecuencia | 4 casos JUnit aprobados |
| Prueba unitaria inicial | 1 caso aprobado |
| Registro, SharedPreferences, ingreso, CRUD y aislamiento entre cuentas | 1 caso instrumentado aprobado en Firebase Emulator Suite |
| Entrada de correo y contraseña, bloqueo durante carga y navegación de acceso | 3 casos instrumentados Compose aprobados |
| Contexto y paquete de Android | 1 caso instrumentado aprobado |
| Lint | 0 errores; advertencias de mantenimiento y estilo |
| APK release | Firma v2 válida; certificado RSA de 3072 bits |

SHA-256 del APK:

`6700392dc1eec2b48fb50fb85a5019302a3229131d0e243f8e8cca7bd087e5bf`

SHA-256 del certificado:

`bc78a6d12b4c506574c1a03dcd61ec4c1a26c24f3a04cc8113c0247b06ebca53`

Las pruebas de integración usan cuentas sintéticas en un backend local. Authentication por correo y contraseña está habilitado en el proyecto real; Firestore está en Santiago y tiene publicadas las reglas por propietario del código fuente. No se presenta la ejecución local como prueba del envío de correo ni como una medición de rendimiento en un teléfono físico.

Se corrigieron dos fallos de la primera ejecución instrumentada: permiso de acceso a red local de Android 17 para el backend de pruebas y selección del nodo editable de contraseña en Compose. La ejecución posterior aprobó los 5 casos.

Las evidencias XML de JUnit, resultados instrumentados y reporte HTML de lint se incluyen en el ZIP. Para repetir pruebas desde Android Studio se puede usar el build normal en `app/build`; la carpeta temporal en D utilizada durante esta sesión fue una solución al espacio disponible en C.

Validaciones adicionales: versiones Android anteriores, equipos físicos de gama baja, accesibilidad con usuarios, interrupción y recuperación de conectividad y recepción real del correo de recuperación. El código permite estas pruebas; no se han inventado resultados.
