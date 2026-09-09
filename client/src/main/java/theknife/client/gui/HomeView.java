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

/**
 * Schermata iniziale mostrata subito dopo l'accesso (come cliente
 * registrato) o dopo la scelta "Continua come ospite". Mostra un
 * elenco dei ristoranti vicini al luogo indicato dall'utente guest,
 * oppure al domicilio dell'utente cliente registrato, e permette di
 * accedere alla ricerca avanzata dei ristoranti.
 *
 * @author Scafidi Michaela - 760101 - VA
 * @author Wafo Tene Wilfried Landry - 763687 - VA
 * @author Fotso Alex Castany - 762919 - VA
 */
public class HomeView {

    private final TableView<Ristorante> table = new TableView<>();

    /**
     * Costruisce la schermata iniziale.
     *
     * @param luogoIniziale luogo da usare per la prima ricerca
     *                      automatica (nome della città del guest,
     *                      oppure domicilio dell'utente loggato);
     *                      può essere {@code null} o vuoto se non
     *                      disponibile.
     * @return la radice del grafo di scena da mostrare
     */
    public Parent getView(String luogoIniziale) {

        boolean loggato = SessionManager.isLoggato();

        Label titolo = new Label(
                loggato
                        ? "Ristoranti vicino a te"
                        : "Benvenuto su TheKnife"
        );

        Label sottotitolo = new Label(
                loggato
                        ? "In base al tuo domicilio"
                        : "Inserisci un luogo per vedere i ristoranti nelle vicinanze"
        );

        TextField luogoField = new TextField();
        luogoField.setPromptText("Città o luogo");

        if (luogoIniziale != null) {
            luogoField.setText(luogoIniziale);
        }

        // Un utente loggato consulta i ristoranti vicino al proprio
        // domicilio: il campo non serve, la ricerca parte da sola.
        luogoField.setVisible(!loggato);
        luogoField.setManaged(!loggato);

        Button cercaButton = new Button("Cerca ristoranti vicini");
        cercaButton.setVisible(!loggato);
        cercaButton.setManaged(!loggato);

        Button ricercaAvanzataButton =
                new Button("Ricerca avanzata");

        ricercaAvanzataButton.setOnAction(
                event -> SceneManager.showSearch()
        );

        Button preferitiButton = new Button("I miei preferiti");
        preferitiButton.setVisible(SessionManager.isCliente());
        preferitiButton.setManaged(SessionManager.isCliente());
        preferitiButton.setOnAction(
                event -> SceneManager.showFavorites()
        );

        Button accountButton = new Button(
                loggato ? "Logout" : "Accedi / Registrati"
        );

        accountButton.setOnAction(event -> {

            if (loggato) {
                SessionManager.logout();
            }

            SceneManager.showLogin();
        });

        Label messaggio = new Label();

        creaTabella();

        table.setRowFactory(tv -> {

            TableRow<Ristorante> row = new TableRow<>();

            row.setOnMouseClicked(event -> {

                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    SceneManager.showDetail(row.getItem());
                }
            });

            return row;
        });

        cercaButton.setOnAction(
                event -> eseguiRicerca(
                        luogoField.getText().trim(),
                        messaggio
                )
        );

        HBox comandi = new HBox(
                15,
                luogoField,
                cercaButton,
                ricercaAvanzataButton,
                preferitiButton,
                accountButton
        );

        VBox top = new VBox(
                10,
                titolo,
                sottotitolo,
                comandi,
                messaggio
        );

        top.setPadding(new Insets(20));

        BorderPane root = new BorderPane();

        root.setTop(top);
        root.setCenter(table);

        BorderPane.setMargin(
                table,
                new Insets(0, 20, 20, 20)
        );

        // Ricerca automatica: subito se l'utente è loggato (usa il
        // proprio domicilio), oppure se è stato fornito un luogo
        // iniziale per l'utente guest.
        String luogoAutomatico =
                loggato
                        ? (SessionManager.getUtente() != null
                                ? SessionManager.getUtente().getCittaDomicilio()
                                : null)
                        : luogoIniziale;

        if (luogoAutomatico != null && !luogoAutomatico.isBlank()) {
            eseguiRicerca(luogoAutomatico, messaggio);
        }

        return root;
    }

    private void eseguiRicerca(String luogo, Label messaggio) {

        if (luogo == null || luogo.isBlank()) {

            messaggio.setText(
                    "Inserire un luogo per cercare i ristoranti vicini."
            );

            return;
        }

        SearchCriteria criteri = new SearchCriteria();
        criteri.setCitta(luogo);

        Request request = new Request(RequestType.SEARCH);
        request.addData("criteria", criteri);

        messaggio.setText("Ricerca in corso...");

        Task<Response> task = new Task<>() {

            @Override
            protected Response call() throws Exception {

                return SceneManager
                        .getConnection()
                        .sendRequest(request);
            }
        };

        task.setOnSucceeded(done -> {

            Response response = task.getValue();

            if (!response.isSuccess()) {
                messaggio.setText(response.getMessage());
                return;
            }

            @SuppressWarnings("unchecked")
            List<Ristorante> risultati =
                    (List<Ristorante>) response.getData();

            table.setItems(
                    FXCollections.observableArrayList(risultati)
            );

            messaggio.setText(
                    "Ristoranti trovati: " + risultati.size()
            );
        });

        task.setOnFailed(done -> {

            messaggio.setText("Errore di connessione al server.");

            if (task.getException() != null) {
                task.getException().printStackTrace();
            }
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void creaTabella() {

        TableColumn<Ristorante, String> nome =
                new TableColumn<>("Nome");

        nome.setCellValueFactory(
                data -> new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getNome()
                )
        );

        TableColumn<Ristorante, String> citta =
                new TableColumn<>("Città");

        citta.setCellValueFactory(
                data -> new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getCitta()
                )
        );

        TableColumn<Ristorante, String> cucina =
                new TableColumn<>("Cucina");

        cucina.setCellValueFactory(
                data -> new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getTipoCucina()
                )
        );

        TableColumn<Ristorante, Number> prezzo =
                new TableColumn<>("Prezzo medio");

        prezzo.setCellValueFactory(
                data -> new javafx.beans.property.SimpleDoubleProperty(
                        data.getValue().getPrezzoMedio()
                )
        );

        TableColumn<Ristorante, Number> stelle =
                new TableColumn<>("Stelle");

        stelle.setCellValueFactory(
                data -> new javafx.beans.property.SimpleDoubleProperty(
                        data.getValue().getMediaStelle()
                )
        );

        table.getColumns().addAll(
                nome, citta, cucina, prezzo, stelle
        );

        table.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );
    }
}
