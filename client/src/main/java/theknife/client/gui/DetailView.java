package theknife.client.gui;

import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import theknife.client.SessionManager;
import theknife.model.Recenzione;
import theknife.model.Ristorante;
import theknife.network.Request;
import theknife.network.RequestType;
import theknife.network.Response;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class DetailView {

    private final Ristorante ristorante;
    private final ListView<Recenzione> listaRecensioni =
            new ListView<>();

    private final Label messaggio =
            new Label();

    public DetailView(Ristorante ristorante) {
        this.ristorante = ristorante;
    }

    public Parent getView() {

        Label titolo =
                new Label(ristorante.getNome());

        Label posizione =
                new Label(
                        "Località: "
                                + ristorante.getLocazione()
                );

        Label cucina =
                new Label(
                        "Cucina: "
                                + ristorante.getTipoCucina()
                );

        Label prezzo =
                new Label(
                        String.format(
                                "Prezzo medio: %.2f €",
                                ristorante.getPrezzoMedio()
                        )
                );

        Label stelle =
                new Label(
                        String.format(
                                "Valutazione media: %.1f / 5",
                                ristorante.getMediaStelle()
                        )
                );

        Label delivery =
                new Label(
                        "Delivery: "
                                + (ristorante.isDelivery()
                                ? "Sì"
                                : "No")
                );

        Label prenotazione =
                new Label(
                        "Prenotazione online: "
                                + (ristorante.isPrenotazione()
                                ? "Sì"
                                : "No")
                );

        Button indietroButton =
                new Button("Torna alla ricerca");

        Button aggiungiPreferitoButton =
                new Button("Aggiungi ai preferiti");

        Button rimuoviPreferitoButton =
                new Button("Rimuovi dai preferiti");

        ComboBox<Integer> stelleBox =
                new ComboBox<>();

        stelleBox.getItems().addAll(
                1, 2, 3, 4, 5
        );

        stelleBox.setPromptText("Stelle");

        TextArea testoRecensione =
                new TextArea();

        testoRecensione.setPromptText(
                "Scrivi la tua recensione"
        );

        testoRecensione.setPrefRowCount(4);

        Button aggiungiRecensioneButton =
                new Button("Pubblica recensione");

        configuraListaRecensioni();

        indietroButton.setOnAction(
                event -> SceneManager.showSearch()
        );

        aggiungiPreferitoButton.setOnAction(
                event -> aggiungiPreferito()
        );

        rimuoviPreferitoButton.setOnAction(
                event -> rimuoviPreferito()
        );

        aggiungiRecensioneButton.setOnAction(event -> {

            if (!SessionManager.isLoggato()) {

                messaggio.setText(
                        "Effettuare il login per lasciare una recensione."
                );

                return;
            }

            Integer valoreStelle =
                    stelleBox.getValue();

            String testo =
                    testoRecensione
                            .getText()
                            .trim();

            if (valoreStelle == null) {

                messaggio.setText(
                        "Selezionare il numero di stelle."
                );

                return;
            }

            if (testo.isEmpty()) {

                messaggio.setText(
                        "Inserire il testo della recensione."
                );

                return;
            }

            aggiungiRecensione(
                    valoreStelle,
                    testo,
                    stelleBox,
                    testoRecensione
            );
        });

        boolean cliente =
                SessionManager.isCliente();

        aggiungiPreferitoButton.setVisible(cliente);
        aggiungiPreferitoButton.setManaged(cliente);

        rimuoviPreferitoButton.setVisible(cliente);
        rimuoviPreferitoButton.setManaged(cliente);

        stelleBox.setVisible(cliente);
        stelleBox.setManaged(cliente);

        testoRecensione.setVisible(cliente);
        testoRecensione.setManaged(cliente);

        aggiungiRecensioneButton.setVisible(cliente);
        aggiungiRecensioneButton.setManaged(cliente);

        HBox preferitiBox =
                new HBox(
                        10,
                        aggiungiPreferitoButton,
                        rimuoviPreferitoButton
                );

        preferitiBox.setAlignment(
                Pos.CENTER_LEFT
        );

        HBox recensioneForm =
                new HBox(
                        10,
                        stelleBox,
                        aggiungiRecensioneButton
                );

        recensioneForm.setAlignment(
                Pos.CENTER_LEFT
        );

        VBox root =
                new VBox(
                        12,
                        indietroButton,
                        titolo,
                        posizione,
                        cucina,
                        prezzo,
                        stelle,
                        delivery,
                        prenotazione,
                        preferitiBox,
                        new Label("Recensioni"),
                        listaRecensioni,
                        new Label("Lascia una recensione"),
                        testoRecensione,
                        recensioneForm,
                        messaggio
                );

        root.setPadding(
                new Insets(25)
        );

        listaRecensioni.setPrefHeight(300);

        caricaRecensioni();

        return root;
    }

    private void configuraListaRecensioni() {

        listaRecensioni.setCellFactory(
                lista -> new ListCell<>() {

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

                        boolean propriaRecensione =
                                SessionManager.isLoggato()
                                && SessionManager.isCliente()
                                && SessionManager
                                        .getUtente()
                                        .getId()
                                == recensione.getIdUtente();

                        if (propriaRecensione) {

                            Button modificaButton =
                                    new Button("Modifica");

                            Button eliminaButton =
                                    new Button("Elimina");

                            modificaButton.setOnAction(
                                    event ->
                                            mostraDialogModifica(
                                                    recensione
                                            )
                            );

                            eliminaButton.setOnAction(
                                    event ->
                                            confermaEliminazione(
                                                    recensione
                                            )
                            );

                            HBox azioni =
                                    new HBox(
                                            10,
                                            modificaButton,
                                            eliminaButton
                                    );

                            contenuto
                                    .getChildren()
                                    .add(azioni);
                        }

                        setText(null);
                        setGraphic(contenuto);
                    }
                }
        );
    }

    private void mostraDialogModifica(
            Recenzione recensione) {

        Dialog<ButtonType> dialog =
                new Dialog<>();

        dialog.setTitle(
                "Modifica recensione"
        );

        dialog.setHeaderText(
                "Modifica la tua recensione"
        );

        ComboBox<Integer> stelleBox =
                new ComboBox<>();

        stelleBox.getItems().addAll(
                1, 2, 3, 4, 5
        );

        stelleBox.setValue(
                recensione.getStelle()
        );

        TextArea testoArea =
                new TextArea(
                        recensione.getTesto()
                );

        testoArea.setPrefRowCount(5);

        VBox contenuto =
                new VBox(
                        10,
                        new Label("Stelle"),
                        stelleBox,
                        new Label("Testo"),
                        testoArea
                );

        contenuto.setPadding(
                new Insets(10)
        );

        dialog.getDialogPane()
                .setContent(contenuto);

        dialog.getDialogPane()
                .getButtonTypes()
                .addAll(
                        ButtonType.OK,
                        ButtonType.CANCEL
                );

        ButtonType risultato =
                dialog.showAndWait()
                        .orElse(
                                ButtonType.CANCEL
                        );

        if (risultato != ButtonType.OK) {
            return;
        }

        Integer nuoveStelle =
                stelleBox.getValue();

        String nuovoTesto =
                testoArea
                        .getText()
                        .trim();

        if (nuoveStelle == null) {

            messaggio.setText(
                    "Selezionare il numero di stelle."
            );

            return;
        }

        if (nuovoTesto.isEmpty()) {

            messaggio.setText(
                    "Il testo della recensione non può essere vuoto."
            );

            return;
        }

        modificaRecensione(
                recensione,
                nuoveStelle,
                nuovoTesto
        );
    }

    private void modificaRecensione(
            Recenzione recensione,
            int nuoveStelle,
            String nuovoTesto) {

        Request request =
                new Request(
                        RequestType.EDIT_REVIEW
                );

        request.addData(
                "idUtente",
                SessionManager
                        .getUtente()
                        .getId()
        );

        request.addData(
                "idRistorante",
                recensione.getIdRistorante()
        );

        request.addData(
                "stelle",
                nuoveStelle
        );

        request.addData(
                "testo",
                nuovoTesto
        );

        Task<Response> task =
                creaTask(request);

        task.setOnSucceeded(event -> {

            Response response =
                    task.getValue();

            messaggio.setText(
                    response.getMessage()
            );

            if (response.isSuccess()) {
                caricaRecensioni();
            }
        });

        gestisciErroreTask(task);
        avviaTask(task);
    }

    private void confermaEliminazione(
            Recenzione recensione) {

        Alert conferma =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );

        conferma.setTitle(
                "Elimina recensione"
        );

        conferma.setHeaderText(
                "Vuoi eliminare la recensione?"
        );

        conferma.setContentText(
                "L'operazione non può essere annullata."
        );

        ButtonType risultato =
                conferma.showAndWait()
                        .orElse(
                                ButtonType.CANCEL
                        );

        if (risultato == ButtonType.OK) {
            eliminaRecensione(recensione);
        }
    }

    private void eliminaRecensione(
            Recenzione recensione) {

        Request request =
                new Request(
                        RequestType.DELETE_REVIEW
                );

        request.addData(
                "idUtente",
                SessionManager
                        .getUtente()
                        .getId()
        );

        request.addData(
                "idRistorante",
                recensione.getIdRistorante()
        );

        Task<Response> task =
                creaTask(request);

        task.setOnSucceeded(event -> {

            Response response =
                    task.getValue();

            messaggio.setText(
                    response.getMessage()
            );

            if (response.isSuccess()) {
                caricaRecensioni();
            }
        });

        gestisciErroreTask(task);
        avviaTask(task);
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

        Task<Response> task =
                creaTask(request);

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
                    "Recensioni caricate: "
                            + recensioni.size()
            );
        });

        gestisciErroreTask(task);
        avviaTask(task);
    }

    private void aggiungiPreferito() {

        if (!SessionManager.isLoggato()) {
            return;
        }

        Request request =
                new Request(
                        RequestType.ADD_FAVORITE
                );

        request.addData(
                "idUtente",
                SessionManager
                        .getUtente()
                        .getId()
        );

        request.addData(
                "idRistorante",
                ristorante.getId()
        );

        inviaOperazioneSemplice(request);
    }

    private void rimuoviPreferito() {

        if (!SessionManager.isLoggato()) {
            return;
        }

        Request request =
                new Request(
                        RequestType.REMOVE_FAVORITE
                );

        request.addData(
                "idUtente",
                SessionManager
                        .getUtente()
                        .getId()
        );

        request.addData(
                "idRistorante",
                ristorante.getId()
        );

        inviaOperazioneSemplice(request);
    }

    private void aggiungiRecensione(
            int stelle,
            String testo,
            ComboBox<Integer> stelleBox,
            TextArea testoRecensione) {

        Request request =
                new Request(
                        RequestType.ADD_REVIEW
                );

        request.addData(
                "idUtente",
                SessionManager
                        .getUtente()
                        .getId()
        );

        request.addData(
                "idRistorante",
                ristorante.getId()
        );

        request.addData(
                "stelle",
                stelle
        );

        request.addData(
                "testo",
                testo
        );

        Task<Response> task =
                creaTask(request);

        task.setOnSucceeded(event -> {

            Response response =
                    task.getValue();

            messaggio.setText(
                    response.getMessage()
            );

            if (response.isSuccess()) {

                stelleBox.setValue(null);
                testoRecensione.clear();

                caricaRecensioni();
            }
        });

        gestisciErroreTask(task);
        avviaTask(task);
    }

    private void inviaOperazioneSemplice(
            Request request) {

        Task<Response> task =
                creaTask(request);

        task.setOnSucceeded(event -> {

            Response response =
                    task.getValue();

            messaggio.setText(
                    response.getMessage()
            );
        });

        gestisciErroreTask(task);
        avviaTask(task);
    }

    private Task<Response> creaTask(
            Request request) {

        return new Task<>() {

            @Override
            protected Response call()
                    throws Exception {

                return SceneManager
                        .getConnection()
                        .sendRequest(request);
            }
        };
    }

    private void gestisciErroreTask(
            Task<Response> task) {

        task.setOnFailed(event -> {

            messaggio.setText(
                    "Errore di comunicazione con il server."
            );

            if (task.getException() != null) {

                task.getException()
                        .printStackTrace();
            }
        });
    }

    private void avviaTask(
            Task<Response> task) {

        Thread thread =
                new Thread(task);

        thread.setDaemon(true);
        thread.start();
    }
}