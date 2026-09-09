package theknife.client.gui;

import javafx.application.Application;
import javafx.stage.Stage;
import theknife.client.ServerConnection;

/**
 * Punto di ingresso dell'applicazione client JavaFX. Inizializza la
 * connessione al server e avvia l'interfaccia grafica mostrando la
 * schermata di login iniziale.
 *
 * @author Scafidi Michaela - 760101 - VA
 * @author Wafo Tene Wilfried Landry - 763687 - VA
 * @author Fotso Alex Castany - 762919 - VA
 */
public class TheKnifeApp extends Application {

    @Override
    public void start(Stage stage) {
        ServerConnection connection =
                new ServerConnection("localhost", 5000);

        SceneManager.initialize(stage, connection);

        stage.setTitle("TheKnife");
        SceneManager.showLogin();
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}