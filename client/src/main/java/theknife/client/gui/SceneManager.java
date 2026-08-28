package theknife.client.gui;

import javafx.scene.Scene;
import javafx.stage.Stage;
import theknife.client.ServerConnection;

public final class SceneManager {

    private static Stage stage;
    private static ServerConnection connection;

    private SceneManager() {
    }

    public static void initialize(
            Stage primaryStage,
            ServerConnection serverConnection) {

        stage = primaryStage;
        connection = serverConnection;
    }

    public static ServerConnection getConnection() {
        return connection;
    }

    public static void showLogin() {
        stage.setScene(
                new Scene(new LoginView().getView(), 500, 450)
        );
    }

    public static void showRegister() {
        stage.setScene(
                new Scene(new RegisterView().getView(), 600, 700)
        );
    }
    public static void showSearch() {
    stage.setScene(
        new Scene(
            new SearchView().getView(),
            1000,
            650
        )
    );
}
}