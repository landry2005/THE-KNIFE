package theknife.client.gui;

import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import theknife.client.SessionManager;
import theknife.model.Ristorante;
import theknife.network.Request;
import theknife.network.RequestType;
import theknife.network.Response;

public class AddRestaurantView {

    private final Label messaggio = new Label();

    public Parent getView() {

        Label titolo =
                new Label("Aggiungi ristorante");

        TextField nomeField =
                new TextField();

        nomeField.setPromptText("Nome");

        TextField nazioneField =
                new TextField();

        nazioneField.setPromptText("Nazione");

        TextField cittaField =
                new TextField();

        cittaField.setPromptText("Città");

        TextField indirizzoField =
                new TextField();

        indirizzoField.setPromptText("Indirizzo");

        TextField latitudineField =
                new TextField();

        latitudineField.setPromptText("Latitudine");

        TextField longitudineField =
                new TextField();

        longitudineField.setPromptText("Longitudine");

        TextField cucinaField =
                new TextField();

        cucinaField.setPromptText("Tipo di cucina");

        TextField prezzoField =
                new TextField();

        prezzoField.setPromptText("Prezzo medio");

        CheckBox deliveryBox =
                new CheckBox("Delivery");

        CheckBox prenotazioneBox =
                new CheckBox("Prenotazione online");

        Button salvaButton =
                new Button("Salva ristorante");

        Button indietroButton =
                new Button("Torna all'area ristoratore");

        indietroButton.setOnAction(
                event -> SceneManager.showRistoratore()
        );

        salvaButton.setOnAction(event -> {

            String nome =
                    nomeField.getText().trim();

            String nazione =
                    nazioneField.getText().trim();

            String citta =
                    cittaField.getText().trim();

            String indirizzo =
                    indirizzoField.getText().trim();

            String cucina =
                    cucinaField.getText().trim();

            if (nome.isEmpty()
                    || nazione.isEmpty()
                    || citta.isEmpty()
                    || cucina.isEmpty()) {

                messaggio.setText(
                        "Compilare tutti i campi obbligatori."
                );

                return;
            }

            double latitudine;
            double longitudine;
            double prezzoMedio;

            try {

                latitudine =
                        latitudineField.getText().isBlank()
                                ? 0.0
                                : Double.parseDouble(
                                        latitudineField.getText()
                                );

                longitudine =
                        longitudineField.getText().isBlank()
                                ? 0.0
                                : Double.parseDouble(
                                        longitudineField.getText()
                                );

                prezzoMedio =
                        Double.parseDouble(
                                prezzoField.getText()
                        );

            } catch (NumberFormatException e) {

                messaggio.setText(
                        "Inserire valori numerici validi."
                );

                return;
            }

            Ristorante ristorante =
                    new Ristorante(
                            nome,
                            nazione,
                            citta,
                            indirizzo,
                            latitudine,
                            longitudine,
                            cucina,
                            prezzoMedio,
                            deliveryBox.isSelected(),
                            prenotazioneBox.isSelected(),
                            SessionManager
                                    .getUtente()
                                    .getId()
                    );

            Request request =
                    new Request(
                            RequestType.ADD_RESTAURANT
                    );

            request.addData(
                    "ristorante",
                    ristorante
            );

            salvaButton.setDisable(true);

            messaggio.setText(
                    "Salvataggio in corso..."
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

                salvaButton.setDisable(false);

                Response response =
                        task.getValue();

                messaggio.setText(
                        response.getMessage()
                );

                if (response.isSuccess()) {

                    SceneManager.showRistoratore();
                }
            });

            task.setOnFailed(done -> {

                salvaButton.setDisable(false);

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
        });

        VBox root =
                new VBox(
                        12,
                        titolo,
                        nomeField,
                        nazioneField,
                        cittaField,
                        indirizzoField,
                        latitudineField,
                        longitudineField,
                        cucinaField,
                        prezzoField,
                        deliveryBox,
                        prenotazioneBox,
                        salvaButton,
                        indietroButton,
                        messaggio
                );

        root.setPadding(
                new Insets(25)
        );

        return root;
    }
}