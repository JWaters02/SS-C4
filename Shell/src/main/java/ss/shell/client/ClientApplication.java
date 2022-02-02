package ss.shell.client;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import static javafx.concurrent.Worker.State;

import java.io.IOException;

public class ClientApplication extends Application {
    /*@Override*/
//    public void start(Stage stage) throws IOException {
////        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
//
//        WebView webView = new WebView();
//        webView.getEngine().load(/*"https://localhost:9091/*/"https://google.com/");
//        VBox vBox = new VBox(webView);
//
//        Scene scene = new Scene(vBox, 1920, 1080);
//        stage.setTitle("Hello!");
//        stage.setScene(scene);
//        stage.show();
//    }
//
//    public static void main(String[] args) {
//        launch();
//    }
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(final Stage stage)
    {
        // Create the WebView
        WebView webView = new WebView();

        // Create the WebEngine
        final WebEngine webEngine = webView.getEngine();

        // Load the Start-Page
        webEngine.load("http://ntu.ac.uk");

        // Update the stage title when a new web page title is available
        webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>()
        {
            public void changed(ObservableValue<? extends Worker.State> ov, Worker.State oldState, Worker.State newState)
            {
                if (newState == Worker.State.SUCCEEDED)
                {
                    //stage.setTitle(webEngine.getLocation());
                    stage.setTitle(webEngine.getTitle());
                }
            }
        });

        // Create the VBox
        VBox root = new VBox();
        // Add the WebView to the VBox
        root.getChildren().add(webView);

        // Set the Style-properties of the VBox
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        // Create the Scene
        Scene scene = new Scene(root);
        // Add  the Scene to the Stage
        stage.setScene(scene);
        // Display the Stage
        stage.show();
    }
}