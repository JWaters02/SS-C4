package ss.shell.examples;

import javafx.beans.value.ChangeListener;
import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.scene.web.WebEngine;
import static javafx.concurrent.Worker.State;

public class FxWebViewExample1 extends Application {
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(final Stage stage) {
        WebView webView = new WebView();
        final WebEngine webEngine = webView.getEngine();
        webEngine.load("localhost:2222");

        // Update the stage title when a new web page title is available
        webEngine.getLoadWorker().stateProperty().addListener((ov, oldState, newState) -> {
            if (newState == State.SUCCEEDED) {
                stage.setTitle(webEngine.getTitle());
            }
        });

        VBox root = new VBox();
        root.getChildren().add(webView);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
