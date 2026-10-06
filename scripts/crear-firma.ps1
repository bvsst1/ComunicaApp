param([string]$JavaBin = 'C:\Program Files\Android\Android Studio\jbr\bin')
$ErrorActionPreference = 'Stop'
$taskRoot = Split-Path -Parent $PSScriptRoot
$taskSigning = Join-Path $taskRoot 'signing'
$taskKeystore = Join-Path $taskSigning 'comunia-release.jks'
$taskProperties = Join-Path $taskRoot 'keystore.properties'
if ((Test-Path -LiteralPath $taskKeystore) -or (Test-Path -LiteralPath $taskProperties)) {
    throw 'Ya existe una firma o configuración. Se conserva para no cambiar la identidad de la aplicación.'
}
New-Item -ItemType Directory -Force -Path $taskSigning | Out-Null
$taskBytes = New-Object byte[] 24
[System.Security.Cryptography.RandomNumberGenerator]::Fill($taskBytes)
$taskPassword = [Convert]::ToBase64String($taskBytes)
$env:COMUNIA_KEY_PASSWORD = $taskPassword
try {
    & (Join-Path $JavaBin 'keytool.exe') -genkeypair -keystore $taskKeystore -storetype JKS -alias comunia -keyalg RSA -keysize 3072 -validity 10000 -dname 'CN=Bastian Ortiz, OU=DSY2204, O=ComunicaApp, C=CL' -storepass:env COMUNIA_KEY_PASSWORD -keypass:env COMUNIA_KEY_PASSWORD -noprompt
    if ($LASTEXITCODE -ne 0) { throw 'No se pudo generar la firma.' }
    @("storeFile=signing/comunia-release.jks", "storePassword=$taskPassword", 'keyAlias=comunia', "keyPassword=$taskPassword") |
        Set-Content -LiteralPath $taskProperties -Encoding ascii
    Write-Output 'Firma creada. Conserva signing/comunia-release.jks y keystore.properties en un respaldo privado; no se incluyen en la entrega.'
} finally { Remove-Item Env:\COMUNIA_KEY_PASSWORD -ErrorAction SilentlyContinue }
