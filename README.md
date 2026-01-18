# TheKnife - Piattaforma di Ricerca Ristoranti

## Descrizione
TheKnife è una piattaforma che consente di trovare ristoranti in tutto il mondo e selezionarli in base al luogo, alla tipologia del ristorante stesso, alla fascia di prezzo, alla possibilità o meno di prenotare un tavolo o di ordinare da asporto. TheKnife simula alcune delle funzionalità della celebre piattaforma TheFork.

## Autori
**[Inserire Nome Cognome - Matricola - Sede]**


## Requisiti
- Java JDK 11 o superiore
- Sistema operativo: Windows, Linux, macOS

## Struttura del Progetto
```
THE-KNIFE/
├── src/                    # Codice sorgente
│   ├── model/             # Classi del modello
│   │   ├── Ristorante.java
│   │   ├── Utente.java
│   │   └── Recenzione.java
│   ├── gestione/          # Classi di gestione dati
│   │   ├── GestoreRistoranti.java
│   │   ├── GestoreUtenti.java
│   │   └── GestoreRecensioni.java
│   ├── util/              # Classi di utilità
│   │   ├── PasswordUtil.java
│   │   └── ImportaMichelin.java
│   └── TheKnife.java       # Classe principale
├── bin/                    # File compilati (.class)
├── data/                   # File di persistenza dati
│   ├── ristoranti.dati     # Database ristoranti
│   ├── utenti.dati         # Database utenti
│   └── recensioni.dati     # Database recensioni (serializzato)
├── run.bat                 # Script di esecuzione (Windows)
└── README.md               # Questo file
```

## Importazione Dati Michelin (Opzionale)

Se hai il file `michelin_my_maps.csv` dalla Michelin Guide, puoi importare automaticamente tutti i ristoranti:

Il file `michelin_my_maps.csv` deve trovarsi in `data/`.

`run.bat` compila il progetto e, se `data/ristoranti.dati` è vuoto o mancante, avvia automaticamente l'importazione Michelin.

In alternativa, puoi importare manualmente dopo la compilazione:
```batch
java -cp bin util.ImportaMichelin
```

## Compilazione

### Windows
Eseguire il file `run.bat` (compila automaticamente).

### Linux/Mac
```bash
javac -d bin -encoding UTF-8 src/model/*.java src/util/*.java src/gestione/*.java src/TheKnife.java
```

## Esecuzione

### Da file JAR (dopo la compilazione)
```bash
java -jar TheKnife.jar
```

### Da classi compilate
#### Windows
```batch
run.bat
```

## Funzionalità

### Utenti Non Registrati (Ospiti)
- Visualizzare i dettagli dei ristoranti (luogo, fascia di prezzo, servizi)
- Visualizzare le recensioni dei ristoranti in forma anonima
- Registrarsi all'applicazione come cliente o gestore dei ristoranti

### Utenti Registrati (Clienti)
- Tutte le funzionalità degli ospiti
- Definire e visualizzare una lista di ristoranti preferiti
- Inserire recensioni per un ristorante (stelle 1-5 + testo)
- Modificare e cancellare le proprie recensioni
- **Cambio password** dal menu utente ⭐ NUOVO
- **Recupero password** con domanda di sicurezza

### Utenti Registrati (Ristoratori)
- Inserire nuovi ristoranti
- Visualizzare le recensioni relative ai propri ristoranti
- Rispondere alle recensioni (massimo una risposta per recensione)
- Visualizzare la valutazione media dei propri ristoranti
- **Cambio password** dal menu utente ⭐ NUOVO
- **Recupero password** con domanda di sicurezza

### Amministratore
- Le funzioni amministrative sono presenti nel codice ma non esposte nel menu utente.

## Ricerca Ristoranti
La piattaforma offre diverse modalità di ricerca:
- Per tipologia di cucina
- Per locazione geografica (obbligatoria)
- Per fascia di prezzo (es. "minore di 30€", "tra 20€ e 50€")
- In base alla disponibilità del servizio di delivery
- In base alla disponibilità del servizio di prenotazione online
- Per media del numero di stelle
- Combinazione dei criteri precedenti

## Persistenza Dati
- **Ristoranti**: Salvati in formato dati (`data/ristoranti.dati`)
- **Utenti**: Salvati in formato dati (`data/utenti.dati`)
  - Le password sono cifrate usando SHA-256
- **Recensioni**: Salvate tramite serializzazione Java (`data/recensioni.dati`)

## Formato File dati Ristoranti
```dati
Nome,Nazione,Citta,Indirizzo,Latitudine,Longitudine,TipoCucina,PrezzoMedio,Delivery,Prenotazione,IdRistoratore,MediaStelle,NumeroRecensioni
```

## Formato File dati Utenti
```dati
Nome,Cognome,Username,Password(cifrata),Ruolo,DataNascita,LuogoDomicilio,DomandaSicurezza,RispostaSicurezza,Preferiti(separati da ;)
```

## Sicurezza

### Password Cifrate
Le password degli utenti sono cifrate utilizzando l'algoritmo SHA-256 prima di essere salvate nel database. Non vengono mai memorizzate in chiaro.

### Recupero Password
Ogni utente imposta una **domanda di sicurezza** durante la registrazione. In caso di password dimenticata:
1. Seleziona "Password dimenticata?" dal menu principale
2. Inserisci username
3. Rispondi alla domanda di sicurezza
4. Imposta una nuova password

Le risposte alle domande di sicurezza sono anch'esse cifrate con SHA-256.


## Note Tecniche
- Il progetto utilizza Java 11+ con supporto per Stream API
- Interfaccia a linea di comando (CLI)
- Supporto completo per caratteri UTF-8
- Gestione errori robusta con validazione input

## Guida Rapida
1. Al primo avvio, registrarsi come cliente o ristoratore
2. Effettuare il login con le credenziali create
3. Esplorare le funzionalità disponibili nel menu

### Esempio di Utilizzo
```
1. Registrarsi come cliente
2. Effettuare login
3. Cercare ristoranti per città (es. "Milano")
4. Visualizzare dettagli di un ristorante
5. Aggiungere il ristorante ai preferiti
6. Lasciare una recensione
```

## Credenziali di Test
Per testare rapidamente l'applicazione, è possibile creare degli utenti di test attraverso il menu di registrazione.

## Sviluppi Futuri
- Interfaccia grafica (GUI)
- Architettura client/server
- Gestione accessi concorrenti
- Sistema di prenotazione integrato
- Geolocalizzazione avanzata

## Licenza
Progetto didattico per il corso di Laboratorio Interdisciplinare - Università degli Studi dell'Insubria

## Contatti
Per informazioni o segnalazioni, contattare gli autori del progetto.