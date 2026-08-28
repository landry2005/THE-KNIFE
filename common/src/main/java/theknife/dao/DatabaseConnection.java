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
     * Stabilisce e restituisce una connessione al database utilizzando le proprietà specificate nel file di configurazione.
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

        // Carica le proprietà dal file di configurazione
        Properties props = new Properties();
        try(FileReader fr = new FileReader(PROPS_PATH)) {
            props.load(fr);
        } catch (IOException e) {
            throw new SQLException("Errore nel caricamento del file di configurazione: " + PROPS_PATH, e);
        }

        String jdbcUrl = "jdbc:postgresql://" + props.getProperty("db.host") + ":" + props.getProperty("db.port") + "/" + props.getProperty("db.name");
        String user = props.getProperty("db.user");
        String password = props.getProperty("db.password");

        // Stabilisce la connessione al database
        return DriverManager.getConnection(jdbcUrl, user, password);
    }
        
}
