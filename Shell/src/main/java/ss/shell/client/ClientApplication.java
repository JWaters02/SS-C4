package ss.shell.client;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class ClientApplication extends Application {
    @Override
    public void start(Stage stage) {
        WebView webView = new WebView();
        final WebEngine webEngine = webView.getEngine();
        webEngine.load("http://localhost:2223");
        VBox vBox = new VBox();
        vBox.getChildren().add(webView);
        Scene scene = new Scene(vBox);
        stage.setTitle("Remote Shell");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}