package theknife.client.gui;

import javafx.scene.Scene;
import javafx.stage.Stage;

import theknife.client.ServerConnection;
import theknife.model.Ristorante;

/**
 * Gestore centralizzato della navigazione tra le schermate (scene)
 * dell'applicazione client. Mantiene un riferimento unico allo
 * {@link javafx.stage.Stage} principale e alla connessione al
 * server, ed espone un metodo statico per ciascuna schermata
 * dell'applicazione.
 *
 * @author Scafidi Michaela - 760101 - VA
 * @author Wafo Tene Wilfried Landry - 763687 - VA
 * @author Fotso Alex Castany - 762919 - VA
 */
public final class SceneManager {

    private static Stage stage;
    private static ServerConnection connection;

    private SceneManager() {
    }

    public static void initialize(
            Stage primaryStage,
            ServerConnection serverConnection) {

        stage = primaryStage;
        connection = serverConnection;
    }

    public static ServerConnection getConnection() {
        return connection;
    }

    public static void showLogin() {

        stage.setScene(
                new Scene(
                        new LoginView().getView(),
                        500,
                        450
                )
        );
    }

    public static void showRegister() {

        stage.setScene(
                new Scene(
                        new RegisterView().getView(),
                        600,
                        700
                )
        );
    }

    public static void showSearch() {

        stage.setScene(
                new Scene(
                        new SearchView().getView(),
                        1000,
                        650
                )
        );
    }

    /**
     * Mostra la schermata iniziale (Home) con l'elenco dei
     * ristoranti vicini al luogo indicato oppure, se l'utente è
     * loggato, al proprio domicilio.
     *
     * @param luogoIniziale luogo da usare per la prima ricerca
     *                      automatica (può essere {@code null})
     */
    public static void showHome(String luogoIniziale) {

        stage.setScene(
                new Scene(
                        new HomeView().getView(luogoIniziale),
                        1000,
                        650
                )
        );
    }

    public static void showDetail(
            Ristorante ristorante) {

        stage.setScene(
                new Scene(
                        new DetailView(
                                ristorante
                        ).getView(),
                        850,
                        750
                )
        );
    }

    public static void showFavorites() {

        stage.setScene(
                new Scene(
                        new FavoritesView().getView(),
                        900,
                        650
                )
        );
    }

    public static void showRistoratore() {

        stage.setScene(
                new Scene(
                        new RistoratoreView().getView(),
                        1000,
                        650
                )
        );
    }

    public static void showAddRestaurant() {

        stage.setScene(
                new Scene(
                        new AddRestaurantView().getView(),
                        650,
                        750
                )
        );
    }

    public static void showRestaurantReviews(
            Ristorante ristorante) {

        stage.setScene(
                new Scene(
                        new RestaurantReviewsView(
                                ristorante
                        ).getView(),
                        850,
                        700
                )
        );
    }
}