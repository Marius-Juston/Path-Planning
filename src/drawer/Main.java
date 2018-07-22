package drawer;

import calibration.Helper;
import javafx.application.Application;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.WritableDoubleValue;
import javafx.beans.value.WritableObjectValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

	public static final WritableDoubleValue scale = new SimpleDoubleProperty(1);
	public static final WritableObjectValue<String> unit = new SimpleStringProperty(Helper.PIXELS);

	public Main() {
	}

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public final void start(Stage primaryStage) throws java.io.IOException {
		Parent root = FXMLLoader.load(getClass().getResource("pointPlacer.fxml"));
		primaryStage.setTitle("Path Planner");
		primaryStage.setScene(new Scene(root));
		primaryStage.show();
	}
}
