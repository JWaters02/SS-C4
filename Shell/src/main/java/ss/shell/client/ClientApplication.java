package ss.shell.client;

import javafx.application.Application;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
//        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));

        WebView webView = new WebView();
        webView.getEngine().load(/*"https://localhost:9091/*/"https://google.com/");
        VBox vBox = new VBox(webView);

        Scene scene = new Scene(vBox, 1920, 1080);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}