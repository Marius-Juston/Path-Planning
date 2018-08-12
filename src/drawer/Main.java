package drawer;

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

		primaryStage.getIcons().add(new Image(
			String.valueOf(Main.class.getResource("../calibration/Walton-Robotic-Logo.png"))));

//		primaryStage.setFullScreen(true);
		Parent root = PointPlacer.getRoot();
		primaryStage.setTitle("Path Planner");
		primaryStage.setScene(new Scene(root));

		primaryStage.show();


	}
}
