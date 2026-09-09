# Cartella lib/

Il progetto usa Maven (con il plugin maven-shade-plugin) per creare
dei "fat jar" auto-contenuti: tutte le dipendenze esterne (driver
JDBC PostgreSQL, JavaFX, ecc.) sono già incluse dentro
`serverTK.jar` e `clientTK.jar`.

Questa cartella resta quindi vuota nel caso standard. Va popolata
solo se in futuro si aggiungono librerie non gestibili da Maven
(es. .jar non pubblicati su repository, da referenziare manualmente
nel classpath), come richiesto dalla struttura di consegna del
progetto.
