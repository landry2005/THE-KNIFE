package theknife.client;

import theknife.network.Request;
import theknife.network.RequestType;
import theknife.network.Response;

public class TestClient {

    public static void main(String[] args) {

        ServerConnection connection =
                new ServerConnection("localhost", 5000);

        Request request =
                new Request(RequestType.LOGIN);

        request.addData("username", "test");
        request.addData("password", "1234");

        try {

            Response response =
                    connection.sendRequest(request);

            System.out.println(
                    "Success: " + response.isSuccess()
            );

            System.out.println(
                    "Message: " + response.getMessage()
            );

        } catch (Exception e) {

            System.err.println(
                    "Errore di connessione: "
                    + e.getMessage()
            );
        }
    }
}