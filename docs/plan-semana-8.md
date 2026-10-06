# Preparación de ComunicaApp para la semana 8

Objetivo: continuar la aplicación de accesibilidad existente y cubrir los nueve criterios de la pauta DSY2204. La referencia principal es la semana 8; las menciones de recetas en semana 6 y 7 pertenecen al ejemplo de la asignatura.

## Implementación y verificación

- [ ] Integrar Firebase Authentication y Firestore sin almacenar contraseñas en la aplicación. Gestionar sesión con SharedPreferences y validar la sesión con Firebase.
- [ ] Mantener Login, Registro, Recuperación, Home, Panel de accesibilidad, Avisos, Recomendaciones y consulta del perfil propio. Agregar consulta, creación, edición y eliminación de frases por cuenta.
- [ ] Mantener categorías, búsqueda, frecuentes, tamaño de texto y contraste. Usar mensajes visuales, diseños desplazables y lectura opcional de frases con TTS.
- [ ] Probar validaciones, filtrado, aislamiento de datos y CRUD con JUnit y pruebas instrumentadas donde exista un dispositivo disponible.
- [ ] Compilar, configurar firma de distribución sin incluir claves en Git y verificar el certificado del APK.
- [ ] Completar una copia del formato Word con los datos y los cinco riesgos recuperados de las entregas anteriores. Exportar a PDF y revisar las páginas.
- [ ] Empaquetar código fuente, informe PDF, APK y resultados verificables en un solo ZIP.

## Dependencias externas

El archivo google-services.json, la habilitación de Authentication y Firestore y la plataforma de publicación deben corresponder al proyecto real del estudiante. La integración puede prepararse y probarse con los emuladores locales de Firebase mientras se reciben esos datos. La publicación y las pruebas en dispositivos físicos deben registrarse según la evidencia real.

## Casos que deben comprobarse

Campos vacíos y correo inválido; contraseñas distintas o débiles; pérdida de conectividad; cierre y restauración de sesión; frases vacías o demasiado largas; actualización y eliminación del elemento correcto; separación de datos entre cuentas; navegación atrás y uso en pantallas pequeñas.
