package theknife.client;

import theknife.network.Request;
import theknife.network.Response;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * @author Scafidi Michaela - 760101 - VA
 * @author Wafo Tene Wilfried Landry - 763687 - VA
 * @author Fotso Alex Castany - 762919 - VA
 */

public class ServerConnection {

    private final String host;
    private final int port;

    public ServerConnection(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public Response sendRequest(Request request)
            throws IOException, ClassNotFoundException {

        try (
            Socket socket = new Socket(host, port);

            ObjectOutputStream out =
                new ObjectOutputStream(socket.getOutputStream());

            ObjectInputStream in =
                new ObjectInputStream(socket.getInputStream())
        ) {

            out.writeObject(request);
            out.flush();

            return (Response) in.readObject();
        }
    }
}