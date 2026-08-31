package theknife.client;

import theknife.network.Request;
import theknife.network.RequestType;
import theknife.network.Response;

import java.time.LocalDate;

/**
 * @author Scafidi Michaela - 760101 - VA
 * @author Wafo Tene Wilfried Landry - 763687 - VA
 * @author Fotso Alex Castany - 762919 - VA
 */
public class TestClient {

    public static void main(String[] args) {

        ServerConnection connection =
                new ServerConnection("localhost", 5000);

        String usernameTest = "utente_test_01";
        String passwordTest = "Password123!";

        try {

            // ==========================================
            // TEST REGISTRAZIONE
            // ==========================================

            Request registerRequest =
                    new Request(RequestType.REGISTER);

            registerRequest.addData("nome", "Mario");
            registerRequest.addData("cognome", "Rossi");
            registerRequest.addData("username", usernameTest);
            registerRequest.addData("password", passwordTest);
            registerRequest.addData("ruolo", "cliente");
            registerRequest.addData(
                    "dataNascita",
                    LocalDate.of(2000, 1, 1)
            );
            registerRequest.addData(
                    "cittaDomicilio",
                    "Varese"
            );
            registerRequest.addData(
                    "domandaSicurezza",
                    "Nome del primo animale?"
            );
            registerRequest.addData(
                    "rispostaSicurezza",
                    "Fido"
            );

            Response registerResponse =
                    connection.sendRequest(registerRequest);

            System.out.println("=== REGISTRAZIONE ===");
            System.out.println(
                    "Success: " + registerResponse.isSuccess()
            );
            System.out.println(
                    "Message: " + registerResponse.getMessage()
            );

            // ==========================================
            // TEST LOGIN
            // ==========================================

            Request loginRequest =
                    new Request(RequestType.LOGIN);

            loginRequest.addData(
                    "username",
                    usernameTest
            );

            loginRequest.addData(
                    "password",
                    passwordTest
            );

            Response loginResponse =
                    connection.sendRequest(loginRequest);

            System.out.println();
            System.out.println("=== LOGIN ===");
            System.out.println(
                    "Success: " + loginResponse.isSuccess()
            );
            System.out.println(
                    "Message: " + loginResponse.getMessage()
            );

            if (loginResponse.getData() != null) {
                System.out.println(
                        "Utente: " + loginResponse.getData()
                );
            }

        } catch (Exception e) {

            System.err.println(
                    "Errore durante il test client: "
                    + e.getMessage()
            );
        }
    }
}