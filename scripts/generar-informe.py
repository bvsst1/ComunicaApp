"""Completa una copia del formato de respuesta DSY2204 con evidencia verificable."""
from pathlib import Path
from copy import deepcopy
import json
import textwrap
import xml.etree.ElementTree as ET
from docx import Document
from docx.shared import Pt, Inches, RGBColor
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.oxml import OxmlElement
from docx.oxml.ns import qn
from PIL import Image, ImageDraw, ImageFont

ROOT = Path(__file__).resolve().parents[1]
REF = Path(r'C:\Users\bastian\Desktop\duoc uc\Desarrollo de aplicaciones moviles\semana 8\S8_ Formato de respuesta_A.docx')
OUT = ROOT / 'entrega'
QA = ROOT / '.qa'
OUT.mkdir(exist_ok=True)
QA.mkdir(exist_ok=True)
evidence = json.loads((QA / 'evidencias.json').read_text(encoding='utf8')) if (QA / 'evidencias.json').exists() else {}
build = Path(evidence.get('build', str(ROOT / 'app' / 'build')))
tests = []
for p in (build / 'test-results' / 'testDebugUnitTest').glob('TEST-*.xml'):
    node = ET.parse(p).getroot()
    tests.append((node.attrib['name'].rsplit('.', 1)[-1], int(node.attrib['tests']), int(node.attrib['failures']), int(node.attrib['errors']), node.attrib['time']))
total = sum(t[1] for t in tests)
failed = sum(t[2] + t[3] for t in tests)
has_config = (ROOT / 'app' / 'google-services.json').exists()
firebase_status = evidence.get('firebase', 'Cliente Android registrado en comunicapp-5661e; configuración de los servicios en curso.')
apk_status = evidence.get('apk', 'Compilación de distribución en verificación.')
integration_status = evidence.get('integracion', 'Pruebas instrumentadas de Firebase y de interfaz preparadas; ejecución pendiente.')
publication_status = evidence.get('publicacion', 'Publicación prevista en GitHub Releases; el enlace de descarga se incorporará al completar la publicación.')
revision_status = evidence.get('revision', 'Código actualizado en la rama codex/semana8 del repositorio https://github.com/bvsst1/ComunicaApp.')

