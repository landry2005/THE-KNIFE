package theknife.server;

import theknife.gestione.GestoreUtenti;
import theknife.network.Request;
import theknife.network.Response;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDate;

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

                    String nome =
                        (String) request.getData("nome");

                    String cognome =
                        (String) request.getData("cognome");

                    String usernameRegistrazione =
                        (String) request.getData("username");

                    String passwordRegistrazione =
                        (String) request.getData("password");

                    String ruolo =
                        (String) request.getData("ruolo");

                    LocalDate dataNascita =
                        (LocalDate) request.getData("dataNascita");

                    String cittaDomicilio =
                        (String) request.getData("cittaDomicilio");

                    String domandaSicurezza =
                        (String) request.getData("domandaSicurezza");

                    String rispostaSicurezza =
                        (String) request.getData("rispostaSicurezza");

                    boolean registrazioneOk =
                        gestoreUtenti.registrazione(
                            nome,
                            cognome,
                            usernameRegistrazione,
                            passwordRegistrazione,
                            ruolo,
                            dataNascita,
                            cittaDomicilio,
                            domandaSicurezza,
                            rispostaSicurezza
                        );

                    if (registrazioneOk) {
                        response = new Response(
                            true,
                            "Registrazione effettuata con successo"
                        );
                    } else {
                        response = new Response(
                            false,
                            "Username già esistente o errore durante la registrazione"
                        );
                    }

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