package theknife.client.gui;

import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import theknife.client.SessionManager;
import theknife.model.Ristorante;
import theknife.network.Request;
import theknife.network.RequestType;
import theknife.network.Response;
import theknife.network.SearchCriteria;

import java.util.List;

public class SearchView {

    private final TableView<Ristorante> table =
            new TableView<>();

    public Parent getView() {

        Label titolo =
                new Label("Ricerca ristoranti");

        TextField cittaField =
                new TextField();

        cittaField.setPromptText("Città");

        TextField cucinaField =
                new TextField();

        cucinaField.setPromptText(
                "Tipo di cucina"
        );

        TextField prezzoMinField =
                new TextField();

        prezzoMinField.setPromptText(
                "Prezzo minimo"
        );

        TextField prezzoMaxField =
                new TextField();

        prezzoMaxField.setPromptText(
                "Prezzo massimo"
        );

        ComboBox<Double> stelleBox =
                new ComboBox<>();

        stelleBox.getItems().addAll(
                1.0,
                2.0,
                3.0,
                4.0,
                5.0
        );

        stelleBox.setPromptText(
                "Stelle minime"
        );

        CheckBox deliveryBox =
                new CheckBox("Delivery");

        CheckBox prenotazioneBox =
                new CheckBox(
                        "Prenotazione online"
                );

        Button cercaButton =
                new Button("Cerca");

        Button preferitiButton =
                new Button(
                        "I miei preferiti"
                );

        Button logoutButton =
                new Button("Logout");

        Label messaggio =
                new Label();

        boolean cliente =
                SessionManager.isCliente();

        preferitiButton.setVisible(cliente);
        preferitiButton.setManaged(cliente);

        preferitiButton.setOnAction(
                event ->
                        SceneManager.showFavorites()
        );

        logoutButton.setOnAction(event -> {

            SessionManager.logout();

            SceneManager.showLogin();
        });

        creaTabella();

        table.setRowFactory(tv -> {

            TableRow<Ristorante> row =
                    new TableRow<>();

            row.setOnMouseClicked(event -> {

                if (event.getClickCount() == 2
                        && !row.isEmpty()) {

                    SceneManager.showDetail(
                            row.getItem()
                    );
                }
            });

            return row;
        });

        cercaButton.setOnAction(event -> {

            String citta =
                    cittaField
                            .getText()
                            .trim();

            if (citta.isEmpty()) {

                messaggio.setText(
                        "Inserire una città."
                );

                return;
            }

            Double prezzoMin;
            Double prezzoMax;

            try {

                prezzoMin =
                        prezzoMinField
                                .getText()
                                .isBlank()
                                ? null
                                : Double.parseDouble(
                                        prezzoMinField
                                                .getText()
                                );

                prezzoMax =
                        prezzoMaxField
                                .getText()
                                .isBlank()
                                ? null
                                : Double.parseDouble(
                                        prezzoMaxField
                                                .getText()
                                );

            } catch (NumberFormatException e) {

                messaggio.setText(
                        "Inserire valori numerici validi per il prezzo."
                );

                return;
            }

            SearchCriteria criteri =
                    new SearchCriteria();

            criteri.setCitta(citta);

            criteri.setTipoCucina(
                    cucinaField
                            .getText()
                            .trim()
            );

            criteri.setPrezzoMin(
                    prezzoMin
            );

            criteri.setPrezzoMax(
                    prezzoMax
            );

            criteri.setDelivery(
                    deliveryBox.isSelected()
            );

            criteri.setPrenotazione(
                    prenotazioneBox.isSelected()
            );

            criteri.setStelleMin(
                    stelleBox.getValue()
            );

            Request request =
                    new Request(
                            RequestType.SEARCH
                    );

            request.addData(
                    "criteria",
                    criteri
            );

            cercaButton.setDisable(true);

            messaggio.setText(
                    "Ricerca in corso..."
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

            task.setOnSucceeded(done -> {

                cercaButton.setDisable(false);

                Response response =
                        task.getValue();

                if (!response.isSuccess()) {

                    messaggio.setText(
                            response.getMessage()
                    );

                    return;
                }

                @SuppressWarnings("unchecked")
                List<Ristorante> risultati =
                        (List<Ristorante>)
                                response.getData();

                table.setItems(
                        FXCollections
                                .observableArrayList(
                                        risultati
                                )
                );

                messaggio.setText(
                        "Ristoranti trovati: "
                                + risultati.size()
                );
            });

            task.setOnFailed(done -> {

                cercaButton.setDisable(false);

                messaggio.setText(
                        "Errore di connessione al server."
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
        });

        HBox filtri1 =
                new HBox(
                        10,
                        cittaField,
                        cucinaField,
                        prezzoMinField,
                        prezzoMaxField
                );

        HBox filtri2 =
                new HBox(
                        15,
                        stelleBox,
                        deliveryBox,
                        prenotazioneBox,
                        cercaButton,
                        preferitiButton,
                        logoutButton
                );

        VBox top =
                new VBox(
                        15,
                        titolo,
                        filtri1,
                        filtri2,
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

        return root;
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
                new TableColumn<>(
                        "Prezzo medio"
                );

        prezzo.setCellValueFactory(
                data ->
                        new javafx.beans.property
                                .SimpleDoubleProperty(
                                data.getValue()
                                        .getPrezzoMedio()
                        )
        );

        TableColumn<Ristorante, Number> stelle =
                new TableColumn<>("Stelle");

        stelle.setCellValueFactory(
                data ->
                        new javafx.beans.property
                                .SimpleDoubleProperty(
                                data.getValue()
                                        .getMediaStelle()
                        )
        );

        TableColumn<Ristorante, String> delivery =
                new TableColumn<>("Delivery");

        delivery.setCellValueFactory(
                data ->
                        new javafx.beans.property
                                .SimpleStringProperty(
                                data.getValue()
                                        .isDelivery()
                                        ? "Sì"
                                        : "No"
                        )
        );

        table.getColumns().addAll(
                nome,
                citta,
                cucina,
                prezzo,
                stelle,
                delivery
        );

        table.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );
    }
}