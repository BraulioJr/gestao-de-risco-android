Set-Location "C:\Users\user\AndroidStudioProjects\Project_GestaoDeRisco"

Write-Host "Compilando APK Debug..." -ForegroundColor Cyan
.\gradlew.bat clean assembleDebug -x lint

if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ BUILD SUCCESSFUL!" -ForegroundColor Green
    
    $apkPath = "app\build\outputs\apk\debug\app-debug.apk"
    if (Test-Path $apkPath) {
        Write-Host "✓ Instalando no emulador..."
        adb install -r $apkPath
        
        if ($LASTEXITCODE -eq 0) {
            Write-Host "✓ Sucesso! Abrindo app..."
            adb shell am start -n com.example.project_gestoderisco/.view.MainActivity
            Write-Host "✅ APP ABRINDO NO EMULADOR!" -ForegroundColor Green
        }
    }
} else {
    Write-Host "❌ BUILD FAILED!" -ForegroundColor Red
}
