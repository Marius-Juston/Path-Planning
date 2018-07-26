package calibration;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {


	public Main() {
	}

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public final void start(Stage primaryStage) throws IOException {

		Parent root = FXMLLoader.load(Controller.class.getResource("fieldSelection.fxml"));
		primaryStage.setTitle("Path Planner");
		primaryStage.setScene(new Scene(root));
		primaryStage.show();
	}
}
