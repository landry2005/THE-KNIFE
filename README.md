# TheKnife - Piattaforma di Ricerca Ristoranti

## Descrizione
TheKnife è una piattaforma che consente di trovare ristoranti in tutto il mondo e selezionarli in base al luogo, alla tipologia del ristorante stesso, alla fascia di prezzo, alla possibilità o meno di prenotare un tavolo o di ordinare da asporto. TheKnife simula alcune delle funzionalità della celebre piattaforma TheFork.

## Autori
- **Scafidi Michaela** - 760101 - VA
- **Wafo Tene Wilfried Landry** - 763687 - VA
- **Fotso Alex Castany** - 762919 - VA

## Requisiti di Sistema
- **Java:** JDK 17 o superiore.
- **Database:** PostgreSQL 14 o superiore.
- **Build Tool:** Apache Maven 3.6 o superiore.
- **Sistema Operativo:** Windows, Linux, macOS.

## Struttura del Progetto (Maven Multi-Module)
Il progetto è strutturato in moduli Maven per separare nettamente le responsabilità:

```text
THE-KNIFE/
├── pom.xml                         # POM padre (packaging: pom)
├── common/                         # Modulo Core (Logica e Dati condivisi)
│   ├── pom.xml                     # Dipendenze: PostgreSQL JDBC
│   └── src/main/java/theknife/
│       ├── dao/                    # Data Access Object (Interazione DB)
│       │   ├── DatabaseConnection.java
│       │   ├── UtenteDAO.java
│       │   ├── RistoranteDAO.java
│       │   ├── RecensioneDAO.java
│       │   └── PreferitoDAO.java
│       ├── gestione/               # Business Logic Layer
│       │   ├── GestoreUtenti.java
│       │   ├── GestoreRistoranti.java
│       │   └── GestoreRecensioni.java
│       ├── model/                  # Data Model (POJO)
│       │   ├── Utente.java
│       │   ├── Ristorante.java
│       │   └── Recenzione.java
│       ├── network/                # Protocollo di comunicazione Client/Server
│       │   ├── Request.java
│       │   ├── Response.java
│       │   ├── RequestType.java
│       │   └── SearchCriteria.java
│       └── util/                   # Utility (Hashing, Import CSV)
│           ├── PasswordUtil.java
│           └── ImportaMichelin.java
├── server/                         # Modulo Server (Socket TCP e Concorrenza)
│   └── src/main/java/theknife/server/
│       ├── ServerMain.java         # Entry point del server (in ascolto sulla porta 5000)
│       └── ClientHandler.java      # Gestione thread per ogni client connesso
├── client/                         # Modulo Client (Interfaccia Grafica JavaFX)
│   └── src/main/java/theknife/client/
│       ├── ServerConnection.java   # Gestione connessione socket al server
│       ├── SessionManager.java     # Stato sessione utente lato client
│       └── gui/                    # Interfacce grafiche JavaFX
│           ├── TheKnifeApp.java    # Entry point della GUI
│           ├── SceneManager.java   # Gestore navigazione tra schermate
│           ├── LoginView.java
│           ├── RegisterView.java
│           └── SearchView.java
├── db/                            # Script di creazione database
│   └── schema.sql
└── data/                           # Dataset di origine (CSV Michelin)
    └── michelin_my_maps.csv
```

## Installazione e Configurazione

### 1. Setup del Database PostgreSQL
1. Avviare PostgreSQL e creare un database vuoto (es. `theknifedb`).
2. Eseguire lo script `sql/schema.sql` tramite pgAdmin per creare le tabelle (`utenti`, `ristoranti`, `recensioni`, `preferiti`), i vincoli e gli indici.

### 2. Configurazione delle Credenziali
Nella cartella radice del progetto, creare un file nominato `db.properties.env` con la seguente struttura:
```properties
db.host=localhost
db.port=5432
db.name=theknifedb
db.user=postgres
db.password=latuapassword
```
*(Nota: Questo file è inserito nel `.gitignore` per motivi di sicurezza).*

### 3. Importazione Dati Michelin (Opzionale ma consigliato)
Per popolare il database con i ristoranti della Guida Michelin:
1. Posizionare il dataset `michelin_my_maps.csv` in una cartella `data/` nella root del progetto.
2. Eseguire la classe di importazione tramite Maven:
   ```bash
   mvn clean compile exec:java -pl common "-Dexec.mainClass=theknife.util.ImportaMichelin"
   ```

## Compilazione e Avvio

Essendo un'applicazione distribuita, è necessario avviare separatamente il Server e il Client.

### 1. Compilazione generale
Per compilare l'intero progetto e installare i moduli in locale:
```bash
mvn clean install
```

### 2. Avvio del Server (Modulo `serverTK`)
Avvia il server in ascolto sulla porta 5000:
```bash
mvn exec:java -pl server "-Dexec.mainClass=theknife.server.ServerMain"
```

### 3. Avvio del Client GUI (Modulo `clientTK`)
Apri un **nuovo terminale** e avvia l'interfaccia grafica JavaFX:
```bash
mvn javafx:run -pl client
```

## Funzionalità

### Utenti Non Registrati (Ospiti)
- Visualizzare i dettagli dei ristoranti (luogo, fascia di prezzo, servizi).
- Visualizzare le recensioni dei ristoranti in forma anonima.
- Registrarsi all'applicazione come cliente o gestore dei ristoranti.

### Utenti Registrati (Clienti)
- Tutte le funzionalità degli ospiti.
- Definire e visualizzare una lista di ristoranti preferiti.
- Inserire recensioni per un ristorante (stelle 1-5 + testo).
- Modificare e cancellare le proprie recensioni.
- Cambio password e recupero tramite domanda di sicurezza.

### Utenti Registrati (Ristoratori)
- Inserire nuovi ristoranti nel database.
- Visualizzare le recensioni relative ai propri ristoranti.
- Rispondere alle recensioni (massimo una risposta per recensione).
- Visualizzare la valutazione media e il numero di recensioni dei propri locali.

## Ricerca Ristoranti
La piattaforma offre diverse modalità di ricerca combinate:
- Per locazione geografica (obbligatoria).
- Per tipologia di cucina.
- Per fascia di prezzo (minimo e massimo).
- In base alla disponibilità del servizio di delivery.
- In base alla disponibilità del servizio di prenotazione online.
- Per media del numero di stelle.

## Persistenza e Sicurezza Dati
- **Database Relazionale:** I dati sono gestiti da PostgreSQL. L'accesso avviene tramite il pattern DAO e query parametriche (`PreparedStatement`) per prevenire SQL Injection.
- **Password Cifrate:** Le password e le risposte di sicurezza vengono cifrate utilizzando l'algoritmo **SHA-256** prima di essere inviate al database. Non vengono mai memorizzate in chiaro.

## Sviluppi Futuri
- Implementazione di un sistema di prenotazione tavoli integrato.
- Geolocalizzazione avanzata per il calcolo delle distanze reali.
- Introduzione di foto e gallery per i ristoranti.

## Licenza
Progetto didattico per il corso di Laboratorio Interdisciplinare B - Università degli Studi dell'Insubria.
