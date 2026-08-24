package theknife.server;

import theknife.network.Request;
import theknife.network.Response;
import theknife.gestione.GestoreUtenti;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final GestoreUtenti gestoreUtenti;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        this.gestoreUtenti = new GestoreUtenti();
    }

    @Override
    public void run() {

        try (
            ObjectOutputStream out =
                new ObjectOutputStream(socket.getOutputStream());

            ObjectInputStream in =
                new ObjectInputStream(socket.getInputStream())
        ) {

            Request request = (Request) in.readObject();

            System.out.println(
                "Richiesta ricevuta: " + request.getType()
            );

            Response response;

            switch (request.getType()) {

                case LOGIN:

    String username =
        (String) request.getData("username");

    String password =
        (String) request.getData("password");

    boolean loginOk =
        gestoreUtenti.login(username, password);

    if (loginOk) {

        response = new Response(
            true,
            "Login effettuato con successo",
            gestoreUtenti.getUtenteCorrente()
        );

    } else {

        response = new Response(
            false,
            "Username o password non validi"
        );
    }

    break;

                case REGISTER:
                    response = new Response(
                        true,
                        "Richiesta REGISTER ricevuta"
                    );
                    break;

                case SEARCH:
                    response = new Response(
                        true,
                        "Richiesta SEARCH ricevuta"
                    );
                    break;

                default:
                    response = new Response(
                        false,
                        "Tipo di richiesta non ancora gestito"
                    );
                    break;
            }

            out.writeObject(response);
            out.flush();

        } catch (IOException | ClassNotFoundException e) {

            System.err.println(
                "Errore nella gestione del client: "
                + e.getMessage()
            );

        } finally {

            try {
                socket.close();

            } catch (IOException e) {

                System.err.println(
                    "Errore nella chiusura del socket: "
                    + e.getMessage()
                );
            }
        }
    }
}