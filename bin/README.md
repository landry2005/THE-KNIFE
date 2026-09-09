# Cartella bin/

Contiene i due eseguibili `.jar` dell'applicazione, come richiesto
dalle specifiche del progetto (Il Progetto – Consegna).

Attenzione: al momento i `pom.xml` di `server` e `client` producono
`server-1.0-SNAPSHOT.jar` e `client-1.0-SNAPSHOT.jar` in
`target/`, con nomi diversi da `serverTK.jar`/`clientTK.jar`. È
stato quindi aggiunto un tag `<finalName>` in entrambi i pom.xml:
da ora, un `mvn clean package` produce direttamente
`server/target/serverTK.jar` e `client/target/clientTK.jar`, pronti
per essere copiati qui senza bisogno di rinominarli a mano:

    mvn clean package
    cp server/target/serverTK.jar bin/
    cp client/target/clientTK.jar bin/
