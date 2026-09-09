package theknife.server;

import theknife.dao.DatabaseConnection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * Punto di ingresso del server TheKnife. Al lancio permette di
 * specificare l'host e le credenziali di accesso al database
 * (tramite argomenti da riga di comando oppure, in loro assenza,
 * tramite prompt interattivo); se nessuna delle due modalità viene
 * usata, resta disponibile la configurazione da file
 * {@code db.properties.env}, letta automaticamente da
 * {@link DatabaseConnection}.
 *
 * @author Scafidi Michaela - 760101 - VA
 * @author Wafo Tene Wilfried Landry - 763687 - VA
 * @author Fotso Alex Castany - 762919 - VA
 */

public class ServerMain {

    private static final int PORT = 5000;

    public static void main(String[] args) {

        configuraDatabase(args);

        System.out.println("Avvio del server TheKnife...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            System.out.println("Server in ascolto sulla porta " + PORT);

            while (true) {

               Socket clientSocket = serverSocket.accept();

System.out.println(
    "Nuovo client connesso: "
    + clientSocket.getInetAddress()
);

ClientHandler handler = new ClientHandler(clientSocket);

Thread thread = new Thread(handler);

thread.start();
            }

        } catch (IOException e) {
            System.err.println(
                "Errore del server: " + e.getMessage()
            );
        }
    }

    /**
     * Configura l'accesso al database in base agli argomenti da
     * riga di comando. Formato atteso:
     * {@code --host <host> --port <port> --db <nome> --user <utente> --password <password>}.
     * Se nessun argomento viene fornito, chiede i dati
     * interattivamente da console; se l'utente lascia tutto vuoto,
     * si ricade sul file {@code db.properties.env}.
     *
     * @param args argomenti passati al lancio di serverTK
     */
    private static void configuraDatabase(String[] args) {

        java.util.Map<String, String> opzioni = new java.util.HashMap<>();

        for (int i = 0; i < args.length - 1; i++) {

            if (args[i].startsWith("--")) {
                opzioni.put(args[i].substring(2), args[i + 1]);
            }
        }

        if (!opzioni.isEmpty()) {

            DatabaseConnection.configure(
                    opzioni.get("host"),
                    opzioni.getOrDefault("port", "5432"),
                    opzioni.get("db"),
                    opzioni.get("user"),
                    opzioni.get("password")
            );

            return;
        }

        // Nessun argomento: chiede i dati interattivamente.
        Scanner scanner = new Scanner(System.in);

        System.out.println(
            "Configurazione del database (premi INVIO per usare "
            + "db.properties.env):"
        );

        System.out.print("Host DB: ");
        String host = scanner.nextLine().trim();

        if (host.isEmpty()) {
            // L'utente ha scelto di usare il file di configurazione.
            return;
        }

        System.out.print("Porta DB [5432]: ");
        String port = scanner.nextLine().trim();

        if (port.isEmpty()) {
            port = "5432";
        }

        System.out.print("Nome DB: ");
        String db = scanner.nextLine().trim();

        System.out.print("Utente DB: ");
        String user = scanner.nextLine().trim();

        System.out.print("Password DB: ");
        String password = scanner.nextLine();

        DatabaseConnection.configure(host, port, db, user, password);
    }
}