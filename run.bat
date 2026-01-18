@echo off
setlocal enabledelayedexpansion
REM Assicurati di essere nella directory corretta
cd /d "%~dp0"
REM Imposta console in UTF-8 per simboli (€, ★)
chcp 65001 >nul

REM Pulizia build precedente
if exist bin (
    rmdir /s /q bin
)
if exist TheKnife.jar (
    del /q TheKnife.jar
)
mkdir bin

REM Compila il progetto
echo ===============================================================
echo Compilazione in corso...
echo ===============================================================
javac -d bin -encoding UTF-8 src\*.java src\gestione\*.java src\model\*.java src\util\*.java
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ===============================================================
    echo ERRORE: Compilazione fallita
    echo ===============================================================
    echo.
    pause
    exit /b 1
)
echo.
echo Compilazione completata con successo!
echo.

REM Importa dati Michelin se ristoranti.dati è vuoto, mancante o solo header
set "RIST_FILE=data\ristoranti.dati"
set "MICHELIN_FILE=data\michelin_my_maps.csv"
set "NEED_IMPORT=0"
if not exist "%RIST_FILE%" set "NEED_IMPORT=1"
if exist "%RIST_FILE%" (
    for %%A in ("%RIST_FILE%") do (
        if %%~zA EQU 0 set "NEED_IMPORT=1"
    )
    if "!NEED_IMPORT!"=="0" (
        for /f %%L in ('type "%RIST_FILE%" ^| find /c /v ""') do set "RIST_LINES=%%L"
        if "!RIST_LINES!"=="1" set "NEED_IMPORT=1"
    )
)
if "!NEED_IMPORT!"=="1" (
    if exist "%MICHELIN_FILE%" (
        echo File ristoranti.dati vuoto o mancante. Avvio importazione Michelin...
        java -cp bin util.ImportaMichelin
        if %ERRORLEVEL% NEQ 0 (
            echo.
            echo ===============================================================
            echo ERRORE: Importazione Michelin fallita
            echo ===============================================================
            echo.
            pause
            exit /b 1
        )
    ) else (
        echo File ristoranti.dati vuoto o mancante e "%MICHELIN_FILE%" non trovato.
        echo Continua senza importazione.
    )
)

REM Esegui TheKnife
java -cp bin TheKnife

REM Se ci sono errori, mostra messaggio
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ===============================================================
    echo ERRORE: Impossibile avviare TheKnife
    echo ===============================================================
    echo.
    echo Possibili cause:
    echo 1. Java non installato - Verifica con: java -version
    echo 2. Compilazione fallita - Controlla gli errori sopra
    echo.
)

pause
