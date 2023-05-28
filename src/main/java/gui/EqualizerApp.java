package gui;
import javafx.scene.Parent;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import gui.FXMLDocumentController;
import java.io.IOException;

public class EqualizerApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(EqualizerApp.class.getResource("gui.fxml"));
        Parent root = FXMLLoader.load(getClass().getResource("gui.fxml"));
        Scene scene = new Scene(root);
        stage.setTitle("Графический эквалайзер со звуковыми эффектами");
        stage.setScene(scene);
        scene.getStylesheets().add
                (EqualizerApp.class.getResource("style.css").toExternalForm());
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}