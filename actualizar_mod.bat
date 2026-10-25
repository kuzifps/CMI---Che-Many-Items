@echo off
cd /d "%~dp0"
echo ========================================================
echo   CMI - Actualizador Automatico para Lunar Client
echo ========================================================
echo [1/2] Compilando mod...
if exist "gradlew.bat" (
    call gradlew.bat build
) else (
    "C:\Program Files\Zulu\zulu-25\bin\javac.exe" @build/javac_args_full.txt
    if %errorlevel% equ 0 (
        xcopy /s /y /q "src\main\resources\*" "build\classes\" >nul 2>&1
        "C:\Program Files\Zulu\zulu-25\bin\jar.exe" -cf "build\libs\cmi-1.0.0.jar" -C "build/classes" .
    )
)
if %errorlevel% neq 0 (
    echo.
    echo [ERROR] La compilacion fallo. Revisa los errores arriba.
    pause
    exit /b %errorlevel%
)
echo.
echo [2/2] Instalando mod en Lunar Client y .minecraft...
if not exist "%USERPROFILE%\.lunarclient\profiles\26\mods\fabric-26.1.2" mkdir "%USERPROFILE%\.lunarclient\profiles\26\mods\fabric-26.1.2"
if not exist "%APPDATA%\.minecraft\mods" mkdir "%APPDATA%\.minecraft\mods"
copy /Y "build\libs\cmi-1.0.0.jar" "%USERPROFILE%\.lunarclient\profiles\26\mods\fabric-26.1.2\cmi-1.0.0.jar"
copy /Y "build\libs\cmi-1.0.0.jar" "%APPDATA%\.minecraft\mods\cmi-1.0.0.jar"
echo.
echo ========================================================
echo   [LISTO] Mod actualizado con exito en Lunar Client!
echo   Ya puedes iniciar el juego.
echo ========================================================
pause
