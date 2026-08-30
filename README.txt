==================================================================
PROGETTO LABORATORIO INTERDISCIPLINARE B - TheKnife
Università degli Studi dell'Insubria
==================================================================

1. REQUISITI PRELIMINARI
------------------------------------------------------------------
Assicurarsi di avere installato sul proprio sistema:
- Java Development Kit (JDK) versione 17 o superiore.
- Apache Maven versione 3.6 o superiore.
- PostgreSQL (versione 14 o superiore) installato e in esecuzione.


2. CONFIGURAZIONE DEL DATABASE
------------------------------------------------------------------
Prima di avviare l'applicazione, è necessario configurare il database:

a) Aprire pgAdmin (o psql) e creare un nuovo database vuoto 
   chiamato "theknifedb".

b) Eseguire lo script SQL presente in:
   db/schema.sql
   Questo creerà le tabelle (utenti, ristoranti, recensioni, 
   preferiti) e tutti i vincoli necessari.

c) Creare un file chiamato "db.properties.env" nella cartella 
   principale del progetto (la stessa dove si trova il pom.xml).
   Inserire all'interno le credenziali del proprio PostgreSQL:
   
   db.host=localhost
   db.port=5432
   db.name=theknifedb
   db.user=postgres
   db.password=latuapassword


3. IMPORTAZIONE DATI MICHELIN (Opzionale ma consigliato)
------------------------------------------------------------------
Per popolare il database con i ristoranti della Guida Michelin:

a) Posizionare il file "michelin_my_maps.csv" in una cartella 
   chiamata "data/" nella root del progetto.

b) Eseguire la classe di importazione tramite Maven con il comando:
   mvn clean compile exec:java -pl common "-Dexec.mainClass=theknife.util.ImportaMichelin"


4. COMPILAZIONE E AVVIO DELL'APPLICAZIONE
------------------------------------------------------------------
L'applicazione è strutturata come progetto Maven Multi-Modulo 
(common, server, client).

Per compilare l'intero progetto e installare le dipendenze in locale:
mvn clean install

L'applicazione richiede l'avvio separato di Server e Client.

A) AVVIO DEL SERVER (Modulo serverTK):
Per avviare il server in ascolto sulla porta 5000:
mvn exec:java -pl server "-Dexec.mainClass=theknife.server.ServerMain"

B) AVVIO DEL CLIENT (Modulo clientTK - Interfaccia Grafica JavaFX):
Aprire un nuovo terminale e lanciare:
mvn javafx:run -pl client


5. STRUTTURA DELLE CARTELLE
------------------------------------------------------------------
- /common       -> Contiene il codice sorgente, i Model, i DAO 
                   e la logica di business.
- /server       -> Modulo server per la gestione concorrente (Socket TCP).
- /client       -> Modulo client con interfaccia grafica (JavaFX).
- /db          -> Contiene lo script schema.sql per il database.
- /doc          -> Manuale Utente, Manuale Tecnico e Diagrammi UML/ER.

==================================================================