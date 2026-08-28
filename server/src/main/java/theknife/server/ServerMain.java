package theknife.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain {

    private static final int PORT = 5000;

    public static void main(String[] args) {

        System.out.println("Avvio del server TheKnife...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            System.out.println("Server in ascolto sulla porta " + PORT);

            while (true) {

               Socket clientSocket = serverSocket.accept();

System.out.println(
    "Nuovo client connesso: "
    + clientSocket.getInetAddress()
);

ClientHandler handler = new ClientHandler(clientSocket);

Thread thread = new Thread(handler);

thread.start();
            }

        } catch (IOException e) {
            System.err.println(
                "Errore del server: " + e.getMessage()
            );
        }
    }
}