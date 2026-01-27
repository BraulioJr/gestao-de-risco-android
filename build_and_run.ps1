# Script PowerShell para compilar e rodar o app
# Gestão de Risco - Build & Deploy Automation

Write-Host "================================" -ForegroundColor Cyan
Write-Host "  GESTAO DE RISCO - Build & Run" -ForegroundColor Cyan
Write-Host "================================" -ForegroundColor Cyan
Write-Host ""

# Definir variáveis
$projectDir = "C:\Users\user\AndroidStudioProjects\Project_GestaoDeRisco"
$gradleWrapper = "$projectDir\gradlew.bat"
$appPackage = "com.example.project_gestoderisco"
$mainActivity = "$appPackage.view.LoginActivity"
$apkPath = "$projectDir\app\build\outputs\apk\debug\app-debug.apk"

# Step 1: Limpar e compilar
Write-Host "[1/4] Compilando APK Debug..." -ForegroundColor Yellow
Write-Host "      Rodando: gradlew clean assembleDebug" -ForegroundColor Gray

cd $projectDir

# Executar Gradle
& $gradleWrapper clean assembleDebug --info 2>&1 | Tee-Object -FilePath "$projectDir\build.log" | Select-Object -Last 50

$buildStatus = $LASTEXITCODE
Write-Host ""

if ($buildStatus -ne 0) {
    Write-Host "❌ ERRO NA COMPILACAO!" -ForegroundColor Red
    Write-Host "   Verifique o log: $projectDir\build.log" -ForegroundColor Red
    exit 1
}

# Step 2: Verificar se APK existe
Write-Host "[2/4] Verificando APK..." -ForegroundColor Yellow

if (-not (Test-Path $apkPath)) {
    Write-Host "❌ APK NAO ENCONTRADO!" -ForegroundColor Red
    Write-Host "   Esperado em: $apkPath" -ForegroundColor Red
    
    # Procurar APKs
    $apks = Get-ChildItem -Path "$projectDir\app\build" -Filter "*.apk" -Recurse
    if ($apks.Count -gt 0) {
        Write-Host "   APKs encontrados:" -ForegroundColor Gray
        $apks | ForEach-Object { Write-Host "   - $_" -ForegroundColor Gray }
    }
    exit 1
}

Write-Host "✓ APK encontrado: $(Get-Item $apkPath | Select-Object -ExpandProperty Length) bytes" -ForegroundColor Green

# Step 3: Instalar
Write-Host "[3/4] Instalando APK no emulador..." -ForegroundColor Yellow
Write-Host "      Rodando: adb install -r $apkPath" -ForegroundColor Gray

adb install -r $apkPath

if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ ERRO NA INSTALACAO!" -ForegroundColor Red
    exit 1
}

Write-Host "✓ App instalado com sucesso!" -ForegroundColor Green

# Step 4: Executar
Write-Host "[4/4] Iniciando aplicativo..." -ForegroundColor Yellow
Write-Host "      Rodando: adb shell am start -n $appPackage/$mainActivity" -ForegroundColor Gray

adb shell am start -n "$appPackage/$mainActivity"

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "================================" -ForegroundColor Green
    Write-Host "✓ SUCESSO!" -ForegroundColor Green
    Write-Host "  App iniciando no emulador..." -ForegroundColor Green
    Write-Host "================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "Dicas:" -ForegroundColor Cyan
    Write-Host "- Abra Logcat: adb logcat | grep project_gestoderisco" -ForegroundColor Gray
    Write-Host "- Para debugar: Android Studio > Run > Debug 'app'" -ForegroundColor Gray
    Write-Host "- Ver logs: $projectDir\build.log" -ForegroundColor Gray
} else {
    Write-Host "❌ Erro ao iniciar app!" -ForegroundColor Red
    exit 1
}
