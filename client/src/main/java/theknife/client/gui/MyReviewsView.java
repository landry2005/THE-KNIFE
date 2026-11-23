package theknife.client.gui;

import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import theknife.client.SessionManager;
import theknife.model.Recenzione;
import theknife.network.Request;
import theknife.network.RequestType;
import theknife.network.Response;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Schermata che mostra tutte le recensioni lasciate
 * dall'utente cliente attualmente autenticato.
 *
 * @author Scafidi Michaela - 760101 - VA
 * @author Wafo Tene Wilfried Landry - 763687 - VA
 * @author Fotso Alex Castany - 762919 - VA
 */
public class MyReviewsView {

    private final ListView<Recenzione> listaRecensioni =
            new ListView<>();

    private final Label messaggio =
            new Label();

    public Parent getView() {

        Label titolo =
                new Label("Le mie recensioni");

        Button indietroButton =
                new Button("Torna alla home");

        Button logoutButton =
                new Button("Logout");

        indietroButton.setOnAction(
                event -> SceneManager.showHome(
                        SessionManager.getUtente() != null
                                ? SessionManager.getUtente().getCittaDomicilio()
                                : null
                )
        );

        logoutButton.setOnAction(event -> {
            SessionManager.logout();
            SceneManager.showLogin();
        });

        configuraLista();

        HBox pulsanti =
                new HBox(
                        10,
                        indietroButton,
                        logoutButton
                );

        VBox top =
                new VBox(
                        15,
                        pulsanti,
                        titolo,
                        messaggio
                );

        top.setPadding(new Insets(20));

        BorderPane root =
                new BorderPane();

        root.setTop(top);
        root.setCenter(listaRecensioni);

        BorderPane.setMargin(
                listaRecensioni,
                new Insets(0, 20, 20, 20)
        );

        caricaMieRecensioni();

        return root;
    }

    private void configuraLista() {

        listaRecensioni.setCellFactory(
                list -> new ListCell<>() {

                    @Override
                    protected void updateItem(
                            Recenzione recensione,
                            boolean empty) {

                        super.updateItem(
                                recensione,
                                empty
                        );

                        if (empty
                                || recensione == null) {

                            setText(null);
                            setGraphic(null);
                            return;
                        }

                        DateTimeFormatter formatter =
                                DateTimeFormatter.ofPattern(
                                        "dd/MM/yyyy HH:mm"
                                );

                        String simboliStelle =
                                "★".repeat(
                                        recensione.getStelle()
                                );

                        Label stelleLabel =
                                new Label(simboliStelle);

                        Label dataLabel =
                                new Label();

                        if (recensione.getDataOra()
                                        != null) {

                            dataLabel.setText(
                                    recensione
                                            .getDataOra()
                                            .format(formatter)
                            );
                        }

                        Label testoLabel =
                                new Label(
                                        recensione.getTesto()
                                );

                        testoLabel.setWrapText(true);

                        VBox contenuto =
                                new VBox(
                                        5,
                                        stelleLabel,
                                        dataLabel,
                                        testoLabel
                                );

                        if (recensione.hasRisposta()) {

                            Label rispostaTitolo =
                                    new Label(
                                            "Risposta del ristoratore:"
                                    );

                            Label risposta =
                                    new Label(
                                            recensione
                                                    .getRispostaRistoratore()
                                    );

                            risposta.setWrapText(true);

                            contenuto.getChildren().addAll(
                                    rispostaTitolo,
                                    risposta
                            );
                        }

                        setText(null);
                        setGraphic(contenuto);
                    }
                }
        );
    }

    private void caricaMieRecensioni() {

        if (!SessionManager.isLoggato()) {

            messaggio.setText(
                    "Effettuare il login per visualizzare le proprie recensioni."
            );

            return;
        }

        Request request =
                new Request(
                        RequestType.GET_MY_REVIEWS
                );

        request.addData(
                "idUtente",
                SessionManager
                        .getUtente()
                        .getId()
        );

        messaggio.setText(
                "Caricamento recensioni..."
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
            List<Recenzione> recensioni =
                    (List<Recenzione>)
                            response.getData();

            listaRecensioni.setItems(
                    FXCollections
                            .observableArrayList(
                                    recensioni
                            )
            );

            messaggio.setText(
                    "Recensioni trovate: "
                            + recensioni.size()
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
}