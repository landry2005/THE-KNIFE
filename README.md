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
│   └── src/main/java/theknife/
│       ├── dao/                    # Data Access Object (Interazione DB)
│       ├── gestione/               # Business Logic Layer
│       ├── model/                  # Data Model (POJO)
│       ├── network/                # Protocollo di comunicazione Client/Server
│       └── util/                   # Utility (Hashing, Import CSV)
├── server/                         # Modulo Server (Socket TCP e Concorrenza)
│   └── src/main/java/theknife/server/
├── client/                         # Modulo Client (Interfaccia Grafica JavaFX)
│   └── src/main/java/theknife/client/
├── bin/                            # File eseguibili .jar per la consegna
├── db/                            # Script di creazione database
└── doc/                            # Manuali, Diagrammi UML/ER e Javadoc
```

## Installazione e Configurazione

### 1. Setup del Database PostgreSQL
1. Avviare PostgreSQL e creare un database vuoto (es. `theknifedb`).
2. Eseguire lo script `db/schema.sql` tramite pgAdmin per creare le tabelle, i vincoli e gli indici.

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

## Avvio dell'Applicazione

L'applicazione è un sistema distribuito Client/Server. È possibile avviarla tramite i file `.jar` precompilati o direttamente tramite Maven dal codice sorgente. In entrambi i casi, è necessario avviare il Server e il Client in due terminali separati.

### Metodo 1: Avvio tramite File JAR (Consigliato)
Nella cartella `bin/` sono presenti i due file eseguibili `.jar` (con tutte le dipendenze incluse).

1. **Avvio del Server:**
   Aprire un terminale, navigare nella cartella `bin/` ed eseguire:
   ```bash
   java -jar serverTK.jar
   ```
   *(Il terminale mostrerà "Server in ascolto sulla porta 5000". Lasciare il terminale aperto).*

2. **Avvio del Client (GUI):**
   Aprire un **nuovo terminale**, navigare nella cartella `bin/` ed eseguire:
   ```bash
   java -jar clientTK.jar
   ```

### Metodo 2: Avvio tramite Maven (Sviluppo)
Per compilare e avviare l'applicazione direttamente dal codice sorgente:

1. **Compilazione iniziale (nella root del progetto):**
   ```bash
   mvn clean install
   ```

2. **Avvio del Server:**
   ```bash
   mvn exec:java -pl server "-Dexec.mainClass=theknife.server.ServerMain"
   ```

3. **Avvio del Client (GUI):**
   Aprire un nuovo terminale ed eseguire:
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

## Sicurezza
- **Password Cifrate:** Le password e le risposte di sicurezza vengono cifrate utilizzando l'algoritmo **SHA-256** prima di essere inviate al database.
- **SQL Injection:** L'accesso al database avviene tramite `PreparedStatement` per prevenire iniezioni SQL.

## Licenza
Progetto didattico per il corso di Laboratorio Interdisciplinare B - Università degli Studi dell'Insubria.
