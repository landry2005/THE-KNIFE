==================================================================
PROGETTO LABORATORIO INTERDISCIPLINARE B - TheKnife
Università degli Studi dell'Insubria
==================================================================

1. REQUISITI PRELIMINARI
------------------------------------------------------------------
Assicurarsi di avere installato sul proprio sistema:
- Java Development Kit (JDK) versione 17 o superiore.
- Apache Maven (versione 3.6 o superiore) se si vuole compilare da sorgente.
- PostgreSQL (versione 14 o superiore) installato e in esecuzione.


2. CONFIGURAZIONE DEL DATABASE
------------------------------------------------------------------
Prima di avviare l'applicazione, è necessario configurare il database:

a) Aprire pgAdmin (o psql) e creare un nuovo database vuoto 
   chiamato "theknifedb".

b) Eseguire lo script SQL presente nella cartella:
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


3. AVVIO DELL'APPLICAZIONE
------------------------------------------------------------------
L'applicazione è un sistema Client/Server e richiede l'avvio 
separato di Server e Client su due terminali diversi.

METODO A: AVVIO TRAMITE FILE JAR (Consigliato)
I file eseguibili .jar si trovano nella cartella "bin/".

A1) Avvio del Server:
    Aprire un terminale, posizionarsi nella cartella "bin/" ed eseguire:
    java -jar serverTK.jar

A2) Avvio del Client (GUI):
    Aprire un NUOVO terminale, posizionarsi in "bin/" ed eseguire:
    java -jar clientTK.jar


METODO B: AVVIO TRAMITE MAVEN (Da codice sorgente)

B1) Compilare l'intero progetto dalla root:
    mvn clean install

B2) Avvio del Server:
    mvn exec:java -pl server "-Dexec.mainClass=theknife.server.ServerMain"

B3) Avvio del Client (GUI) su un nuovo terminale:
    mvn javafx:run -pl client


4. STRUTTURA DELLE CARTELLE
------------------------------------------------------------------
- /bin        -> Contiene i file eseguibili .jar (Server e Client).
- /common     -> Contiene il codice sorgente, i Model, i DAO 
                 e la logica di business.
- /server     -> Codice sorgente del server (Socket TCP).
- /client     -> Codice sorgente del client (Interfaccia JavaFX).
- /db        -> Contiene lo script schema.sql per il database.
- /doc        -> Manuale Utente, Manuale Tecnico, Diagrammi e Javadoc.

==================================================================