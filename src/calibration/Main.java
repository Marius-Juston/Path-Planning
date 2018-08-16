package calibration;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {


    public Main() {
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public final void start(Stage primaryStage) throws IOException {

        Parent root = Controller.getRoot();
//		primaryStage.getIcons().add(new Image(drawer.Main.class.getResourceAsStream("Walton-Robotic-Logo.png")));

//		primaryStage.getIcons().add(new Image(
//			String.valueOf(drawer.Main.class.getResource("../calibration/Walton-Robotic-Logo.png"))));
        primaryStage.getIcons().add(new Image(
                Main.class.getResource("Walton-Robotic-Logo.png").toExternalForm()));

//		primaryStage.setFullScreen(true);
        primaryStage.setTitle("Path Planner");
        primaryStage.setScene(new Scene(root));

        primaryStage.setOnCloseRequest(event -> NetworkTable.shutdown());

        primaryStage.show();
    }
}
