# TheKnife - Manuale Utente

## Indice
1. [Introduzione](#introduzione)
2. [Installazione](#installazione)
3. [Primo Avvio](#primo-avvio)
4. [Registrazione](#registrazione)
5. [Login](#login)
6. [Funzionalità Ospite](#funzionalità-ospite)
7. [Funzionalità Cliente](#funzionalità-cliente)
8. [Funzionalità Ristoratore](#funzionalità-ristoratore)
9. [Ricerca Ristoranti](#ricerca-ristoranti)
10. [Domande Frequenti](#domande-frequenti)

---

## 1. Introduzione

**TheKnife** è una piattaforma che permette di scoprire ristoranti in tutto il mondo, consultare recensioni e condividere le proprie esperienze culinarie. Simile a TheFork, TheKnife offre funzionalità di ricerca avanzata, gestione preferiti e sistema di recensioni.

### Caratteristiche Principali
- 🔍 Ricerca ristoranti per località, tipo di cucina, prezzo
- ⭐ Sistema di valutazione con stelle (1-5)
- ❤️ Lista personalizzata di ristoranti preferiti
- 📝 Recensioni con possibilità di modifica
- 🍕 Informazioni su delivery e prenotazioni
- 👨‍🍳 Area dedicata per ristoratori

---

## 2. Installazione

### Requisiti di Sistema
- **Sistema Operativo**: Windows, Linux, macOS
- **Java**: JDK 11 o superiore
- **Spazio su disco**: Minimo 50 MB

### Procedura di Installazione

#### Windows
1. Scaricare l'archivio del progetto
2. Estrarre in una cartella a piacere
3. Fare doppio clic su `run.bat` per compilare ed avviare

#### Linux/Mac
1. Scaricare l'archivio del progetto
2. Estrarre in una cartella a piacere
3. Aprire il terminale nella cartella del progetto
4. Compilare:
   ```bash
   javac -d bin -encoding UTF-8 src/**/*.java
   ```
5. Avviare:
   ```bash
   java -cp bin TheKnife
   ```

#### Tramite JAR
```bash
java -jar TheKnife.jar
```

---

## 3. Primo Avvio

Al primo avvio, verrai accolto dal menu principale:

```
═══════════════════════════════════════════════════════
           BENVENUTO IN THE KNIFE
    La tua piattaforma per scoprire ristoranti
═══════════════════════════════════════════════════════

═══════════════════════════════════════════════════════
              MENU PRINCIPALE
═══════════════════════════════════════════════════════
1) Continua come ospite
2) Registrati
3) Accedi
4) Password dimenticata?
0) Esci
═══════════════════════════════════════════════════════
```

Puoi scegliere di:
- **Registrarti** per creare un nuovo account
- **Accedere** se hai già un account
- **Continuare come ospite** per esplorare senza registrarti

---

## 4. Registrazione

### Registrazione come Cliente

1. Selezionare `2) Registrati` dal menu principale
2. Inserire i dati richiesti:
   - **Nome**: Il tuo nome
   - **Cognome**: Il tuo cognome
   - **Username**: Scegli un username univoco
   - **Password**: Scegli una password sicura
   - **Tipo account**: Seleziona `1) Cliente`
   - **Data di nascita**: Opzionale (formato: gg/mm/aaaa)
   - **Luogo di domicilio**: La tua città
   - **Domanda di sicurezza**: Obbligatoria per il recupero password
   - **Risposta**: Verrà cifrata e salvata

3. Conferma i dati

**Nota**: L'username deve essere univoco. Se esiste già, dovrai sceglierne un altro.

### Registrazione come Ristoratore

La procedura è simile, ma al punto del tipo account seleziona `2) Ristoratore`.

**Vantaggi account ristoratore**:
- Puoi aggiungere i tuoi ristoranti alla piattaforma
- Puoi rispondere alle recensioni
- Visualizzi statistiche sulle valutazioni

---

## 5. Login

1. Selezionare `3) Accedi` dal menu principale
2. Inserire **username** e **password**
3. Se le credenziali sono corrette, accederai al tuo profilo

### Password Dimenticata?
Dal menu principale seleziona `4) Password dimenticata?` e rispondi alla domanda di sicurezza per reimpostare la password.

---

## 6. Funzionalità Ospite

Come **ospite** (non registrato) puoi:

### 6.1 Visualizzare Ristoranti Vicini
1. Inserisci la tua città
2. Seleziona `1) Visualizza ristoranti vicini`
3. Visualizza l'elenco dei ristoranti disponibili

### 6.2 Cercare Ristoranti
Utilizza i filtri di ricerca per trovare il ristorante perfetto:
- **Città**: Obbligatoria
- **Tipo di cucina**: Italiana, Giapponese, ecc.
- **Fascia di prezzo**: Minimo e massimo
- **Servizi**: Delivery, Prenotazione online
- **Valutazione**: Stelle minime

### 6.3 Visualizzare Dettagli
Consulta informazioni dettagliate su ogni ristorante:
- Indirizzo completo e coordinate
- Tipo di cucina
- Prezzo medio
- Servizi disponibili
- Valutazione media
- Recensioni di altri utenti

**Limitazioni ospite**: Non puoi lasciare recensioni né salvare preferiti.

---

## 7. Funzionalità Cliente

### 7.1 Ristoranti Preferiti

#### Aggiungere ai Preferiti
1. Visualizza dettagli di un ristorante
2. Seleziona `1) Aggiungi/Rimuovi dai preferiti`
3. Il ristorante viene aggiunto alla tua lista

#### Visualizzare Preferiti
Dal menu cliente, seleziona `4) Visualizza ristoranti preferiti`

#### Rimuovere dai Preferiti
Visualizza il ristorante e seleziona nuovamente l'opzione per rimuoverlo.

### 7.2 Recensioni

#### Lasciare una Recensione
1. Menu cliente → `5) Gestisci recensioni`
2. Seleziona `2) Aggiungi recensione`
3. Inserisci nome e città del ristorante
4. Scegli il numero di stelle (1-5):
   - ⭐ = Pessimo
   - ⭐⭐ = Scarso
   - ⭐⭐⭐ = Buono
   - ⭐⭐⭐⭐ = Ottimo
   - ⭐⭐⭐⭐⭐ = Eccellente
5. Scrivi il testo della recensione
6. Conferma

**Nota**: Puoi recensire ogni ristorante una sola volta.

#### Modificare una Recensione
1. `5) Gestisci recensioni` → `3) Modifica recensione`
2. Inserisci nome e città del ristorante
3. Inserisci nuove stelle e nuovo testo
4. Conferma

#### Eliminare una Recensione
1. `5) Gestisci recensioni` → `4) Elimina recensione`
2. Inserisci nome e città del ristorante
3. Conferma eliminazione

#### Visualizzare le Tue Recensioni
Seleziona `5) Gestisci recensioni` → `1) Visualizza le mie recensioni`

### 7.3 Profilo
Visualizza i tuoi dati personali selezionando `6) Profilo`

### 7.4 Cambia Password

Per cambiare la tua password:

1. Menu Cliente → `7) Cambia password`
2. Inserisci **password attuale** (verifica identità)
3. Inserisci **nuova password** (minimo 6 caratteri)
4. **Conferma** nuova password
5. ✓ Password cambiata!

**Requisiti:**
- Password attuale corretta
- Nuova password diversa dalla vecchia
- Nuova password ≥ 6 caratteri
- Conferma uguale a nuova password

**Consigli:**
- Usa password forte (8+ caratteri, lettere e numeri)
- Cambia password ogni 3-6 mesi
- Non riutilizzare password di altri siti
- Annota password in luogo sicuro

**Nota**: Se hai dimenticato la password attuale, usa **Recupero Password** dal menu principale.

---

## 8. Funzionalità Ristoratore

### 8.1 Aggiungere un Ristorante

1. Menu ristoratore → `1) Aggiungi ristorante`
2. Inserisci tutti i dati richiesti:
   - **Nome**: Nome del ristorante
   - **Nazione**: Es. Italia
   - **Città**: Es. Milano
   - **Indirizzo**: Indirizzo completo
   - **Latitudine**: Coordinata GPS
   - **Longitudine**: Coordinata GPS
   - **Tipo di cucina**: Es. Italiana, Giapponese
   - **Prezzo medio**: In euro
   - **Delivery**: Sì/No
   - **Prenotazione online**: Sì/No
3. Conferma

**Suggerimento**: Per trovare le coordinate GPS, usa Google Maps o altri servizi di mappe.

### 8.2 Visualizzare i Tuoi Ristoranti

Seleziona `2) Visualizza i miei ristoranti` per vedere tutti i ristoranti che hai aggiunto.

### 8.3 Gestione Recensioni

#### Visualizzare Riepilogo
`3) Visualizza riepilogo recensioni` mostra per ogni tuo ristorante:
- Valutazione media
- Numero di recensioni

#### Rispondere alle Recensioni
1. Seleziona `4) Visualizza e rispondi alle recensioni`
2. Scegli uno dei tuoi ristoranti
3. Visualizza l'elenco delle recensioni
4. Seleziona una recensione senza risposta
5. Scrivi la tua risposta
6. Conferma

**Importante**: Puoi rispondere una sola volta per ogni recensione. Rispondi in modo professionale e cortese.

**Consigli per le risposte**:
- Ringrazia sempre il cliente
- Rispondi anche alle recensioni negative in modo costruttivo
- Offri soluzioni ai problemi segnalati
- Invita a ritornare

---

## 9. Ricerca Ristoranti

### Ricerca Base
Inserisci solo la città per vedere tutti i ristoranti disponibili.

### Ricerca Avanzata

#### Per Tipo di Cucina
Inserisci parole chiave come:
- Italiana
- Giapponese
- Cinese
- Pesce
- Pizza
- Sushi

#### Per Fascia di Prezzo
Esempi:
- Budget (0-20€): Prezzo min: 0, max: 20
- Medio (20-50€): Prezzo min: 20, max: 50
- Alto (50+€): Prezzo min: 50, max: lascia vuoto

#### Per Servizi
- **Con delivery**: Filtra solo ristoranti che consegnano a domicilio
- **Con prenotazione**: Filtra solo ristoranti con prenotazione online

#### Per Valutazione
Inserisci un numero da 1 a 5 per vedere solo ristoranti con valutazione uguale o superiore.

### Combinazione Filtri
Puoi combinare più filtri per una ricerca precisa. Esempio:
- Città: Milano
- Tipo cucina: Giapponese
- Prezzo max: 40
- Con prenotazione: Sì
- Stelle min: 4

---

## 10. Domande Frequenti

### Come recupero la password?
Se hai impostato una **domanda di sicurezza** durante la registrazione:
1. Menu Principale → `4) Password dimenticata?`
2. Inserisci username
3. Rispondi alla domanda di sicurezza
4. Imposta nuova password

Se **non** hai domanda di sicurezza:
- Contatta l'amministratore per reimpostare la password

### Come cambio la password?
Dal tuo menu (cliente o ristoratore):
- Cliente: `7) Cambia password`
- Ristoratore: `6) Cambia password`

Dovrai inserire la password attuale e la nuova password (minimo 6 caratteri).

### Posso cambiare il mio username?
No, l'username non può essere modificato dopo la registrazione.

### Posso eliminare il mio account?
La funzione non è ancora disponibile. Contatta l'amministratore.

### Quante recensioni posso lasciare?
Puoi lasciare una recensione per ogni ristorante. Puoi però modificarla o eliminarla in qualsiasi momento.

### Come trovo le coordinate GPS per il mio ristorante?
1. Apri Google Maps
2. Cerca l'indirizzo del ristorante
3. Clicca con tasto destro sulla posizione
4. Seleziona "Copia coordinate"

### Posso modificare i dati di un ristorante già inserito?
Al momento no. Contatta l'amministratore per modifiche.

### Cosa succede se chiudo l'applicazione durante l'utilizzo?
I dati vengono salvati automaticamente quando:
- Aggiungi/modifichi/elimini recensioni
- Aggiungi/rimuovi preferiti
- Aggiungi ristoranti
- Esci correttamente dall'applicazione

### Le mie password sono sicure?
Sì, le password vengono cifrate con algoritmo SHA-256 prima di essere salvate. Non vengono mai memorizzate in chiaro.

### Posso usare l'applicazione offline?
Sì, TheKnife funziona completamente offline. Tutti i dati sono memorizzati localmente sul tuo computer.

### Come segnalo un bug o un problema?
Contatta gli sviluppatori del progetto tramite i contatti indicati nel README.

### Posso esportare i miei dati?
I dati sono salvati in formato testuale nella cartella `data/`:
- `utenti.dati`: Dati utenti
- `ristoranti.dati`: Dati ristoranti
- `recensioni.dati`: Recensioni (formato binario)

---

## Supporto

Per ulteriore assistenza, consulta:
- **README.md**: Informazioni tecniche
- **MANUALE_TECNICO.md**: Documentazione per sviluppatori

**Contatti**: [Inserire contatti di supporto]

---

**TheKnife - La tua guida al mondo dei ristoranti!** 🍴
