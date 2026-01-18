# TheKnife - Manuale Tecnico

## Indice
1. [Architettura del Sistema](#architettura-del-sistema)
2. [Struttura del Progetto](#struttura-del-progetto)
3. [Modello dei Dati](#modello-dei-dati)
4. [Classi Principali](#classi-principali)
5. [Persistenza Dati](#persistenza-dati)
6. [Sicurezza](#sicurezza)
7. [Flusso di Esecuzione](#flusso-di-esecuzione)
8. [API e Interfacce](#api-e-interfacce)
9. [Compilazione e Deploy](#compilazione-e-deploy)
10. [Testing](#testing)
11. [Manutenzione](#manutenzione)
12. [Sviluppi Futuri](#sviluppi-futuri)

---

## 1. Architettura del Sistema

### 1.1 Architettura Generale

TheKnife adotta un'architettura **a tre livelli** (Three-tier architecture):

```
┌─────────────────────────────────────┐
│     Presentation Layer (UI)         │
│      - TheKnife.java                │
│      - Console Interface            │
└─────────────────────────────────────┘
              ↕
┌─────────────────────────────────────┐
│     Business Logic Layer            │
│      - GestoreUtenti                │
│      - GestoreRistoranti            │
│      - GestoreRecensioni            │
│      - PasswordUtil                 │
└─────────────────────────────────────┘
              ↕
┌─────────────────────────────────────┐
│     Data Layer (Model + Storage)    │
│      - Utente                       │
│      - Ristorante                   │
│      - Recenzione                   │
│      - File CSV / Serializzazione   │
└─────────────────────────────────────┘
```

### 1.2 Pattern Utilizzati

#### Singleton Pattern (implicito)
I gestori (GestoreUtenti, GestoreRistoranti, GestoreRecensioni) sono istanziati una sola volta nella classe principale.

#### DAO Pattern (Data Access Object)
Le classi Gestore fungono da DAO per l'accesso ai dati persistenti.

#### MVC Pattern (Model-View-Controller)
- **Model**: Classi in `model/` (Utente, Ristorante, Recenzione)
- **View**: Interfaccia console in TheKnife
- **Controller**: Classi in `gestione/` e logica in TheKnife

---

## 2. Struttura del Progetto

```
THE-KNIFE/
│
├── src/                              # Codice sorgente
│   ├── model/                        # Modello dati
│   │   ├── Utente.java              # Rappresenta un utente
│   │   ├── Ristorante.java          # Rappresenta un ristorante
│   │   └── Recenzione.java          # Rappresenta una recensione
│   │
│   ├── gestione/                     # Business logic
│   │   ├── GestoreUtenti.java       # Gestione utenti e autenticazione
│   │   ├── GestoreRistoranti.java   # Gestione ristoranti e ricerca
│   │   └── GestoreRecensioni.java   # Gestione recensioni
│   │
│   ├── util/                         # Utilità
│   │   ├── PasswordUtil.java        # Cifratura password
│   │   └── ImportaMichelin.java     # Import dati Michelin
│   │
│   └── TheKnife.java                 # Classe principale (main)
│
├── bin/                              # File compilati (.class)
│   ├── model/
│   ├── gestione/
│   ├── util/
│   └── TheKnife.class
│
├── data/                             # Persistenza dati
│   ├── ristoranti.dati              # Database ristoranti (DATI)
│   ├── utenti.dati                  # Database utenti (DATI)
│   └── recensioni.dati              # Database recensioni (binario)
│
├── run.bat                           # Script esecuzione
├── MANIFEST.MF                       # Manifest per JAR
├── TheKnife.jar                      # File eseguibile (dopo build)
│
└── Documentazione
    ├── README.md                     # Guida generale
    ├── MANUALE_UTENTE.md            # Manuale per utenti finali
    ├── MANUALE_TECNICO.md           # Questo documento
    └── TROUBLESHOOTING.md           # Troubleshooting
```

---

## 3. Modello dei Dati

### 3.1 Diagramma delle Classi

```
┌─────────────────────────────┐
│         Utente              │
├─────────────────────────────┤
│ - nome: String              │
│ - cognome: String           │
│ - username: String          │
│ - password: String          │
│ - ruolo: String             │
│ - dataNascita: LocalDate    │
│ - luogoDomicilio: String    │
│ - ristoratiPreferiti: List  │
├─────────────────────────────┤
│ + aggiungiPreferito()       │
│ + rimuoviPreferito()        │
│ + isPreferito()             │
└─────────────────────────────┘

┌─────────────────────────────┐
│        Ristorante           │
├─────────────────────────────┤
│ - nome: String              │
│ - nazione: String           │
│ - citta: String             │
│ - indirizzo: String         │
│ - latitudine: double        │
│ - longitudine: double       │
│ - tipoCucina: String        │
│ - prezzoMedio: double       │
│ - delivery: boolean         │
│ - prenotazione: boolean     │
│ - mediaStelle: double       │
│ - numeroRecensioni: int     │
│ - idRistoratore: String     │
├─────────────────────────────┤
│ + getId()                   │
│ + getLocazione()            │
│ + toStringDettagliato()     │
└─────────────────────────────┘

┌─────────────────────────────┐
│       Recenzione            │
├─────────────────────────────┤
│ - idRecensione: String      │
│ - usernameCliente: String   │
│ - idRistorante: String      │
│ - stelle: int               │
│ - testo: String             │
│ - dataOra: LocalDateTime    │
│ - rispostaRistoratore: Str. │
│ - dataOraRisposta: LocalDT  │
├─────────────────────────────┤
│ + hasRisposta()             │
│ + getStelleSimbolo()        │
│ + toStringCompatto()        │
└─────────────────────────────┘
```

### 3.2 Relazioni

```
Utente (cliente) ──┐
                   │ 1:N
                   ├──── Recenzione ──── 1:1 ──── Ristorante
                   │ N:1
Utente (ristoratore) ┘

Utente (cliente) ──── N:M ──── Ristorante (preferiti)
```

---

## 4. Classi Principali

### 4.1 Package `model`

#### Classe `Utente`
```java
public class Utente implements Serializable
```

**Responsabilità**:
- Rappresentare un utente del sistema (cliente o ristoratore)
- Gestire la lista dei preferiti (solo per clienti)
- Memorizzare credenziali cifrate

**Attributi principali**:
- `username`: Identificatore univoco
- `password`: Hash SHA-256 della password
- `ruolo`: "cliente" o "ristoratore"
- `ristoratiPreferiti`: Lista ID ristoranti preferiti

**Metodi principali**:
- `aggiungiPreferito(String idRistorante)`: Aggiunge un ristorante ai preferiti
- `rimuoviPreferito(String idRistorante)`: Rimuove un ristorante dai preferiti
- `isPreferito(String idRistorante)`: Verifica se un ristorante è tra i preferiti

#### Classe `Ristorante`
```java
public class Ristorante implements Serializable
```

**Responsabilità**:
- Rappresentare un ristorante con tutte le sue caratteristiche
- Fornire metodi di utilità per visualizzazione

**Attributi principali**:
- `nome`, `citta`: Identificatori principali
- `latitudine`, `longitudine`: Coordinate GPS
- `prezzoMedio`: Prezzo medio in euro
- `mediaStelle`: Valutazione media (calcolata)
- `numeroRecensioni`: Conteggio recensioni
- `idRistoratore`: Username del proprietario (può essere null)

**Metodi principali**:
- `getId()`: Genera ID univoco (nome_citta)
- `getLocazione()`: Restituisce indirizzo completo
- `toStringDettagliato()`: Formato output dettagliato

#### Classe `Recenzione`
```java
public class Recenzione implements Serializable
```

**Responsabilità**:
- Rappresentare una recensione di un cliente
- Gestire la risposta del ristoratore

**Attributi principali**:
- `stelle`: Valutazione 1-5
- `testo`: Contenuto della recensione
- `dataOra`: Timestamp creazione
- `rispostaRistoratore`: Risposta (opzionale)
- `dataOraRisposta`: Timestamp risposta

**Metodi principali**:
- `hasRisposta()`: Verifica presenza risposta
- `getStelleSimbolo()`: Rappresentazione visiva (★★★☆☆)
- `setStelle(int)`: Con validazione 1-5

### 4.2 Package `gestione`

#### Classe `GestoreUtenti`
```java
public class GestoreUtenti
```

**Responsabilità**:
- Gestire registrazione e autenticazione utenti
- Persistenza su file CSV
- Mantenere riferimento all'utente corrente

**Attributi**:
- `List<Utente> utenti`: Lista in-memory degli utenti
- `Utente utenteCorrente`: Utente loggato

**Metodi principali**:
```java
boolean registrazione(String nome, String cognome, String username, 
                     String password, String ruolo, LocalDate dataNascita, 
                     String luogoDomicilio)
```
Registra un nuovo utente con password cifrata.

```java
boolean login(String username, String password)
```
Autentica un utente verificando password cifrata.

```java
void salvaUtenti()
```
Persiste gli utenti su file CSV.

```java
private void caricaUtenti()
```
Carica utenti da file CSV all'avvio.

#### Classe `GestoreRistoranti`
```java
public class GestoreRistoranti
```

**Responsabilità**:
- Gestire il database dei ristoranti
- Implementare funzionalità di ricerca avanzata
- Persistenza su file CSV

**Metodi di ricerca**:
```java
List<Ristorante> cercaPerCitta(String citta)
List<Ristorante> cercaPerTipoCucina(String tipoCucina, String citta)
List<Ristorante> cercaPerFasciaPrezzo(double min, double max, String citta)
List<Ristorante> cercaConDelivery(String citta)
List<Ristorante> cercaConPrenotazione(String citta)
List<Ristorante> cercaPerStelle(double stelleMin, String citta)
```

**Ricerca combinata**:
```java
List<Ristorante> cercaRistorante(String citta, String tipoCucina, 
                                 Double prezzoMin, Double prezzoMax, 
                                 Boolean delivery, Boolean prenotazione, 
                                 Double stelleMin)
```

Usa Java Stream API per filtraggio efficiente:
```java
return ristoranti.stream()
    .filter(r -> r.getCitta().toLowerCase().contains(citta.toLowerCase()))
    .filter(r -> tipoCucina == null || 
                 r.getTipoCucina().toLowerCase().contains(tipoCucina.toLowerCase()))
    // ... altri filtri
    .collect(Collectors.toList());
```

#### Classe `GestoreRecensioni`
```java
public class GestoreRecensioni
```

**Responsabilità**:
- Gestire CRUD delle recensioni
- Calcolare medie e statistiche
- Aggiornare valutazioni ristoranti
- Persistenza tramite serializzazione

**Metodi principali**:
```java
boolean aggiungiRecensione(String username, String idRist, int stelle, String testo)
boolean modificaRecensione(String username, String idRist, int stelle, String testo)
boolean eliminaRecensione(String username, String idRist)
boolean rispondiRecensione(String idRecensione, String risposta)
```

**Calcolo media**:
```java
public double calcolaMediaStelle(String idRistorante) {
    List<Recenzione> recensioni = getRecensioniRistorante(idRistorante);
    if (recensioni.isEmpty()) return 0.0;
    
    double somma = 0;
    for (Recenzione r : recensioni) {
        somma += r.getStelle();
    }
    return somma / recensioni.size();
}
```

Dopo ogni modifica, aggiorna automaticamente il ristorante:
```java
private void aggiornaValutazioneRistorante(String idRistorante) {
    double media = calcolaMediaStelle(idRistorante);
    int numero = getRecensioniRistorante(idRistorante).size();
    gestoreRistoranti.aggiornaValutazione(idRistorante, media, numero);
}
```

### 4.3 Package `util`

#### Classe `PasswordUtil`
```java
public class PasswordUtil
```

**Responsabilità**:
- Cifratura password con SHA-256
- Verifica password

**Implementazione SHA-256**:
```java
public static String cifraPassword(String password) {
    try {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
        
        // Converte in esadecimale
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    } catch (NoSuchAlgorithmException e) {
        throw new RuntimeException("Errore cifratura", e);
    }
}
```

### 4.4 Classe Principale `TheKnife`

**Responsabilità**:
- Entry point dell'applicazione
- Gestione interfaccia utente console
- Coordinamento tra gestori
- Routing delle funzionalità

**Struttura**:
```java
public class TheKnife {
    private static Scanner scanner;
    private static GestoreUtenti gestoreUtenti;
    private static GestoreRistoranti gestoreRistoranti;
    private static GestoreRecensioni gestoreRecensioni;
    private static String cittaCorrente;
    
    public static void main(String[] args) {
        // Inizializzazione gestori
        // Menu principale
    }
}
```

**Menu hierarchy**:
```
main()
  └─ menuPrincipale()
      ├─ registrazione()
      ├─ login()
      │   ├─ menuCliente()
      │   │   ├─ visualizzaPreferiti()
      │   │   ├─ menuRecensioniCliente()
      │   │   │   ├─ aggiungiRecensione()
      │   │   │   ├─ modificaRecensione()
      │   │   │   └─ eliminaRecensione()
      │   │   └─ ...
      │   └─ menuRistoratore()
      │       ├─ aggiungiRistorante()
      │       └─ visualizzaERispondiRecensioni()
      └─ modalitaOspite()
          ├─ visualizzaRistorantiVicini()
          └─ cercaRistoranti()
```

---

## 5. Persistenza Dati

### 5.1 File CSV per Utenti

**Percorso**: `data/utenti.dati`

**Formato**:
```csv
Nome,Cognome,Username,Password,Ruolo,DataNascita,LuogoDomicilio,Preferiti
Mario,Rossi,mario.rossi,5e884898da...f4c,cliente,1990-03-15,Milano,Rist1_Milano;Rist2_Roma
```

**Codifica**: UTF-8

**Parsing**:
```java
String[] parti = line.split(",", -1); // -1 mantiene campi vuoti
nome = parti[0];
cognome = parti[1];
// ...
if (parti.length > 7 && !parti[7].isEmpty()) {
    String[] preferiti = parti[7].split(";");
    // Carica preferiti
}
```

### 5.2 File CSV per Ristoranti

**Percorso**: `data/ristoranti.dati`

**Formato**:
```csv
Nome,Nazione,Citta,Indirizzo,Latitudine,Longitudine,TipoCucina,PrezzoMedio,Delivery,Prenotazione,IdRistoratore,MediaStelle,NumeroRecensioni
La Tavernetta,Italia,Milano,Via Mercato 28,45.464664,9.188540,Italiana,35.0,true,true,giuseppe.verdi,4.5,12
```

**Gestione header**: Prima riga ignorata se inizia con "Nome"

### 5.3 Serializzazione Recensioni

**Percorso**: `data/recensioni.dati`

**Formato**: Java Object Serialization

**Salvataggio**:
```java
try (ObjectOutputStream oos = new ObjectOutputStream(
        new FileOutputStream(FILE_RECENSIONI))) {
    oos.writeObject(recensioni);
}
```

**Caricamento**:
```java
try (ObjectInputStream ois = new ObjectInputStream(
        new FileInputStream(FILE_RECENSIONI))) {
    recensioni = (List<Recenzione>) ois.readObject();
}
```

**Vantaggi**:
- Preserva strutture complesse (LocalDateTime)
- Efficiente per dati binari
- Automatico con `Serializable`

**Svantaggi**:
- Non human-readable
- Dipendente da versione Java

### 5.4 Strategia di Salvataggio

**Quando vengono salvati i dati?**

1. **Utenti**: 
   - Dopo registrazione
   - Dopo modifica preferiti
   - All'uscita dall'applicazione

2. **Ristoranti**:
   - Dopo aggiunta ristorante
   - Dopo aggiornamento valutazione

3. **Recensioni**:
   - Dopo aggiunta/modifica/eliminazione recensione
   - Dopo risposta ristoratore

**All'uscita**:
```java
case 0:
    System.out.println("Grazie per aver usato TheKnife!");
    gestoreUtenti.salvaUtenti();
    gestoreRistoranti.salvaRistoranti();
    gestoreRecensioni.salvaRecensioni();
    System.exit(0);
```

---

## 6. Sicurezza

### 6.1 Cifratura Password

**Algoritmo**: SHA-256 (Secure Hash Algorithm 256-bit)

**Caratteristiche**:
- Hash unidirezionale (non reversibile)
- Output fisso di 256 bit (64 caratteri hex)
- Resistente a collisioni

**Esempio**:
```
Input:  "password123"
Output: "ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f"
```

**Flusso autenticazione**:
```
1. Utente inserisce password in chiaro
2. Sistema cifra con SHA-256
3. Confronta hash con hash memorizzato
4. Se corrispondono → autenticato
```

**Limitazioni attuali**:
- Nessun salt (vulnerabile a rainbow tables)
- SHA-256 non è specifico per password (preferibile bcrypt/Argon2)

**Miglioramenti futuri**:
- Aggiungere salt random per ogni utente
- Usare algoritmi specifici per password (bcrypt, PBKDF2, Argon2)
- Implementare rate limiting per tentativi login

### 6.2 Validazione Input

**Validazione stelle recensione**:
```java
public void setStelle(int stelle) {
    if (stelle < 1 || stelle > 5) {
        throw new IllegalArgumentException(
            "Le stelle devono essere comprese tra 1 e 5");
    }
    this.stelle = stelle;
}
```

**Gestione eccezioni input numerico**:
```java
private static int leggiIntero(String messaggio) {
    while (true) {
        try {
            System.out.print(messaggio);
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Inserisci un numero valido.");
        }
    }
}
```

### 6.3 Controllo Accessi

**Verifica login prima di operazioni sensibili**:
```java
if (!gestoreUtenti.isLoggato()) {
    System.out.println("Devi effettuare il login.");
    return;
}
```

**Verifica ruolo**:
```java
if (!utente.getRuolo().equals("ristoratore")) {
    System.out.println("Funzione disponibile solo per ristoratori.");
    return;
}
```

**Verifica proprietà ristorante**:
```java
if (!ristorante.getIdRistoratore().equals(utente.getUsername())) {
    System.out.println("Non hai i permessi per modificare questo ristorante.");
    return;
}
```

---

## 7. Flusso di Esecuzione

### 7.1 Avvio Applicazione

```
main()
  │
  ├─ Inizializza GestoreRistoranti
  │   └─ caricaRistoranti() da CSV
  │
  ├─ Inizializza GestoreUtenti
  │   └─ caricaUtenti() da CSV
  │
  ├─ Inizializza GestoreRecensioni
  │   └─ caricaRecensioni() da file serializzato
  │
  └─ menuPrincipale()
```

### 7.2 Flusso Login Cliente

```
login()
  │
  ├─ Chiede username e password
  │
  ├─ gestoreUtenti.login(username, password)
  │   ├─ Cerca utente per username
  │   ├─ Verifica password cifrata
  │   └─ Imposta utenteCorrente
  │
  └─ menuCliente()
      └─ Loop menu fino a logout
```

### 7.3 Flusso Aggiunta Recensione

```
aggiungiRecensione(idRistorante)
  │
  ├─ Verifica se già recensito
  │   └─ hasRecensione() → return false
  │
  ├─ Chiede stelle e testo
  │
  ├─ gestoreRecensioni.aggiungiRecensione()
  │   ├─ Crea nuova Recenzione
  │   ├─ Aggiunge alla lista
  │   ├─ aggiornaValutazioneRistorante()
  │   │   ├─ Calcola nuova media
  │   │   └─ gestoreRistoranti.aggiornaValutazione()
  │   └─ salvaRecensioni()
  │
  └─ Conferma operazione
```

### 7.4 Flusso Ricerca Ristoranti

```
cercaRistoranti()
  │
  ├─ Chiede criteri di ricerca
  │   ├─ città (obbligatorio)
  │   ├─ tipo cucina (opzionale)
  │   ├─ prezzo min/max (opzionale)
  │   ├─ delivery (opzionale)
  │   ├─ prenotazione (opzionale)
  │   └─ stelle min (opzionale)
  │
  ├─ gestoreRistoranti.cercaRistorante(criteri)
  │   └─ Stream API con filtri multipli
  │       ├─ filter per città
  │       ├─ filter per tipo cucina
  │       ├─ filter per prezzo
  │       ├─ filter per delivery
  │       ├─ filter per prenotazione
  │       ├─ filter per stelle
  │       └─ collect(Collectors.toList())
  │
  └─ Visualizza risultati
```

---

## 8. API e Interfacce

### 8.1 Interfacce Pubbliche dei Gestori

#### GestoreUtenti
```java
// Registrazione e autenticazione
public boolean registrazione(String nome, String cognome, String username, 
                             String password, String ruolo, 
                             LocalDate dataNascita, String luogoDomicilio)
public boolean login(String username, String password)
public void logout()

// Getter
public Utente getUtenteCorrente()
public boolean isLoggato()
public Utente cercaUtente(String username)
public List<Utente> getUtenti()

// Persistenza
public void salvaUtenti()
```

#### GestoreRistoranti
```java
// CRUD Ristoranti
public void aggiungiRistorante(Ristorante ristorante)
public Ristorante cercaRistorantePerId(String idRistorante)
public List<Ristorante> getTuttiRistoranti()
public List<Ristorante> getRistorantiPerRistoratore(String username)

// Ricerca
public List<Ristorante> cercaPerCitta(String citta)
public List<Ristorante> cercaPerTipoCucina(String tipo, String citta)
public List<Ristorante> cercaPerFasciaPrezzo(double min, double max, String citta)
public List<Ristorante> cercaConDelivery(String citta)
public List<Ristorante> cercaConPrenotazione(String citta)
public List<Ristorante> cercaPerStelle(double stelleMin, String citta)
public List<Ristorante> cercaRistorante(String citta, String tipoCucina, 
                                       Double prezzoMin, Double prezzoMax, 
                                       Boolean delivery, Boolean prenotazione, 
                                       Double stelleMin)

// Aggiornamenti
public void aggiornaValutazione(String idRist, double media, int numero)

// Persistenza
public void salvaRistoranti()
```

#### GestoreRecensioni
```java
// CRUD Recensioni
public boolean aggiungiRecensione(String username, String idRist, 
                                  int stelle, String testo)
public boolean modificaRecensione(String username, String idRist, 
                                  int nuoveStelle, String nuovoTesto)
public boolean eliminaRecensione(String username, String idRist)
public boolean rispondiRecensione(String idRecensione, String risposta)

// Query
public boolean hasRecensione(String username, String idRist)
public Recenzione getRecensione(String username, String idRist)
public List<Recenzione> getRecensioniRistorante(String idRist)
public List<Recenzione> getRecensioniCliente(String username)

// Statistiche
public double calcolaMediaStelle(String idRist)

// Visualizzazione
public void visualizzaRecensioni(String idRist)
public void visualizzaRiepilogo(String username, GestoreRistoranti gr)

// Persistenza
public void salvaRecensioni()
```

### 8.2 Contratti Interfacce

**Esempio: aggiungiRecensione**

**Pre-condizioni**:
- `username` deve esistere nel sistema
- `idRistorante` deve esistere
- `stelle` deve essere tra 1 e 5
- L'utente non deve aver già recensito il ristorante

**Post-condizioni**:
- Recensione aggiunta alla lista
- Media stelle ristorante aggiornata
- Numero recensioni incrementato
- Dati salvati su file

**Esempio: rispondiRecensione**

**Pre-condizioni**:
- Recensione deve esistere
- Non deve già esistere una risposta
- Utente deve essere il proprietario del ristorante

**Post-condizioni**:
- Risposta aggiunta alla recensione
- Timestamp risposta impostato
- Dati salvati su file

---

## 9. Compilazione e Deploy

### 9.1 Compilazione Manuale

**Windows**:
```batch
javac -d bin -encoding UTF-8 ^
  src/model/Ristorante.java ^
  src/model/Utente.java ^
  src/model/Recenzione.java ^
  src/util/PasswordUtil.java ^
  src/gestione/GestoreUtenti.java ^
  src/gestione/GestoreRistoranti.java ^
  src/gestione/GestoreRecensioni.java ^
  src/TheKnife.java
```

**Linux/Mac**:
```bash
javac -d bin -encoding UTF-8 \
  src/model/Ristorante.java \
  src/model/Utente.java \
  src/model/Recenzione.java \
  src/util/PasswordUtil.java \
  src/gestione/GestoreUtenti.java \
  src/gestione/GestoreRistoranti.java \
  src/gestione/GestoreRecensioni.java \
  src/TheKnife.java
```

**Note**:
- `-d bin`: Output directory per .class
- `-encoding UTF-8`: Gestione caratteri internazionali

### 9.2 Creazione JAR

**Prerequisiti**:
- File `MANIFEST.MF` configurato:
```
Manifest-Version: 1.0
Main-Class: TheKnife
Created-By: TheKnife Project
```

**Comando**:
```bash
cd bin
jar cfm ../TheKnife.jar ../MANIFEST.MF TheKnife.class model/*.class util/*.class gestione/*.class
```

**Verifica**:
```bash
jar tf TheKnife.jar    # Lista contenuto
java -jar TheKnife.jar # Esecuzione
```

### 9.3 Script Automatici

#### run.bat (Windows)
Esegue pulizia, compilazione e avvio. Se `data/ristoranti.dati` è vuoto o mancante, avvia l'importazione Michelin.

### 9.4 Requisiti Runtime

**Java Version**:
- Minimo: JRE 11
- Raccomandato: JRE 17 LTS
- Features utilizzate:
  - `java.time` API (LocalDate, LocalDateTime)
  - Stream API
  - Lambda expressions
  - Try-with-resources

**Dipendenze esterne**: Nessuna (solo Java SE)

---

## 10. Testing

### 10.1 Strategia di Test

**Test Manuali**:
Eseguire una checklist interna in base ai flussi utente principali.

**Categorie di test**:
1. Test funzionalità ospite
2. Test registrazione e login
3. Test funzionalità cliente
4. Test funzionalità ristoratore
5. Test persistenza dati
6. Test ricerca e filtraggio
7. Test validazione input
8. Test sicurezza

### 10.2 Test Case Critici

#### TC-001: Cifratura Password
```
Input: password = "test123"
Atteso: Hash SHA-256 salvato in utenti.dati
Verifica: Hash diverso da password chiara
```

#### TC-002: Prevenzione Recensione Duplicata
```
Scenario: Cliente tenta di recensire stesso ristorante due volte
Atteso: Seconda recensione rifiutata
Messaggio: "Hai già recensito questo ristorante"
```

#### TC-003: Calcolo Media Stelle
```
Input: 3 recensioni (5, 4, 3 stelle)
Atteso: Media = 4.0
Verifica: Arrotondamento a 1 decimale
```

#### TC-004: Persistenza Preferiti
```
Scenario: Aggiungi preferito → Chiudi app → Riapri → Verifica
Atteso: Preferito ancora presente
```

### 10.3 Test Edge Cases

**Stringhe vuote**:
- Nome ristorante vuoto
- Testo recensione vuoto

**Valori limite**:
- Stelle = 0, 1, 5, 6
- Prezzo = 0, negativo

**File corrotti**:
- CSV malformato
- File recensioni corrotto

**Concorrenza** (futuro):
- Accessi simultanei
- Race conditions

### 10.4 Debug e Logging

**Messaggi di errore informativi**:
```java
System.err.println("Errore nel caricamento dei ristoranti: " + e.getMessage());
```

**Gestione eccezioni**:
```java
try {
    // Operazione file
} catch (IOException e) {
    System.err.println("Errore I/O: " + e.getMessage());
    e.printStackTrace(); // Solo in fase di debug
}
```

**Aggiungere logging** (sviluppo futuro):
```java
import java.util.logging.*;

private static final Logger logger = Logger.getLogger(TheKnife.class.getName());
logger.info("Avvio applicazione");
logger.warning("Username non trovato: " + username);
logger.severe("Errore critico: " + e.getMessage());
```

---

## 11. Manutenzione

### 11.1 Backup Dati

**File da backuppare**:
```
data/
  ├── utenti.dati
  ├── ristoranti.dati
  └── recensioni.dati
```

**Strategia backup**:
1. Backup giornaliero automatico (script)
2. Backup prima di aggiornamenti
3. Versioning con timestamp

**Script backup (esempio)**:
```bash
#!/bin/bash
BACKUP_DIR="backups/$(date +%Y%m%d_%H%M%S)"
mkdir -p $BACKUP_DIR
cp -r data/ $BACKUP_DIR/
echo "Backup completato in $BACKUP_DIR"
```

### 11.2 Migrazione Dati

**Da vecchia versione a nuova**:

Se si aggiunge un campo:
1. Modificare parser per gestire campo mancante
2. Assegnare valore default
3. Migrare dati con script

**Esempio: Aggiunta campo "telefono" a Ristorante**:
```java
String telefono = parti.length > 13 ? parti[13] : "";
```

### 11.3 Monitoring

**Metriche da monitorare**:
- Numero utenti registrati
- Numero ristoranti nel database
- Numero recensioni
- Media valutazioni globale
- Utilizzo spazio file

**Script monitoring**:
```bash
#!/bin/bash
echo "=== TheKnife Statistics ==="
echo "Utenti: $(wc -l < data/utenti.dati)"
echo "Ristoranti: $(wc -l < data/ristoranti.dati)"
ls -lh data/
```

### 11.4 Troubleshooting Comune

| Problema | Causa | Soluzione |
|----------|-------|-----------|
| Errore "file not found" | Directory data/ mancante | Creare manualmente o al primo salvataggio |
| Caratteri strani | Encoding sbagliato | Usare UTF-8 in compilazione |
| Password non riconosciuta | Hash diverso | Ricreare utente |
| Recensioni perse | File corrotto | Ripristinare da backup |
| OutOfMemoryError | Troppi dati in memoria | Aumentare heap: `java -Xmx512m -jar TheKnife.jar` |

---

## 12. Sviluppi Futuri

### 12.1 Miglioramenti Tecnici

#### 12.1.1 Database Relazionale
Sostituire file CSV/serializzazione con DB:
- **SQLite**: Embedded, no server
- **PostgreSQL**: Per produzione
- **JPA/Hibernate**: ORM Java

**Vantaggi**:
- Query complesse efficienti
- Integrità referenziale
- Transazioni ACID
- Concorrenza gestita

#### 12.1.2 Architettura Client-Server
```
Client (GUI/Mobile)
      ↕
REST API (Spring Boot)
      ↕
Database (PostgreSQL)
```

**Tecnologie**:
- **Backend**: Spring Boot, JAX-RS
- **Frontend**: JavaFX, React, Angular
- **Mobile**: Android (Kotlin), iOS (Swift)
- **API**: REST JSON

#### 12.1.3 Autenticazione Avanzata
- JWT (JSON Web Tokens)
- OAuth 2.0 (login con Google, Facebook)
- Two-Factor Authentication (2FA)
- Session management

#### 12.1.4 Caching
```java
import java.util.concurrent.ConcurrentHashMap;

private ConcurrentHashMap<String, Ristorante> cacheRistoranti;
```

### 12.2 Nuove Funzionalità

#### 12.2.1 Sistema di Prenotazione
```java
class Prenotazione {
    private String idPrenotazione;
    private String usernameCliente;
    private String idRistorante;
    private LocalDateTime dataOra;
    private int numeroPersone;
    private String stato; // "confermata", "annullata"
}
```

#### 12.2.2 Geolocalizzazione
- Calcolo distanza da coordinate GPS
- "Ristoranti nei dintorni" entro X km
- Integrazione Google Maps API

```java
public double calcolaDistanza(double lat1, double lon1, 
                              double lat2, double lon2) {
    // Formula Haversine
}
```

#### 12.2.3 Filtri Avanzati
- Allergie e intolleranze
- Certificazioni (bio, vegano, halal, kosher)
- Accessibilità (disabili, bambini)
- Parcheggio disponibile

#### 12.2.4 Social Features
- Seguire altri utenti
- Feed recensioni amici
- Badge e achievement
- Classifica top reviewer

#### 12.2.5 Immagini
- Upload foto ristoranti
- Foto nelle recensioni
- Gallery

**Storage**:
- FileSystem locale: `data/images/`
- Cloud: AWS S3, Google Cloud Storage

#### 12.2.6 Notifiche
- Email conferma registrazione
- Notifica risposta ristoratore
- Newsletter settimanale

**Tecnologie**:
- JavaMail API
- SendGrid, Mailgun
- Firebase Cloud Messaging (push mobile)

### 12.3 Ottimizzazioni Performance

#### 12.3.1 Lazy Loading
Non caricare tutti i dati all'avvio:
```java
public List<Ristorante> getRistorantiPaginati(int page, int size) {
    int start = page * size;
    int end = Math.min(start + size, ristoranti.size());
    return ristoranti.subList(start, end);
}
```

#### 12.3.2 Indicizzazione
Per ricerche veloci:
```java
private Map<String, List<Ristorante>> indiceCitta;
private Map<String, List<Ristorante>> indiceTipoCucina;
```

Aggiornare indici dopo ogni modifica.

#### 12.3.3 Async I/O
Operazioni file non bloccanti:
```java
CompletableFuture.runAsync(() -> salvaUtenti());
```

### 12.4 Testing Automatizzato

#### JUnit 5
```java
@Test
public void testCifraturaPassword() {
    String password = "test123";
    String hash = PasswordUtil.cifraPassword(password);
    assertNotEquals(password, hash);
    assertEquals(64, hash.length()); // SHA-256 = 64 hex chars
}

@Test
public void testAggiungiRecensione() {
    GestoreRecensioni gr = new GestoreRecensioni(gestoreRistoranti);
    boolean result = gr.aggiungiRecensione("user1", "rist1", 5, "Ottimo!");
    assertTrue(result);
    assertEquals(1, gr.getRecensioniRistorante("rist1").size());
}
```

#### Integration Testing
Test end-to-end di flussi completi:
```java
@Test
public void testFlussoRegistrazioneLogin() {
    // 1. Registra utente
    // 2. Verifica salvataggio su file
    // 3. Effettua login
    // 4. Verifica sessione attiva
}
```

#### Test Coverage
Obiettivo: > 80% code coverage

Tools:
- JaCoCo
- Cobertura

### 12.5 CI/CD Pipeline

**Continuous Integration**:
```yaml
# .github/workflows/ci.yml
name: Java CI
on: [push, pull_request]
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Set up JDK 11
        uses: actions/setup-java@v2
        with:
          java-version: '11'
      - name: Compile
        run: javac -d bin -encoding UTF-8 src/**/*.java
      - name: Run tests
        run: java -jar junit-platform-console-standalone.jar --scan-classpath
```

**Continuous Deployment**:
- Automatizzare creazione JAR
- Deploy su server
- Docker containerization

### 12.6 Dockerizzazione

**Dockerfile**:
```dockerfile
FROM openjdk:11-jre-slim
WORKDIR /app
COPY TheKnife.jar .
COPY data/ data/
CMD ["java", "-jar", "TheKnife.jar"]
```

**Build e run**:
```bash
docker build -t theknife:latest .
docker run -it -v $(pwd)/data:/app/data theknife:latest
```

### 12.7 Scalabilità

#### Microservizi
Separare in servizi indipendenti:
- **User Service**: Autenticazione, profili
- **Restaurant Service**: CRUD ristoranti
- **Review Service**: Recensioni
- **Search Service**: Ricerca ottimizzata

#### Load Balancing
Distribuire carico tra più istanze:
```
           Load Balancer (Nginx)
              /     |     \
         App1     App2     App3
              \     |     /
               Database
```

#### Caching Distribuito
- Redis per cache condivisa
- Memcached

---

## 13. Conclusioni

### 13.1 Punti di Forza

✅ **Architettura pulita**: Separazione MVC
✅ **Persistenza dati**: File CSV + Serializzazione
✅ **Sicurezza**: Password cifrate SHA-256
✅ **Ricerca avanzata**: Filtri multipli combinabili
✅ **Interfaccia intuitiva**: Console ben strutturata
✅ **Javadoc completo**: Codice ben documentato
✅ **Multipiattaforma**: Java cross-platform

### 13.2 Limitazioni Attuali

⚠️ **Concorrenza**: Non gestita (single-user)
⚠️ **Scalabilità**: Tutti i dati in memoria
⚠️ **Database**: File-based, no SQL
⚠️ **UI**: Solo console, no GUI
⚠️ **Network**: No client-server
⚠️ **Backup**: Manuale

### 13.3 Metriche Progetto

- **Linee di codice**: ~2500 LOC
- **Classi**: 7 + 1 main
- **Metodi pubblici**: ~80
- **File configurazione**: 3 (CSV + 1 DAT)
- **Dipendenze esterne**: 0
- **Java Version**: 11+

### 13.4 Maintenance

**Tempo stimato manutenzione**:
- Backup dati: 5 min/settimana
- Monitoring: 10 min/settimana
- Bug fixes: Variabile
- Feature updates: Variabile

**Documentazione aggiornata**: Ottobre 2025

---

## Appendici

### A. Glossario

- **CRUD**: Create, Read, Update, Delete
- **DAO**: Data Access Object
- **CSV**: Comma-Separated Values
- **SHA-256**: Secure Hash Algorithm 256-bit
- **MVC**: Model-View-Controller
- **ORM**: Object-Relational Mapping
- **JWT**: JSON Web Token
- **REST**: Representational State Transfer
- **CI/CD**: Continuous Integration/Continuous Deployment

### B. Riferimenti

- [Java SE Documentation](https://docs.oracle.com/javase/)
- [Java Stream API](https://docs.oracle.com/javase/8/docs/api/java/util/stream/package-summary.html)
- [SHA-256 Algorithm](https://en.wikipedia.org/wiki/SHA-2)
- [MVC Pattern](https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93controller)

### C. Contatti Sviluppatori

**Progetto sviluppato per**: Laboratorio Interdisciplinare A/B
**Università**: Università degli Studi dell'Insubria
**Anno Accademico**: 2024/2025

---

**Fine Manuale Tecnico**
