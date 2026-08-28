package theknife.client.gui;

import javafx.application.Application;
import javafx.stage.Stage;
import theknife.client.ServerConnection;

public class TheKnifeApp extends Application {

    @Override
    public void start(Stage stage) {
        ServerConnection connection =
                new ServerConnection("localhost", 5000);

        SceneManager.initialize(stage, connection);

        stage.setTitle("TheKnife");
        SceneManager.showLogin();
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}