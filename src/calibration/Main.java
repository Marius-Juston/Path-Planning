package calibration;

import java.io.IOException;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

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
		primaryStage.show();
	}
}
