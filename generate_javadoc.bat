@echo off
echo Generazione JavaDoc...
javadoc -d docs/javadoc -encoding UTF-8 -charset UTF-8 -sourcepath src -subpackages model:gestione:util -author -version
if %ERRORLEVEL% EQU 0 (
    echo.
    echo JavaDoc generato con successo in docs/javadoc/
    echo Apri docs/javadoc/index.html per visualizzarlo
) else (
    echo.
    echo Errore nella generazione JavaDoc
)
pause
