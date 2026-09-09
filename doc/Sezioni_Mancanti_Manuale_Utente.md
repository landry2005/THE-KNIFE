# Sezioni da aggiungere al Manuale Utente

Il Manuale Utente attuale si ferma alla sezione 8 (Domande Frequenti).
Secondo la struttura richiesta dal corso, mancano le sezioni "Limiti
della soluzione sviluppata" e "Sitografia / Bibliografia". Di seguito
il testo pronto da incollare nel documento sorgente, per poi
rigenerare il PDF.

Aggiornare anche l'Indice aggiungendo le voci 9 e 10.

---

## 9. Limiti della soluzione sviluppata

L'applicazione TheKnife copre tutte le funzionalità richieste dalle
specifiche di progetto, ma presenta alcuni limiti noti, di cui è
importante essere consapevoli:

- **Ricerca per città esatta.** La ricerca dei ristoranti "vicini"
  si basa sulla corrispondenza testuale del campo città e non su un
  calcolo di distanza geografica reale (nessuna geocodifica delle
  coordinate). Un utente che indica un nome di località scritto in
  modo diverso da come è memorizzato a database potrebbe non
  ottenere risultati.

- **Una sola recensione per utente per ristorante.** Coerentemente
  con le specifiche, un cliente può lasciare al massimo una
  recensione per ogni ristorante; per aggiornare il proprio giudizio
  deve modificarla, non può inserirne una nuova.

- **Nessun caricamento di immagini.** I ristoranti non possono
  essere corredati da foto: le informazioni mostrate sono solo
  testuali (nome, luogo, fascia di prezzo, tipo di cucina, servizi).

- **Recupero password tramite domanda di sicurezza.** Non è previsto
  un sistema di recupero password via e-mail; il reset avviene
  rispondendo alla domanda di sicurezza scelta in fase di
  registrazione.

- **Assenza di paginazione.** I risultati di ricerca vengono
  restituiti e mostrati tutti insieme nella tabella; con un numero
  molto elevato di ristoranti le prestazioni dell'interfaccia
  potrebbero risentirne.

- **Protocollo di rete senza sessione persistente lato server.**
  Ogni richiesta client-server apre e chiude una propria connessione
  socket; lo stato di login è mantenuto esclusivamente lato client
  (`SessionManager`). Questa scelta semplifica la gestione della
  concorrenza ma richiede che il client invii sempre l'identificativo
  dell'utente nelle richieste che lo richiedono.

- **Interfaccia disponibile solo in italiano.**
  Non è prevista internazionalizzazione dei testi dell'interfaccia.

- **Assenza di una suite di test automatici.** Il progetto non
  include test unitari (es. JUnit); la verifica del comportamento è
  stata effettuata tramite test manuali delle funzionalità.


## 10. Sitografia / Bibliografia

- PostgreSQL, *PostgreSQL: The World's Most Advanced Open Source
  Relational Database*, Online: https://www.postgresql.org

- PostgreSQL Global Development Group, *PostgreSQL JDBC Driver*,
  Online: https://jdbc.postgresql.org

- Gluon / OpenJFX, *JavaFX Documentation*,
  Online: https://openjfx.io

- Apache Software Foundation, *Apache Maven Project*,
  Online: https://maven.apache.org

- Oracle, *How to Write Doc Comments for the Javadoc Tool*,
  Online: https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html

- Ng Shi Heng, *Michelin Guide Restaurants (dataset)*, Kaggle,
  Online: https://www.kaggle.com/datasets/ngshiheng/michelin-guide-restaurants-2021

- G. Meroni, *Laboratorio Interdisciplinare B - Specifiche di
  Progetto*, Università degli Studi dell'Insubria, a.a. 2024/2025
