package theknife.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.FileReader;
import java.io.IOException;

/**
 * Classe utility per la gestione e l'ottenimento delle connessioni al database PostgreSQL tramite JDBC.
 * 
 * @author Scafidi Michaela - 760101 - VA
 * @author Wafo Tene Wilfried Landry - 763687 - VA
 * @author Fotso Alex Castany - 762919 - VA
 */

public class DatabaseConnection {

    private static final String PROPS_PATH = "db.properties.env";

    /**
     * Credenziali e host del database eventualmente specificati al
     * lancio di serverTK (riga di comando o prompt interattivo).
     * Se impostati, hanno priorità sul file {@code db.properties.env}.
     */
    private static String host;
    private static String port;
    private static String dbName;
    private static String user;
    private static String password;

    /**
     * Permette di specificare esplicitamente le credenziali e
     * l'host del database, tipicamente al lancio di serverTK,
     * invece di leggerli dal file di configurazione.
     *
     * @param host     host del DBMS PostgreSQL
     * @param port     porta del DBMS PostgreSQL
     * @param dbName   nome del database
     * @param user     utente per la connessione
     * @param password password dell'utente
     */
    public static void configure(String host, String port,
            String dbName, String user, String password) {

        DatabaseConnection.host = host;
        DatabaseConnection.port = port;
        DatabaseConnection.dbName = dbName;
        DatabaseConnection.user = user;
        DatabaseConnection.password = password;
    }

    /**
     * Stabilisce e restituisce una connessione al database. Se le
     * credenziali sono state impostate tramite {@link #configure},
     * usa quelle; altrimenti le legge dal file di configurazione
     * {@code db.properties.env}.
     * @return oggetto Connection attivo
     * @throws SQLException se si verifica un errore durante la connessione al database
     */

       public static Connection getConnection() throws SQLException {
        // FORZA IL CARICAMENTO DEL DRIVER POSTGRESQL
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver PostgreSQL non trovato!", e);
        }

        String dbHost = host;
        String dbPort = port;
        String dbNameLocal = dbName;
        String dbUser = user;
        String dbPassword = password;

        if (dbHost == null || dbUser == null) {

            // Nessuna configurazione esplicita fornita al lancio:
            // si ricorre al file di configurazione.
            Properties props = new Properties();
            try(FileReader fr = new FileReader(PROPS_PATH)) {
                props.load(fr);
            } catch (IOException e) {
                throw new SQLException("Errore nel caricamento del file di configurazione: " + PROPS_PATH, e);
            }

            dbHost = props.getProperty("db.host");
            dbPort = props.getProperty("db.port");
            dbNameLocal = props.getProperty("db.name");
            dbUser = props.getProperty("db.user");
            dbPassword = props.getProperty("db.password");
        }

        String jdbcUrl = "jdbc:postgresql://" + dbHost + ":" + dbPort + "/" + dbNameLocal;

        // Stabilisce la connessione al database
        return DriverManager.getConnection(jdbcUrl, dbUser, dbPassword);
    }
        
}
