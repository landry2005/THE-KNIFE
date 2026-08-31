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

public class FavoritesView {

    private final TableView<Ristorante> table =
            new TableView<>();

    private final Label messaggio =
            new Label();

    public Parent getView() {

        Label titolo =
                new Label("I miei preferiti");

        Button indietroButton =
                new Button("Torna alla ricerca");

        Button logoutButton =
                new Button("Logout");

        indietroButton.setOnAction(
                event ->
                        SceneManager.showSearch()
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

        caricaPreferiti();

        return root;
    }

    private void caricaPreferiti() {

        if (!SessionManager.isLoggato()) {

            messaggio.setText(
                    "Effettuare il login per visualizzare i preferiti."
            );

            return;
        }

        Request request =
                new Request(
                        RequestType.GET_FAVORITES
                );

        request.addData(
                "idUtente",
                SessionManager
                        .getUtente()
                        .getId()
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
            List<Ristorante> preferiti =
                    (List<Ristorante>)
                            response.getData();

            table.setItems(
                    FXCollections
                            .observableArrayList(
                                    preferiti
                            )
            );

            messaggio.setText(
                    "Preferiti trovati: "
                            + preferiti.size()
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

        table.getColumns().addAll(
                nome,
                citta,
                cucina,
                prezzo
        );

        table.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );
    }
}