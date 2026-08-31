package theknife.client.gui;

import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import theknife.model.Recenzione;
import theknife.model.Ristorante;
import theknife.network.Request;
import theknife.network.RequestType;
import theknife.network.Response;

import java.util.List;

public class RestaurantReviewsView {

    private final Ristorante ristorante;

    private final ListView<Recenzione> recensioniList =
            new ListView<>();

    private final Label messaggio =
            new Label();

    public RestaurantReviewsView(
            Ristorante ristorante) {

        this.ristorante = ristorante;
    }

    public Parent getView() {

        Label titolo =
                new Label(
                        "Recensioni ricevute - "
                                + ristorante.getNome()
                );

        Button indietroButton =
                new Button(
                        "Torna all'area ristoratore"
                );

        indietroButton.setOnAction(
                event ->
                        SceneManager.showRistoratore()
        );

        recensioniList.setCellFactory(
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

                        } else {

                            String testo =
                                    recensione.getStelle()
                                    + "/5 - "
                                    + recensione.getTesto();

                            if (recensione.hasRisposta()) {

                                testo +=
                                        "\nRisposta: "
                                        + recensione
                                        .getRispostaRistoratore();
                            }

                            setText(testo);
                        }
                    }
                }
        );

        TextArea rispostaArea =
                new TextArea();

        rispostaArea.setPromptText(
                "Scrivi la risposta..."
        );

        rispostaArea.setPrefRowCount(3);

        Button rispondiButton =
                new Button(
                        "Rispondi alla recensione"
                );

        rispondiButton.setOnAction(event -> {

            Recenzione selezionata =
                    recensioniList
                            .getSelectionModel()
                            .getSelectedItem();

            if (selezionata == null) {

                messaggio.setText(
                        "Selezionare una recensione."
                );

                return;
            }

            if (selezionata.hasRisposta()) {

                messaggio.setText(
                        "Questa recensione ha già una risposta."
                );

                return;
            }

            String risposta =
                    rispostaArea
                            .getText()
                            .trim();

            if (risposta.isEmpty()) {

                messaggio.setText(
                        "Inserire una risposta."
                );

                return;
            }

            inviaRisposta(
                    selezionata,
                    risposta,
                    rispostaArea,
                    rispondiButton
            );
        });

        HBox pulsanti =
                new HBox(
                        10,
                        indietroButton,
                        rispondiButton
                );

        VBox bottom =
                new VBox(
                        10,
                        rispostaArea,
                        pulsanti,
                        messaggio
                );

        bottom.setPadding(
                new Insets(15)
        );

        VBox top =
                new VBox(
                        10,
                        titolo
                );

        top.setPadding(
                new Insets(15)
        );

        BorderPane root =
                new BorderPane();

        root.setTop(top);
        root.setCenter(recensioniList);
        root.setBottom(bottom);

        BorderPane.setMargin(
                recensioniList,
                new Insets(
                        0,
                        15,
                        0,
                        15
                )
        );

        caricaRecensioni();

        return root;
    }

    private void caricaRecensioni() {

        Request request =
                new Request(
                        RequestType.GET_REVIEWS
                );

        request.addData(
                "idRistorante",
                ristorante.getId()
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

            recensioniList.setItems(
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

    private void inviaRisposta(
            Recenzione recensione,
            String risposta,
            TextArea rispostaArea,
            Button rispondiButton) {

        Request request =
                new Request(
                        RequestType.REPLY_REVIEW
                );

        request.addData(
                "idRecensione",
                recensione.getId()
        );

        request.addData(
                "risposta",
                risposta
        );

        rispondiButton.setDisable(true);

        messaggio.setText(
                "Invio risposta..."
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

            rispondiButton.setDisable(false);

            Response response =
                    task.getValue();

            messaggio.setText(
                    response.getMessage()
            );

            if (response.isSuccess()) {

                rispostaArea.clear();

                caricaRecensioni();
            }
        });

        task.setOnFailed(event -> {

            rispondiButton.setDisable(false);

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