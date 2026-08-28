package theknife.client.gui;

import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import theknife.network.Request;
import theknife.network.RequestType;
import theknife.network.Response;

public class RegisterView {

    public Parent getView() {

        Label titolo = new Label("Registrazione");

        TextField nomeField = new TextField();
        nomeField.setPromptText("Nome");

        TextField cognomeField = new TextField();
        cognomeField.setPromptText("Cognome");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        DatePicker dataNascita = new DatePicker();

        TextField cittaField = new TextField();
        cittaField.setPromptText("Città di domicilio");

        ComboBox<String> ruoloBox = new ComboBox<>();
        ruoloBox.getItems().addAll("cliente", "ristoratore");

        TextField domandaField = new TextField();
        domandaField.setPromptText("Domanda di sicurezza");

        TextField rispostaField = new TextField();
        rispostaField.setPromptText("Risposta di sicurezza");

        Label messaggio = new Label();

        Button registerButton = new Button("Crea account");
        Button indietroButton = new Button("Torna al login");

        registerButton.setOnAction(event -> {

            if (nomeField.getText().trim().isEmpty()
                    || cognomeField.getText().trim().isEmpty()
                    || usernameField.getText().trim().isEmpty()
                    || passwordField.getText().isEmpty()
                    || cittaField.getText().trim().isEmpty()
                    || ruoloBox.getValue() == null
                    || domandaField.getText().trim().isEmpty()
                    || rispostaField.getText().trim().isEmpty()) {

                messaggio.setText(
                        "Compilare tutti i campi obbligatori."
                );
                return;
            }

            registerButton.setDisable(true);
            messaggio.setText("Registrazione in corso...");

            Task<Response> task = new Task<>() {
                @Override
                protected Response call() throws Exception {

                    Request request =
                            new Request(RequestType.REGISTER);

                    request.addData("nome",
                            nomeField.getText().trim());

                    request.addData("cognome",
                            cognomeField.getText().trim());

                    request.addData("username",
                            usernameField.getText().trim());

                    request.addData("password",
                            passwordField.getText());

                    request.addData("ruolo",
                            ruoloBox.getValue());

                    request.addData("dataNascita",
                            dataNascita.getValue());

                    request.addData("cittaDomicilio",
                            cittaField.getText().trim());

                    request.addData("domandaSicurezza",
                            domandaField.getText().trim());

                    request.addData("rispostaSicurezza",
                            rispostaField.getText().trim());

                    return SceneManager
                            .getConnection()
                            .sendRequest(request);
                }
            };

            task.setOnSucceeded(done -> {

                registerButton.setDisable(false);

                Response response = task.getValue();
                messaggio.setText(response.getMessage());

                if (response.isSuccess()) {
                    SceneManager.showLogin();
                }
            });

            task.setOnFailed(done -> {
                registerButton.setDisable(false);
                messaggio.setText("Server non raggiungibile.");
            });

            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();
        });

        indietroButton.setOnAction(
                event -> SceneManager.showLogin()
        );

        VBox root = new VBox(
                12,
                titolo,
                nomeField,
                cognomeField,
                usernameField,
                passwordField,
                dataNascita,
                cittaField,
                ruoloBox,
                domandaField,
                rispostaField,
                registerButton,
                indietroButton,
                messaggio
        );

        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));

        nomeField.setMaxWidth(350);
        cognomeField.setMaxWidth(350);
        usernameField.setMaxWidth(350);
        passwordField.setMaxWidth(350);
        dataNascita.setMaxWidth(350);
        cittaField.setMaxWidth(350);
        ruoloBox.setMaxWidth(350);
        domandaField.setMaxWidth(350);
        rispostaField.setMaxWidth(350);

        return root;
    }
}