FONT = Path(r'C:\Windows\Fonts\arial.ttf')
BOLD = Path(r'C:\Windows\Fonts\arialbd.ttf')
def font(size=30, bold=False): return ImageFont.truetype(str(BOLD if bold else FONT), size)
def wireframe(path, screens):
    width = 650 if len(screens) == 1 else 1300
    height = 40 + ((len(screens) + 1) // 2) * 840
    img = Image.new('RGB', (width, height), 'white'); draw = ImageDraw.Draw(img)
    for i, (title, lines) in enumerate(screens):
        x = 45 + (i % 2) * 650; y = 40 + (i // 2) * 840
        draw.rounded_rectangle((x, y, x+560, y+780), radius=24, outline='#303030', width=4)
        draw.rounded_rectangle((x+15, y+15, x+545, y+110), radius=14, fill='#eeeeee')
        draw.text((x+34, y+36), 'ComunicaApp', font=font(34, True), fill='black')
        draw.text((x+34, y+127), title, font=font(29, True), fill='black')
        ypos=y+190
        for line in lines:
            for text in textwrap.wrap(line, 31):
                draw.text((x+34, ypos), text, font=font(28), fill='black'); ypos += 42
            ypos += 22
    img.save(path)

wireframe(QA/'mockup-acceso.png', [
    ('Inicio de sesión', ['Correo electrónico', '[ ana@ejemplo.cl           ]', 'Contraseña', '[ ********                ]', '[ Iniciar sesión ]', '[ Olvidé mi contraseña ]', '[ Crear cuenta ]']),
    ('Registro', ['Nombre y apellido', '[ Ana ] [ Pérez ]', 'Usuario y correo', '[ ana ] [ ana@ejemplo.cl ]', 'Contraseña y confirmación', '[ ******** ] [ ******** ]', '[ Crear cuenta ]']),
    ('Recuperar contraseña', ['Correo de la cuenta', '[ ana@ejemplo.cl           ]', '[ Enviar instrucciones ]', 'Confirmación visual del envío', '[ Volver ]']),
    ('Inicio', ['¡Hola, Ana!', '[ Texto grande ]', '[ Alto contraste ]', '[ Escribir para comunicar ]', '[ Noticias y avisos ]', '[ Historial de frases ]', '[ Recomendaciones ]', '[ Mi perfil ]'])])
wireframe(QA/'mockup-funciones.png', [
    ('Panel de accesibilidad', ['Nueva frase', '[ Necesito ayuda          ]', '[ Agregar frase ]', 'Todas  Salud  Compras', 'Transporte  General', '[ Buscar frase guardada ]', 'Solo uso frecuente [ ]', '[ Mostrar y hablar ]', '[ Editar ] [ Eliminar ]']),
    ('Noticias y avisos', ['[ Volver ]  [ A ] [ A+ ]', 'Información accesible', 'Título del aviso', 'Fecha y prioridad', 'Contenido del aviso', 'Ejemplos informativos']),
    ('Recomendaciones', ['[ Volver ]', 'Lunes: anticipa el tema', 'Martes: usa frases cortas', 'Miércoles: confirma el mensaje', 'Jueves: usa apoyo visual', 'Viernes: da tiempo a responder']),
    ('Mi perfil', ['[ Volver ]', 'Nombre: Ana Pérez', 'Usuario: ana', 'Correo: ana@ejemplo.cl', 'Datos de la cuenta activa', 'Frases y perfil privados'])])
wireframe(QA/'mockup-historial.png', [
    ('Historial de frases', ['[ Volver ]', '[ Nueva frase ] [ Guardar ]', 'Contexto: Todas  Salud', 'Compras  Transporte  General', '[ Buscar frase guardada ]', 'Solo uso frecuente [ ]', 'General: Necesito ayuda', '[ Mostrar y hablar ]', '[ Editar ] [ Eliminar ]'])])

img=Image.new('RGB',(1300,850),'white'); draw=ImageDraw.Draw(img)
draw.rounded_rectangle((390,20,910,110),radius=14,fill='#eeeeee',outline='black',width=2)
draw.text((472,47),'ComunicaApp',font=font(38,True),fill='black')
phases=[('1 Gestión', ['1.1 Requisitos', '1.2 Informe y Git']),('2 Diseño', ['2.1 Mockups', '2.2 Accesibilidad']),('3 Desarrollo', ['3.1 Auth y sesión', '3.2 CRUD de frases', '3.3 Interfaz y TTS']),('4 Cierre', ['4.1 Pruebas', '4.2 APK y firma', '4.3 Publicación', '4.4 PDF y ZIP'])]
draw.line((650,110,650,150),fill='black',width=3); draw.line((160,150,1140,150),fill='black',width=3)
for i,(title,children) in enumerate(phases):
    x=18+i*325
    draw.line((x+145,150,x+145,195),fill='black',width=3)
    draw.rounded_rectangle((x,195,x+290,280),radius=12,fill='#eeeeee',outline='black',width=2)
    draw.text((x+20,222),title,font=font(29,True),fill='black')
    for j,child in enumerate(children):
        y=330+j*106
        draw.line((x+145,y-50,x+145,y),fill='black',width=2)
        draw.rounded_rectangle((x,y,x+290,y+78),radius=10,outline='#555555',width=2)
        draw.text((x+12,y+25),child,font=font(25),fill='black')
img.save(QA/'edt.png')

d=Document(REF)
def format_p(p, bold=False):
    p.alignment=WD_ALIGN_PARAGRAPH.LEFT
    pf=p.paragraph_format
    pf.line_spacing=1.5; pf.space_before=Pt(0); pf.space_after=Pt(6)
    pf.keep_together=True; pf.keep_with_next=False
    for run in p.runs:
        run.font.name='Arial'; run.font.size=Pt(12); run.font.color.rgb=RGBColor(0,0,0)
        if bold: run.font.bold=True
    return p
def cell_text(cell, paragraphs):
    cell.text=''
    for i, text in enumerate(paragraphs):
        p=cell.paragraphs[0] if i==0 else cell.add_paragraph()
        p.add_run(text); format_p(p)
def after_p(anchor, text, bold=False):
    el=OxmlElement('w:p'); anchor._p.addnext(el)
    from docx.text.paragraph import Paragraph
    p=Paragraph(el, anchor._parent); p.add_run(text); return format_p(p,bold)
def after_table(table, text, bold=False):
    el=OxmlElement('w:p'); table._tbl.addnext(el)
    from docx.text.paragraph import Paragraph
    p=Paragraph(el,d._body); p.add_run(text); return format_p(p,bold)
for table in d.tables:
    for row in table.rows:
        for h in row._tr.xpath('./w:trPr/w:trHeight'): h.getparent().remove(h)
        for cell in row.cells:
            for p in cell.paragraphs:
                p.paragraph_format.keep_with_next=False
            for border in cell._tc.xpath('./w:tcPr/w:tcBorders/*'):
                border.set(qn('w:color'),'D9D9D9')

# La portada y las instrucciones permanecen en el formato entregado.
cell_text(d.tables[0].cell(0,0), ['Nombre estudiante: Bastian Ortiz'])
cell_text(d.tables[0].cell(1,0), ['Asignatura: Desarrollo de aplicaciones móviles'])
cell_text(d.tables[0].cell(1,1), ['Carrera: Ingeniería en desarrollo de software'])
cell_text(d.tables[0].cell(2,0), ['Profesor: Miguel Puebla'])
cell_text(d.tables[0].cell(2,1), ['Fecha: 05-10-2026'])
history=[('1.0','20/08/2026','Alcance inicial y cinco riesgos técnicos.','Bastian Ortiz'),('2.0','13/09/2026','Vistas Compose y componentes UI.','Bastian Ortiz'),('3.0','05/10/2026','Backend, CRUD, sesión, pruebas y preparación de distribución.','Bastian Ortiz')]
for i,record in enumerate(history,1):
    for j,value in enumerate(record): cell_text(d.tables[1].cell(i,j),[value])
info=['Duoc UC','001','ComunicaApp','20/08/2026','En cierre técnico','Proyecto académico','Miguel Puebla']
for i,value in enumerate(info): cell_text(d.tables[2].cell(i,1),[value])

cell_text(d.tables[3].cell(1,0),[
    'ComunicaApp facilita la comunicación cotidiana de personas con discapacidad sensorial auditiva mediante mensajes escritos, frases rápidas y apoyos visuales. La aplicación transforma el teléfono en una herramienta para expresar necesidades en contextos de salud, compras, transporte y uso general.',
    'Esta entrega mantiene el frontend Kotlin con Jetpack Compose de las semanas anteriores e integra Firebase Authentication para las cuentas y Firestore para el perfil y las frases privadas. La sesión conserva datos básicos en SharedPreferences y las frases admiten creación, consulta, modificación y eliminación.',
    'El trabajo incorpora validaciones, estados de carga, errores visuales, texto grande, contraste, pruebas JUnit y preparación del APK de distribución. La consulta del antiguo arreglo de usuarios se actualiza a un perfil privado; las contraseñas dejan de almacenarse y mostrarse en la interfaz.',
    'La lectura de mensajes con TextToSpeech es una acción voluntaria. Las confirmaciones de la aplicación se mantienen visuales. No se incorporan chat entre usuarios ni reconocimiento ambiental de sonido.'])

cell_text(d.tables[4].cell(1,0),['Mockups de las nueve vistas de la aplicación. El flujo de acceso conduce al inicio y desde allí al panel, historial, avisos, recomendaciones y perfil.'])
for name in ['mockup-acceso.png','mockup-funciones.png']:
    p=d.tables[4].cell(1,0).add_paragraph(); p.add_run().add_picture(str(QA/name),width=Inches(5.25)); format_p(p)
p=d.tables[4].cell(1,0).add_paragraph(); p.add_run('Historial de frases'); format_p(p,True)
p=d.tables[4].cell(1,0).add_paragraph(); p.add_run().add_picture(str(QA/'mockup-historial.png'),width=Inches(2.9)); format_p(p)
p=d.tables[4].cell(1,0).add_paragraph(); p.add_run('El historial reutiliza el panel de frases con su propio título y acceso desde Inicio. Permite consultar y administrar los datos de la cuenta activa.'); format_p(p)

cell_text(d.tables[5].cell(1,0),[
    'Login: captura correo y contraseña, valida los campos e inicia sesión con Firebase. Registro: captura nombre, apellido, alias, correo y confirmación de contraseña; crea la cuenta y el perfil. Recuperación: solicita a Firebase el envío de instrucciones al correo de la cuenta.',
    'Home: organiza los accesos y conserva las preferencias de tamaño y contraste. Panel de accesibilidad: administra frases, filtra por texto, contexto y frecuencia, edita contenido y categoría y confirma la eliminación. Historial: consulta las frases guardadas de la cuenta activa.',
    'Avisos: presenta información en tarjetas con fecha, prioridad y ajustes de texto. Recomendaciones: mantiene cinco consejos semanales. Perfil: consulta nombre, alias y correo de la identidad autenticada, sin exponer cuentas ajenas ni contraseñas.',
    'La interfaz usa listas perezosas y desplazamiento para evitar cargar todas las tarjetas de una vez y permitir el uso del teclado en pantallas pequeñas. Las llamadas de red se ejecutan en corrutinas; los botones muestran carga y evitan solicitudes repetidas. El tiempo máximo de espera de una operación interactiva es 20 segundos.',
    'Objetivos de rendimiento: respuesta inmediata de validaciones locales y filtros; navegación fluida; inicio y sincronización sujetos a red y al servicio remoto. Son objetivos de diseño, no mediciones de latencia de la aplicación en dispositivos físicos.',
    f'Pruebas JUnit ejecutadas: {total} casos, {failed} fallos o errores. La validación cubre correo, espacios, campos obligatorios, longitud de contraseña, confirmación, límite de 500 caracteres y categoría de frase. El filtrado comprueba mayúsculas, espacios, categoría, frecuencia y ausencia de coincidencias.',
    integration_status,
    'La comprobación manual del APK publicado se realizó en el emulador ComuniaPruebas con Firebase real. Las capturas muestran el inicio, la edición y el texto actualizado, además de los ajustes de accesibilidad. Se recibió el correo de recuperación y se confirmó el cambio de contraseña. El usuario también confirmó el ingreso con la nueva contraseña, la eliminación y la conservación de sesión y frases al reabrir. Los anexos 4 y 5 detallan resultados automáticos y manuales.'])

cell_text(d.tables[6].cell(1,0),[
    'Tecnología: Android Studio, Kotlin 2.2.10, Android Gradle Plugin 9.3.2, Jetpack Compose y Material 3. Android mínimo API 26, compilación y objetivo API 37. Se conserva el identificador com.example.comuniaapp.',
    'Servicios: el registro, ingreso, recuperación y sincronización requieren Firebase configurado y conexión a internet. La caché de Firestore permite consultar frases previamente cargadas cuando se pierde la conexión. Los cambios se ejecutan con validación de red y mensajes visuales ante errores.',
    'Seguridad: las reglas separan datos por UID, admiten únicamente los campos definidos y validan longitud y tipo. SharedPreferences no almacena contraseñas. La clave privada de firma se conserva localmente y queda fuera de Git y del ZIP de entrega; debe respaldarse para futuras actualizaciones.',
    'Accesibilidad: la aplicación evita que una alerta acústica sea el único medio de retroalimentación. El texto grande y el contraste se mantienen entre sesiones. La voz depende de que el dispositivo tenga un motor TTS y datos de español disponibles.',
    firebase_status,
    'La validación en dispositivos físicos y con usuarios sigue pendiente. ' + publication_status])
cell_text(d.tables[7].cell(1,0),[
    'La EDT organiza la continuidad de la aplicación en cuatro fases: gestión del proyecto, diseño de interfaz, desarrollo de software y cierre. Conserva la estructura de las experiencias anteriores y amplía desarrollo y cierre con autenticación, persistencia, CRUD, pruebas y distribución.',
    'El Anexo 1 muestra el diagrama jerárquico. El Anexo 2 define los paquetes de trabajo y su evidencia de aceptación.'])

risks=[
    ('Incompatibilidad de Compose con versiones antiguas de Android.','Desarrollo','Media','Significativo','Mantener minSdk 26 y comprobar instalación en versiones compatibles.'),
    ('Legibilidad insuficiente por contraste inadecuado.','Diseño y pruebas','Media','Alto','Aplicar contraste, texto grande y revisar legibilidad en distintos dispositivos.'),
    ('Errores de lógica en el registro y el arreglo de cinco usuarios.','Pruebas','Media','Moderado','Validar formularios y reemplazar el arreglo por autenticación y perfil persistentes.'),
    ('Fallo al perder la conexión a internet.','Uso y operación','Alta','Significativo','Comprobar conexión, manejar errores y permitir consulta de la caché de frases.'),
    ('Respuesta lenta en dispositivos de gama baja.','Pruebas','Media','Moderado','Usar listas perezosas y corrutinas; medir el desempeño en un equipo de gama baja.')]
table=d.tables[8]
for i,record in enumerate(risks,2):
    for j,value in enumerate(record): cell_text(table.cell(i,j),[value])
for row in list(table.rows)[7:]: table._tbl.remove(row._tr)
anchor=after_table(table,'Seguimiento de los cinco riesgos originales',True)
tracking=[
    'R1 Compatibilidad: se conserva minSdk 26 y se verifica la compilación del proyecto. Resultado: mitigación técnica implementada; validación en múltiples versiones Android pendiente de la matriz de dispositivos.',
    'R2 Legibilidad: el contraste se aplica desde el tema general y el tamaño de texto conserva la preferencia. Se eliminaron contenedores de altura fija en el listado de frases. Resultado: las capturas del emulador muestran el inicio con texto normal y con texto grande y contraste activados. La evaluación con usuarios con discapacidad auditiva y dispositivos físicos sigue pendiente.',
    f'R3 Registro: se incorporaron validaciones verificadas con JUnit y autenticación de Firebase. El riesgo original del arreglo evoluciona a consistencia entre cuenta y perfil. Resultado: {total} pruebas locales ejecutadas con {failed} fallos; el registro remoto se comprueba en las pruebas de integración.',
    'R4 Conectividad: se valida la existencia de red antes de operaciones, se manejan excepciones y se aplica un tiempo de espera de 20 segundos. Firestore mantiene caché de lectura. Resultado: mitigación implementada; la comprobación manual de interrupción y recuperación de red continúa pendiente. La publicación del APK ya fue realizada.',
    'R5 Rendimiento: se usa una sola LazyColumn para las frases y se eliminan listas perezosas anidadas con altura fija. Las consultas remotas no bloquean el hilo principal. Resultado: optimización implementada; mediciones de fluidez y consumo en gama baja todavía pendientes.']
for text in tracking: anchor=after_p(anchor,text)
# El salto de la plantilla no debe crear una página vacía cuando termina el seguimiento.
separator=anchor._p.getnext()
if separator is not None and separator.tag == qn('w:p'):
    from docx.text.paragraph import Paragraph
    break_p=Paragraph(separator,d._body)
    for br in separator.xpath('.//w:br'): br.getparent().remove(br)
    break_p.paragraph_format.page_break_before=True
    break_p.paragraph_format.line_spacing=1
    break_p.paragraph_format.space_after=Pt(0)
    for run in break_p.runs: run.font.size=Pt(1)

artifacts=[('Código fuente Kotlin','Proyecto Android, ViewModel, repositorios, modelos y pantallas Compose.'),('Configuración Firebase','Cliente Android y reglas Firestore para datos privados por UID.'),('Pruebas y evidencia','Resultados JUnit y pruebas instrumentadas de UI y backend.'),('APK de distribución','Paquete Android firmado y evidencia del certificado.'),('Documento técnico PDF','Formato de respuesta con alcance, mockups, views, restricciones, cinco riesgos y EDT.'),('Repositorio Git y ZIP','Historial de versiones y paquete de entrega con fuente, PDF, APK y evidencias.')]
for i,(name,text) in enumerate(artifacts,2):
    cell_text(d.tables[9].cell(i,0),[name]); cell_text(d.tables[9].cell(i,1),[text])

cell_text(d.tables[10].cell(1,0),[
    'El cierre requiere que el frontend y backend compilen, las cuentas se registren e ingresen con credenciales válidas y se pueda recuperar el acceso; que SharedPreferences restaure únicamente el perfil de la identidad autenticada; y que el CRUD permita crear, consultar, editar y eliminar frases manteniendo su aislamiento por cuenta.',
    'Las pruebas JUnit deben terminar sin fallos. Se debe comprobar navegación, teclado, texto grande, contraste, interrupción de red y persistencia tras reiniciar. No se aceptan defectos que impidan iniciar sesión, guardar datos, mantener la privacidad o completar la comunicación. Se toleran ajustes menores de estilo que no afecten legibilidad ni funcionamiento.',
    apk_status,
    'Proceso de distribución: generar la clave RSA, configurar la firma local de release, ejecutar assembleRelease, verificar el APK con apksigner y adjuntar el paquete al ZIP. La clave se conserva para futuras actualizaciones y no se publica.',
    publication_status + ' La entrega por AVA requiere adjuntar el ZIP y comprobar su recepción dentro del plazo de la asignatura.',
    revision_status + ' El código actualizado también se incluye en el ZIP de entrega.'])

for p in d.paragraphs:
    if p.text.startswith('Anexo 1:'):
        pic=after_p(p,''); pic.add_run().add_picture(str(QA/'edt.png'),width=Inches(6))
    if p.text.startswith('Anexo 2.'):
        p.paragraph_format.page_break_before=True
        edt=d.add_table(rows=1,cols=3); p._p.addnext(edt._tbl)
        edt.autofit=False
        for i,label in enumerate(['Código','Paquete de trabajo','Evidencia de aceptación']): cell_text(edt.rows[0].cells[i],[label])
        records=[('1.1','Requisitos','Matriz de los nueve criterios en el Anexo 6.'),('1.2','Informe y Git','Formato de respuesta completado e historial del proyecto.'),('2.1','Mockups','Nueve vistas y flujo de navegación documentados.'),('2.2','Accesibilidad','Texto y contraste persistentes; confirmaciones visuales.'),('3.1','Autenticación y sesión','Registro, ingreso, recuperación y SharedPreferences.'),('3.2','CRUD de frases','Creación, lectura, edición, frecuencia y eliminación por UID.'),('3.3','Interfaz y TTS','Mensajes escritos y lectura opcional con manejo de disponibilidad.'),('4.1','Pruebas','Resultados JUnit y casos instrumentados de UI y Firebase.'),('4.2','APK y firma','Compilación release y verificación del certificado.'),('4.3','Publicación','Plataforma seleccionada y enlace de descarga verificado.'),('4.4','PDF y ZIP','Informe PDF y paquete único con los entregables.')]
        for record in records:
            for cell,text in zip(edt.add_row().cells,record): cell_text(cell,[text])
        for row in edt.rows:
            for cell,width in zip(row.cells,[0.8,1.9,3.65]): cell.width=Inches(width)

def annex_heading(anchor, text):
    p=after_p(anchor,text,True)
    p.paragraph_format.page_break_before=True
    p.paragraph_format.keep_with_next=True
    return p

def annex_table(anchor, headers, records, widths):
    table=d.add_table(rows=1,cols=len(headers)); anchor._p.addnext(table._tbl)
    table.autofit=False
    for cell,text in zip(table.rows[0].cells,headers): cell_text(cell,[text])
    for record in records:
        for cell,text in zip(table.add_row().cells,record): cell_text(cell,[text])
    for row in table.rows:
        cant_split=OxmlElement('w:cantSplit'); row._tr.get_or_add_trPr().append(cant_split)
        for cell,width in zip(row.cells,widths): cell.width=Inches(width)
    return table

anchor=after_table(edt,'Anexo 3 Implementación del código',True)
anchor.paragraph_format.page_break_before=True
anchor.paragraph_format.keep_with_next=True
code_sections=[
    ('Pantallas y estado', 'MainActivity.kt define los nueve destinos y conecta las acciones de las pantallas Compose con ComuniaViewModel. El destino de acceso cambia a Inicio cuando existe un perfil autenticado. Panel e Historial reutilizan PanelAccesibilidadScreen, con títulos distintos. El ViewModel mantiene usuario, frases, carga y mensajes; las corrutinas realizan las operaciones sin bloquear la interfaz.'),
    ('Autenticación y recuperación', 'data/AuthRepository.kt utiliza createUserWithEmailAndPassword para el registro y signInWithEmailAndPassword para el ingreso. Guarda el perfil en usuarios/{uid} y actualiza el nombre visible en Firebase Authentication. Si falla la creación del perfil, intenta eliminar la cuenta recién creada. recuperar llama a sendPasswordResetEmail; salir cierra Firebase y limpia los datos básicos locales.'),
    ('Sesión y preferencias', 'data/SessionStore.kt guarda UID, nombre, apellido, alias y correo en comunia_sesion. leer devuelve el perfil solo cuando el UID guardado coincide con la identidad autenticada. Al iniciar, el ViewModel restaura ese perfil y después lo actualiza desde Firebase. MainActivity conserva por separado textoGrande y contraste en comunia_accesibilidad.'),
    ('Persistencia y CRUD', 'data/ComunicacionRepository.kt trabaja sobre usuarios/{uid}/frases. guardar crea un documento con set o edita uno existente con update; observar usa addSnapshotListener para consultar cambios; frecuencia modifica esFrecuente y eliminar ejecuta delete. El registro agrega ocho frases iniciales mediante un lote. Cada operación comprueba que el UID de la sesión siga siendo el propietario.'),
    ('Validaciones y errores', 'domain/Validacion.kt normaliza correos y verifica campos, contraseña, confirmación, categoría y longitud de frase. domain/Frases.kt combina búsqueda, categoría y frecuencia. ComuniaViewModel comprueba la red antes de las acciones y limita la espera a 20 segundos. Las excepciones se convierten en mensajes visuales; el indicador de carga evita envíos duplicados.'),
    ('Reglas y entorno', 'firestore.rules permite acceder únicamente a usuarios/{uid} y sus frases cuando request.auth.uid coincide con uid. Valida campos, tipos, longitudes y categorías. data/FirebaseBackend.kt utiliza el proyecto real en release y un proyecto local de pruebas solo cuando la variante debug habilita firebaseEmulators. La compilación publicada conserva FIREBASE_EMULATORS=false.')]
for title,text in code_sections:
    anchor=after_p(anchor,title,True); anchor.paragraph_format.keep_with_next=True
    anchor=after_p(anchor,text)

anchor=annex_heading(anchor,'Anexo 4 Resultados de las pruebas automáticas')
anchor=after_p(anchor,'Se ejecutaron 11 casos unitarios y 5 instrumentados: 16 aprobados, 0 fallos, 0 errores y 0 omitidos. Los XML y el informe de lint se adjuntan en Evidencias del ZIP. Las comprobaciones de backend de esta tabla utilizan Firebase Emulator Suite local.')
test_records=[
    ('Registro válido\n1 caso', 'Aceptar campos válidos sin mensaje de error.', 'Aprobado en ValidacionTest.'),
    ('Campos y correo\n2 casos', 'Rechazar campos vacíos y formatos de correo inválidos.', 'Ambos aprobados en ValidacionTest.'),
    ('Normalización\n1 caso', 'Recortar espacios y convertir el correo a minúsculas.', 'Aprobado en ValidacionTest.'),
    ('Contraseñas\n1 caso', 'Rechazar una clave corta y una confirmación distinta.', 'Aprobado en ValidacionTest.'),
    ('Texto y categoría\n1 caso', 'Rechazar texto vacío, 501 caracteres o categoría inválida; aceptar 500 caracteres.', 'Aprobado en ValidacionTest.'),
    ('Filtros\n4 casos', 'Combinar consulta, mayúsculas, espacios, categoría y frecuencia; devolver lista vacía sin coincidencias.', 'Los cuatro aprobados en FrasesTest.'),
    ('Prueba inicial\n1 caso', 'Comprobar la aserción aritmética de la prueba de ejemplo.', 'Aprobado en ExampleUnitTest.'),
    ('Firebase\n1 caso', 'Registrar, guardar perfil, restaurar acceso, crear, leer, editar y eliminar; rechazar el acceso de otra cuenta.', 'Aprobado en FirebaseCrudTest. El documento ajeno devuelve PERMISSION_DENIED.'),
    ('Formulario Compose\n3 casos', 'Enviar valores, bloquear envío durante carga y ejecutar callbacks de registro y recuperación.', 'Los tres aprobados en LoginScreenTest.'),
    ('Contexto Android\n1 caso', 'Comprobar el identificador com.example.comuniaapp.', 'Aprobado en ExampleInstrumentedTest.')]
table=annex_table(anchor,['Prueba','Resultado esperado','Resultado obtenido'],test_records,[1.45,2.7,2.2])
anchor=after_table(table,'Las clases de prueba se encuentran en app/src/test y app/src/androidTest. Lint finalizó con 0 errores; las advertencias de mantenimiento y estilo permanecen registradas. La firma del APK se verificó con apksigner, con firma v2 válida y certificado RSA de 3072 bits.')
anchor=after_p(anchor,'Se corrigieron dos fallos de la primera ejecución instrumentada: el permiso de red local de Android 17 para Firebase Emulator Suite y el selector del campo editable de contraseña. La ejecución posterior aprobó los cinco casos instrumentados.')

anchor=annex_heading(anchor,'Anexo 5 Validación manual del APK publicado')
anchor=after_p(anchor,'Bastian Ortiz probó el APK 1.0 publicado en el emulador ComuniaPruebas, Android 17, conectado al proyecto Firebase real comunicapp-5661e. Estas comprobaciones son adicionales a los casos automáticos. Las capturas fueron aportadas por el usuario el 05-10-2026; los resultados sin imagen se identifican como confirmaciones del usuario.')
manual_records=[
    ('Acceso e Inicio','Mostrar el menú de la cuenta activa.','Captura U1: se muestra Inicio con el saludo de la cuenta.'),
    ('Edición de frase','Guardar el texto modificado y mostrarlo en el listado.','Capturas U2 y U3: formulario de edición y frase actualizada.'),
    ('Accesibilidad','Cambiar el tamaño de texto y el contraste del tema.','Capturas U1 y U4: vista normal y ajustes activados.'),
    ('Recuperación','Recibir el correo y completar el cambio de contraseña.','Correo recibido visible en la captura aportada; cambio confirmado por el usuario.'),
    ('Ingreso posterior','Acceder con la contraseña nueva.','Confirmado por el usuario.'),
    ('Eliminación','Retirar del listado la frase seleccionada.','Confirmado por el usuario.'),
    ('Restauración','Conservar sesión y frases al cerrar y reabrir.','Confirmado por el usuario.')]
table=annex_table(anchor,['Comprobación','Resultado esperado','Resultado obtenido'],manual_records,[1.45,2.2,2.7])
anchor=after_table(table,'Continúan pendientes la interrupción y recuperación de red, la matriz de versiones Android y las mediciones en equipos de gama baja. La revisión de accesibilidad se limita al emulador y no representa una evaluación con personas con discapacidad auditiva.')

user_dir=QA/'evidencias-usuario'
figures=[('U1 Inicio con ajustes normales','U1-inicio.png',3.0),
         ('U2 Edición de una frase','U2-edicion.png',2.55),
         ('U3 Frase después de guardar cambios','U3-frase-actualizada.png',4.5),
         ('U4 Inicio con texto grande y contraste activados','U4-accesibilidad.png',3.0)]
for index,(title,name,width) in enumerate(figures):
    path=user_dir/name
    if not path.is_file(): continue
    anchor=after_p(anchor,title,True)
    anchor.paragraph_format.keep_with_next=True
    if index in (0,1,3): anchor.paragraph_format.page_break_before=True
    anchor=after_p(anchor,''); anchor.add_run().add_picture(str(path),width=Inches(width))
anchor=after_p(anchor,'La recepción del correo se comprobó en la captura aportada. El mensaje contiene el enlace personal de recuperación; el informe registra el resultado sin reproducir ese enlace ni el código de la acción.')

anchor=annex_heading(anchor,'Anexo 6 Correspondencia con la pauta')
criteria=[
    ('1','Kotlin y Compose','Alcance, nueve mockups y Anexo 3; fuente Android en el ZIP.'),
    ('2','Persistencia','Firestore por cuenta y ComunicacionRepository; prueba de Firebase del Anexo 4.'),
    ('3','Acceso y sesión','AuthRepository, SessionStore y comprobación manual de restauración del Anexo 5.'),
    ('4','CRUD','Panel e Historial, prueba automática y capturas de edición; eliminación confirmada.'),
    ('5','Pruebas','JUnit y pruebas instrumentadas ejecutadas; 16 casos aprobados y XML adjuntos.'),
    ('6','APK','ComunicaApp-1.0.apk incluido y publicado.'),
    ('7','Firma','Verificación apksigner de firma v2 y certificado RSA.'),
    ('8','Git','Repositorio público, rama codex/semana8 y tag v1.0.0 del APK.'),
    ('9','Documentación','Vistas, código, pruebas, resultados, restricciones, cinco riesgos, EDT y publicación.')]
annex_table(anchor,['Criterio','Contenido','Evidencia'],criteria,[0.8,1.8,3.75])

# La cabecera de continuación identifica las columnas, sin repetir instrucciones extensas.
risk_table=d.tables[8]
for p in risk_table.rows[0].cells[0].paragraphs:
    if p.text.strip():
        from docx.text.paragraph import Paragraph
        el=deepcopy(p._p); risk_table._tbl.addprevious(el)
        intro=Paragraph(el,d._body); intro.style='Normal'; format_p(intro)
        for shade in intro._p.xpath('./w:pPr/w:shd'): shade.getparent().remove(shade)
risk_table._tbl.remove(risk_table.rows[0]._tr)

# Respuestas con párrafos legibles; no se conservan las grandes alturas de campos vacíos.
for table in d.tables[1:]:
    for row in table.rows:
        for cell in row.cells:
            for p in cell.paragraphs: format_p(p)
            tcpr=cell._tc.get_or_add_tcPr()
            borders=tcpr.find(qn('w:tcBorders'))
            if borders is None: borders=OxmlElement('w:tcBorders'); tcpr.append(borders)
            for edge in ['top','left','bottom','right']:
                el=OxmlElement('w:'+edge); el.set(qn('w:val'),'single'); el.set(qn('w:sz'),'4'); el.set(qn('w:color'),'D9D9D9'); borders.append(el)
    first=table.rows[0]._tr.get_or_add_trPr()
    repeat=OxmlElement('w:tblHeader'); first.append(repeat)
    for cell in table.rows[0].cells:
        for p in cell.paragraphs:
            p.paragraph_format.keep_with_next=True
            for run in p.runs: run.font.bold=True

for p in d.tables[4].cell(1,0).paragraphs:
    if p.text == 'Historial de frases': p.paragraph_format.keep_with_next=True

d.core_properties.title='Desarrollo y distribución de ComunicaApp'
d.core_properties.author='Bastian Ortiz'
dest=OUT/'S8_Bastian_Ortiz_ComunicaApp.docx'
d.save(dest)
print(dest)
print('Resultados JUnit:',tests)
