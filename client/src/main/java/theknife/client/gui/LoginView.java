package theknife.client.gui;

import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import theknife.client.SessionManager;
import theknife.model.Utente;
import theknife.network.Request;
import theknife.network.RequestType;
import theknife.network.Response;

public class LoginView {

    public Parent getView() {

        Label titolo = new Label("Accesso a TheKnife");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Label messaggio = new Label();

        Button loginButton = new Button("Accedi");
        Button registerButton = new Button("Registrati");

        loginButton.setOnAction(event -> {

            String username = usernameField.getText().trim();
            String password = passwordField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                messaggio.setText("Inserire username e password.");
                return;
            }

            loginButton.setDisable(true);
            messaggio.setText("Connessione in corso...");

            Task<Response> task = new Task<>() {

                @Override
                protected Response call() throws Exception {

                    Request request =
                            new Request(RequestType.LOGIN);

                    request.addData("username", username);
                    request.addData("password", password);

                    return SceneManager
                            .getConnection()
                            .sendRequest(request);
                }
            };

            task.setOnSucceeded(done -> {

                loginButton.setDisable(false);

                Response response = task.getValue();

                messaggio.setText(
                        response.getMessage()
                );

                if (response.isSuccess()
                        && response.getData() instanceof Utente) {

                    Utente utente =
                            (Utente) response.getData();

                    // Salva l'utente nella sessione
                    SessionManager.setUtente(utente);

                    // Decide quale interfaccia mostrare
                    // in base al ruolo dell'utente
                    if (SessionManager.isRistoratore()) {

                        SceneManager.showRistoratore();

                    } else {

                        SceneManager.showSearch();
                    }
                }
            });

            task.setOnFailed(done -> {

                loginButton.setDisable(false);

                messaggio.setText(
                        "Server non raggiungibile."
                );

                if (task.getException() != null) {
                    task.getException().printStackTrace();
                }
            });

            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();
        });

        registerButton.setOnAction(
                event -> SceneManager.showRegister()
        );

        VBox root = new VBox(
                15,
                titolo,
                usernameField,
                passwordField,
                loginButton,
                registerButton,
                messaggio
        );

        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));

        usernameField.setMaxWidth(300);
        passwordField.setMaxWidth(300);

        return root;
    }
}