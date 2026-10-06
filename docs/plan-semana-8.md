# Preparación de ComunicaApp para la semana 8

Objetivo: continuar la aplicación de accesibilidad existente y cubrir los nueve criterios de la pauta DSY2204. La referencia principal es la semana 8; las menciones de recetas en semana 6 y 7 pertenecen al ejemplo de la asignatura.

## Implementación y verificación

- [x] Integrar Firebase Authentication y Firestore sin almacenar contraseñas en la aplicación. Gestionar sesión con SharedPreferences y validar la sesión con Firebase.
- [x] Mantener Login, Registro, Recuperación, Home, Panel de accesibilidad, Avisos, Recomendaciones y consulta del perfil propio. Agregar consulta, creación, edición y eliminación de frases por cuenta.
- [x] Mantener categorías, búsqueda, frecuentes, tamaño de texto y contraste. Usar mensajes visuales, diseños desplazables y lectura opcional de frases con TTS.
- [x] Probar validaciones, filtrado, aislamiento de datos y CRUD con JUnit y pruebas instrumentadas donde exista un dispositivo disponible.
- [x] Compilar, configurar firma de distribución sin incluir claves en Git y verificar el certificado del APK.
- [x] Completar una copia del formato Word con los datos y los cinco riesgos recuperados de las entregas anteriores. Exportar a PDF y revisar las páginas.
- [x] Empaquetar código fuente, informe PDF, APK y resultados verificables en un solo ZIP.

## Dependencias externas

Firebase real comunicapp-5661e está configurado y sus reglas están publicadas. El usuario seleccionó GitHub Releases y autorizó publicar código y APK. Las pruebas automáticas de integración se ejecutaron con los emuladores locales de Firebase. El usuario aportó capturas del APK publicado, ejecutado en un emulador Android con Firebase real, y confirmó el cambio de contraseña, el ingreso posterior, la eliminación y la conservación de sesión y frases al reabrir. El informe añade la explicación del código, resultados detallados, el mockup de Historial y la evidencia manual. Las pruebas de pérdida de red, versiones anteriores, rendimiento en gama baja y evaluación con usuarios con discapacidad auditiva siguen como validaciones adicionales.

## Casos que deben comprobarse

Campos vacíos y correo inválido; contraseñas distintas o débiles; pérdida de conectividad; cierre y restauración de sesión; frases vacías o demasiado largas; actualización y eliminación del elemento correcto; separación de datos entre cuentas; navegación atrás y uso en pantallas pequeñas.

