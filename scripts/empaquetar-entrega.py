"""Empaqueta la entrega académica sin claves privadas ni archivos de compilación."""
from pathlib import Path
import json
import subprocess
import zipfile

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / 'entrega'
QA = ROOT / '.qa'
evidence = json.loads((QA / 'evidencias.json').read_text(encoding='utf8'))
build = Path(evidence.get('build', str(ROOT / 'app' / 'build')))
files = subprocess.check_output(['git', 'ls-files', '-z', '--cached', '--others', '--exclude-standard'], cwd=ROOT).decode('utf8').split('\0')
revision = subprocess.check_output(['git', 'rev-parse', 'HEAD'], cwd=ROOT, text=True).strip()
destination = OUT / 'Entrega_S8_Bastian_Ortiz_ComunicaApp.zip'
required = ['ComunicaApp-1.0.apk', 'S8_Bastian_Ortiz_ComunicaApp.pdf', 'S8_Bastian_Ortiz_ComunicaApp.docx']
for name in required:
    if not (OUT / name).is_file(): raise FileNotFoundError(name)
config = ROOT / 'app' / 'google-services.json'
if not config.is_file(): raise FileNotFoundError(config)

with zipfile.ZipFile(destination, 'w', zipfile.ZIP_DEFLATED) as archive:
    for name in sorted(set(files)):
        path = ROOT / name
        if not name or not path.is_file() or name.startswith(('.idea/', '.git/')): continue
        if path.suffix in ('.jks', '.keystore') or path.name == 'keystore.properties': raise ValueError('Clave privada en la lista de fuentes')
        archive.write(path, 'Codigo_fuente/ComuniaApp/' + name)
    archive.write(config, 'Codigo_fuente/ComuniaApp/app/google-services.json')
    for name in required: archive.write(OUT / name, name)
    archive.writestr('VERSION_GIT.txt', 'Repositorio: https://github.com/bvsst1/ComunicaApp\nRevision: ' + revision + '\n')
    archive.writestr('LEEME_ENTREGA.txt',
        'Entrega DSY2204 Semana 8 - Bastian Ortiz\n\n'
        'El ZIP contiene fuente Android, APK firmado, informe PDF y copia Word editable.\n'
        'Abrir Codigo_fuente/ComuniaApp en Android Studio. Firebase real está configurado.\n'
        'El APK usa Firebase de producción; las pruebas instrumentadas usan emuladores locales.\n'
        'Las claves privadas de firma permanecen en el equipo del autor y no se incluyen.\n'
        'Consultar Evidencias y docs/verificacion-semana-8.md para los resultados y límites.\n'
        + evidence.get('publicacion', '') + '\n')
    sources = [
        (build / 'test-results' / 'testDebugUnitTest', 'Evidencias/JUnit'),
        (build / 'outputs' / 'androidTest-results' / 'connected' / 'debug', 'Evidencias/Android'),
    ]
    for directory, prefix in sources:
        for path in sorted(directory.glob('*.xml')): archive.write(path, prefix + '/' + path.name)
    for name in ['lint-results-debug.html', 'lint-results-debug.sarif']:
        path = build / 'reports' / name
        if path.is_file(): archive.write(path, 'Evidencias/' + name)
    archive.write(ROOT / 'docs' / 'verificacion-semana-8.md', 'Evidencias/Verificacion.md')

with zipfile.ZipFile(destination) as archive:
    if archive.testzip() is not None: raise ValueError('El ZIP contiene un archivo corrupto')
    names = archive.namelist()
    assert all(name in names for name in required)
    assert not any(n.endswith(('.jks', '.keystore', '/keystore.properties')) for n in names)
print(destination)
print('Archivos:', len(names), 'Bytes:', destination.stat().st_size)
