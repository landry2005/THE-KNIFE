package theknife.client.gui;

import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import theknife.client.SessionManager;
import theknife.model.Ristorante;
import theknife.network.Request;
import theknife.network.RequestType;
import theknife.network.Response;

import java.util.List;

/**
 * Interfaccia principale dedicata al ristoratore.
 * Permette di visualizzare i ristoranti appartenenti
 * al ristoratore autenticato.
 *
 * @author Scafidi Michaela - 760101 - VA
 * @author Wafo Tene Wilfried Landry - 763687 - VA
 * @author Fotso Alex Castany - 762919 - VA
 */
public class RistoratoreView {

    private final TableView<Ristorante> table =
            new TableView<>();

    private final Label messaggio =
            new Label();

    public Parent getView() {

        Label titolo =
                new Label("Area ristoratore");

        Label benvenuto =
                new Label();

        if (SessionManager.getUtente() != null) {

            benvenuto.setText(
                    "Benvenuto "
                    + SessionManager
                            .getUtente()
                            .getNome()
            );
        }

        Button aggiornaButton =
                new Button("Aggiorna");

        Button aggiungiButton =
                new Button("Aggiungi ristorante");

        Button logoutButton =
                new Button("Logout");

        logoutButton.setOnAction(event -> {

            SessionManager.logout();

            SceneManager.showLogin();
        });

        aggiornaButton.setOnAction(
                event -> caricaRistoranti()
        );

        /*
         * Per ora il pulsante esiste.
         * Nella prossima classe collegheremo
         * il form per aggiungere un ristorante.
         */
        aggiungiButton.setOnAction(event -> {

            messaggio.setText(
                    "Apertura inserimento ristorante..."
            );

            SceneManager.showAddRestaurant();
        });

        creaTabella();

        /*
         * Doppio click su un ristorante:
         * aprirà successivamente la gestione
         * delle recensioni ricevute.
         */
        table.setRowFactory(tv -> {

            TableRow<Ristorante> row =
                    new TableRow<>();

            row.setOnMouseClicked(event -> {

                if (event.getClickCount() == 2
                        && !row.isEmpty()) {

                    SceneManager.showRestaurantReviews(
                            row.getItem()
                    );
                }
            });

            return row;
        });

        HBox pulsanti =
                new HBox(
                        10,
                        aggiungiButton,
                        aggiornaButton,
                        logoutButton
                );

        VBox top =
                new VBox(
                        15,
                        titolo,
                        benvenuto,
                        pulsanti,
                        messaggio
                );

        top.setPadding(
                new Insets(20)
        );

        BorderPane root =
                new BorderPane();

        root.setTop(top);
        root.setCenter(table);

        BorderPane.setMargin(
                table,
                new Insets(
                        0,
                        20,
                        20,
                        20
                )
        );

        caricaRistoranti();

        return root;
    }

    private void caricaRistoranti() {

        if (!SessionManager.isRistoratore()) {

            messaggio.setText(
                    "Accesso riservato ai ristoratori."
            );

            return;
        }

        Request request =
                new Request(
                        RequestType.GET_MY_RESTAURANTS
                );

        request.addData(
                "idRistoratore",
                SessionManager
                        .getUtente()
                        .getId()
        );

        messaggio.setText(
                "Caricamento ristoranti..."
        );

        Task<Response> task =
                new Task<>() {

                    @Override
                    protected Response call()
                            throws Exception {

                        return SceneManager
                                .getConnection()
                                .sendRequest(request);
                    }
                };

        task.setOnSucceeded(event -> {

            Response response =
                    task.getValue();

            if (!response.isSuccess()) {

                messaggio.setText(
                        response.getMessage()
                );

                return;
            }

            @SuppressWarnings("unchecked")
            List<Ristorante> ristoranti =
                    (List<Ristorante>)
                            response.getData();

            table.setItems(
                    FXCollections
                            .observableArrayList(
                                    ristoranti
                            )
            );

            messaggio.setText(
                    "Ristoranti trovati: "
                    + ristoranti.size()
            );
        });

        task.setOnFailed(event -> {

            messaggio.setText(
                    "Errore di comunicazione con il server."
            );

            if (task.getException() != null) {
                task.getException()
                        .printStackTrace();
            }
        });

        Thread thread =
                new Thread(task);

        thread.setDaemon(true);
        thread.start();
    }

    private void creaTabella() {

        TableColumn<Ristorante, String> nome =
                new TableColumn<>("Nome");

        nome.setCellValueFactory(
                data ->
                        new javafx.beans.property
                                .SimpleStringProperty(
                                data.getValue()
                                        .getNome()
                        )
        );

        TableColumn<Ristorante, String> citta =
                new TableColumn<>("Città");

        citta.setCellValueFactory(
                data ->
                        new javafx.beans.property
                                .SimpleStringProperty(
                                data.getValue()
                                        .getCitta()
                        )
        );

        TableColumn<Ristorante, String> cucina =
                new TableColumn<>("Cucina");

        cucina.setCellValueFactory(
                data ->
                        new javafx.beans.property
                                .SimpleStringProperty(
                                data.getValue()
                                        .getTipoCucina()
                        )
        );

        TableColumn<Ristorante, Number> prezzo =
                new TableColumn<>("Prezzo medio");

        prezzo.setCellValueFactory(
                data ->
                        new javafx.beans.property
                                .SimpleDoubleProperty(
                                data.getValue()
                                        .getPrezzoMedio()
                        )
        );

        TableColumn<Ristorante, Number> recensioni =
                new TableColumn<>("Recensioni");

        recensioni.setCellValueFactory(
                data ->
                        new javafx.beans.property
                                .SimpleIntegerProperty(
                                data.getValue()
                                        .getNumeroRecensioni()
                        )
        );

        table.getColumns().addAll(
                nome,
                citta,
                cucina,
                prezzo,
                recensioni
        );

        table.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );
    }
}