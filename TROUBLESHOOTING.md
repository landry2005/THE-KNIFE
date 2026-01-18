# Troubleshooting TheKnife

## ❌ Problema: "Impossibile avviare con run.bat"

### Soluzione 1: Usa run.bat (compila automaticamente)
```batch
run.bat
```

Se l'errore persiste, prova una compilazione manuale:
```batch
javac -d bin -encoding UTF-8 src\*.java src\gestione\*.java src\model\*.java src\util\*.java
```

---

### Soluzione 2: Verifica Java
Assicurati che Java sia installato:
```batch
java -version
```

Dovresti vedere qualcosa come:
```
java version "11.0.x"
```

Se non funziona, installa Java JDK 11 o superiore da:
https://www.oracle.com/java/technologies/downloads/

---

### Soluzione 3: Esegui da File Explorer
1. Apri **File Explorer**
2. Naviga alla cartella `THE-KNIFE`
3. Fai **doppio clic** su `run.bat`

---

### Soluzione 4: Usa il file JAR
Se esiste `TheKnife.jar`:

```batch
java -jar TheKnife.jar
```

---

## ❌ Problema: "ClassNotFoundException"

**Causa**: Progetto non compilato

**Soluzione**:
```batch
run.bat
```

Se necessario, compila manualmente:
```batch
javac -d bin -encoding UTF-8 src\*.java src\gestione\*.java src\model\*.java src\util\*.java
```

---

## ❌ Problema: "File ristoranti.dati non trovato"

**Soluzione 1**: Avvia con `run.bat`
Il file viene creato automaticamente se assente o vuoto.

**Soluzione 2**: Importa da Michelin
Assicurati di avere `data/michelin_my_maps.csv`, poi:
```batch
java -cp bin util.ImportaMichelin
```

---

## ❌ Problema: PowerShell non funziona

**Soluzione**: Usa `cmd` invece di PowerShell

1. Premi `Win + R`
2. Digita `cmd`
3. Premi Invio
4. Naviga al progetto:
   ```
   cd C:\percorso\THE-KNIFE
   ```
5. Esegui:
   ```
   run.bat
   ```

---

## ❌ Problema: Caratteri strani nell'output

**Soluzione**: Imposta encoding UTF-8

Prima di eseguire:
```batch
chcp 65001
run.bat
```

---

## ✅ Test Rapido

Esegui questo per verificare tutto:

```batch
run.bat
```

---

## 📞 Verifica Sistema

### 1. Verifica Java
```batch
java -version
javac -version
```

### 2. Verifica Struttura File
```
THE-KNIFE/
├── bin/                ✅ Deve esistere con file .class
├── src/                ✅ Deve contenere file .java
├── data/               ✅ Può essere vuota inizialmente
├── run.bat             ✅ File di esecuzione
```

### 3. Verifica Compilazione
```batch
dir bin\*.class /s
```

Dovresti vedere:
- `TheKnife.class`
- `model\Ristorante.class`
- `model\Utente.class`
- `model\Recenzione.class`
- E altri...

---

## 🚀 Metodi Alternativi di Avvio

### Metodo 1: JAR (Più Affidabile)
```batch
java -jar TheKnife.jar
```

### Metodo 2: Direttamente
```batch
cd THE-KNIFE
java -cp bin TheKnife
```

### Metodo 3: Con percorso completo
```batch
java -cp "C:\Users\...\THE-KNIFE\bin" TheKnife
```

### Metodo 4: Da IDE (Eclipse/IntelliJ)
1. Importa progetto
2. Run → Run As → Java Application
3. Seleziona `TheKnife` (main class)

---

## 🔧 Ricompilazione Completa

Se nulla funziona, ricompila da zero:

```batch
REM Elimina file compilati
rmdir /s /q bin
mkdir bin

REM Ricompila tutto
javac -d bin -encoding UTF-8 src\*.java src\gestione\*.java src\model\*.java src\util\*.java

REM Esegui
run.bat
```

---

## 📊 Checklist Debug

- [ ] Java installato e nel PATH
- [ ] Directory `bin` esiste
- [ ] File `bin\TheKnife.class` esiste
- [ ] Compilazione senza errori
- [ ] Nella directory corretta del progetto
- [ ] Usare `cmd` invece di PowerShell

---

## 💡 Suggerimenti

1. **Usa sempre `cmd` su Windows**, non PowerShell
2. **Fai doppio clic su `run.bat`** invece di eseguirlo da terminale
3. **Ricompila se hai modificato il codice**: `run.bat`
4. **Verifica Java**: `java -version` deve funzionare
5. **Percorsi**: Assicurati di essere nella directory `THE-KNIFE`

---

## 📝 Log Errori Comuni

### Errore: "java: command not found"
→ Java non installato o non nel PATH
→ Soluzione: Installa Java JDK

### Errore: "ClassNotFoundException: TheKnife"
→ Progetto non compilato
→ Soluzione: Esegui `run.bat`

### Errore: "Cannot find or load main class"
→ Nella directory sbagliata
→ Soluzione: Naviga a `THE-KNIFE` e riprova

### Errore: "Access Denied"
→ Permessi insufficienti
→ Soluzione: Esegui come amministratore

---

**Ultima risorsa**: Apri il progetto in un IDE come Eclipse o IntelliJ IDEA ed eseguilo da lì.

