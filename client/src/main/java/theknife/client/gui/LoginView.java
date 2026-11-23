package theknife.client.gui;

import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;

import theknife.client.SessionManager;
import theknife.model.Utente;
import theknife.network.Request;
import theknife.network.RequestType;
import theknife.network.Response;

/**
 * Schermata di accesso all'applicazione.
 * Permette il login, la registrazione, l'accesso come ospite
 * e il recupero della password tramite domanda di sicurezza.
 *
 * @author Scafidi Michaela - 760101 - VA
 * @author Wafo Tene Wilfried Landry - 763687 - VA
 * @author Fotso Alex Castany - 762919 - VA
 */
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
        Button ospiteButton = new Button("Continua come Ospite");
        Button recuperaButton = new Button("Password dimenticata?");

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
                    Request request = new Request(RequestType.LOGIN);
                    request.addData("username", username);
                    request.addData("password", password);
                    return SceneManager.getConnection().sendRequest(request);
                }
            };

            task.setOnSucceeded(done -> {
                loginButton.setDisable(false);
                Response response = task.getValue();
                messaggio.setText(response.getMessage());

                if (response.isSuccess() && response.getData() instanceof Utente) {
                    Utente utente = (Utente) response.getData();
                    SessionManager.setUtente(utente);

                    if (SessionManager.isRistoratore()) {
                        SceneManager.showRistoratore();
                    } else {
                        SceneManager.showSearch();
                    }
                }
            });

            task.setOnFailed(done -> {
                loginButton.setDisable(false);
                messaggio.setText("Server non raggiungibile.");
                if (task.getException() != null) {
                    task.getException().printStackTrace();
                }
            });

            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();
        });

        registerButton.setOnAction(event -> SceneManager.showRegister());

        ospiteButton.setOnAction(event -> {
            SessionManager.logout();
            SceneManager.showSearch();
        });

        recuperaButton.setOnAction(event -> mostraDialogRecuperoPassword(usernameField.getText().trim()));

        VBox root = new VBox(
                15,
                titolo,
                usernameField,
                passwordField,
                loginButton,
                registerButton,
                ospiteButton,
                recuperaButton,
                messaggio
        );

        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));

        usernameField.setMaxWidth(300);
        passwordField.setMaxWidth(300);
        loginButton.setMaxWidth(300);
        registerButton.setMaxWidth(300);
        ospiteButton.setMaxWidth(300);
        recuperaButton.setMaxWidth(300);

        return root;
    }

    private void mostraDialogRecuperoPassword(String usernameIniziale) {

        TextInputDialog usernameDialog = new TextInputDialog(usernameIniziale);
        usernameDialog.setTitle("Recupero Password");
        usernameDialog.setHeaderText("Inserisci il tuo username");
        usernameDialog.setContentText("Username:");

        String username = usernameDialog.showAndWait().orElse(null);

        if (username == null || username.trim().isEmpty()) {
            return;
        }

        Request requestDomanda = new Request(RequestType.RESET_PASSWORD);
        requestDomanda.addData("username", username);
        requestDomanda.addData("step", "get_domanda");

        Task<Response> taskDomanda = new Task<>() {
            @Override
            protected Response call() throws Exception {
                return SceneManager.getConnection().sendRequest(requestDomanda);
            }
        };

        taskDomanda.setOnSucceeded(e -> {
            Response responseDomanda = taskDomanda.getValue();

            if (!responseDomanda.isSuccess()) {
                mostraAlert("Errore", responseDomanda.getMessage());
                return;
            }

            String domanda = (String) responseDomanda.getData();

            TextInputDialog rispostaDialog = new TextInputDialog();
            rispostaDialog.setTitle("Domanda di Sicurezza");
            rispostaDialog.setHeaderText(domanda);
            rispostaDialog.setContentText("Risposta:");

            String risposta = rispostaDialog.showAndWait().orElse(null);

            if (risposta == null || risposta.trim().isEmpty()) {
                return;
            }

            Request requestVerifica = new Request(RequestType.RESET_PASSWORD);
            requestVerifica.addData("username", username);
            requestVerifica.addData("risposta", risposta.trim());
            requestVerifica.addData("step", "verifica_risposta");

            Task<Response> taskVerifica = new Task<>() {
                @Override
                protected Response call() throws Exception {
                    return SceneManager.getConnection().sendRequest(requestVerifica);
                }
            };

            taskVerifica.setOnSucceeded(e2 -> {
                Response responseVerifica = taskVerifica.getValue();

                if (!responseVerifica.isSuccess()) {
                    mostraAlert("Errore", responseVerifica.getMessage());
                    return;
                }

                Dialog<String> passwordDialog = new Dialog<>();
                passwordDialog.setTitle("Nuova Password");
                passwordDialog.setHeaderText("Inserisci la nuova password");
                
                PasswordField nuovaPasswordField = new PasswordField();
                nuovaPasswordField.setPromptText("Nuova password");
                
                VBox content = new VBox(10, new Label("Nuova password:"), nuovaPasswordField);
                content.setPadding(new Insets(10));
                passwordDialog.getDialogPane().setContent(content);
                passwordDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

                passwordDialog.setResultConverter(button -> {
                    if (button == ButtonType.OK) {
                        return nuovaPasswordField.getText();
                    }
                    return null;
                });

                String nuovaPassword = passwordDialog.showAndWait().orElse(null);

                if (nuovaPassword == null || nuovaPassword.isEmpty()) {
                    return;
                }

                Request requestSalva = new Request(RequestType.RESET_PASSWORD);
                requestSalva.addData("username", username);
                requestSalva.addData("nuovaPassword", nuovaPassword);
                requestSalva.addData("step", "nuova_password");

                Task<Response> taskSalva = new Task<>() {
                    @Override
                    protected Response call() throws Exception {
                        return SceneManager.getConnection().sendRequest(requestSalva);
                    }
                };

                taskSalva.setOnSucceeded(e3 -> {
                    Response responseSalva = taskSalva.getValue();
                    mostraAlert("Recupero Password", responseSalva.getMessage());
                });

                Thread threadSalva = new Thread(taskSalva);
                threadSalva.setDaemon(true);
                threadSalva.start();
            });

            Thread threadVerifica = new Thread(taskVerifica);
            threadVerifica.setDaemon(true);
            threadVerifica.start();
        });

        Thread threadDomanda = new Thread(taskDomanda);
        threadDomanda.setDaemon(true);
        threadDomanda.start();
    }

    private void mostraAlert(String titolo, String messaggio) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titolo);
        alert.setHeaderText(null);
        alert.setContentText(messaggio);
        alert.showAndWait();
    }
}