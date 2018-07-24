package calibration;

import calibration.Helper.Unit;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class DistanceConverter implements Initializable {

	private static final Alert confirmation = new Alert(AlertType.CONFIRMATION,
		"Are you sure this is the correct conversion?", ButtonType.YES, ButtonType.NO);
	private static final Stage primaryStage = new Stage();
	private static Unit info;
	private static double pixelUnit;

	static {
		primaryStage.initModality(Modality.APPLICATION_MODAL);
//		primaryStage.initStyle(StageStyle.UTILITY);
		primaryStage.setTitle("Unit converter");
	}

	@FXML
	private TextField pixelDistance;

	public DistanceConverter() {
	}

	static Unit display(double pixelUnit) throws IOException {
		info = null;
		DistanceConverter.pixelUnit = pixelUnit;

		Parent root = FXMLLoader.load(DistanceConverter.class.getResource("conversion.fxml"));

		primaryStage.setScene(new Scene(root));

		primaryStage.showAndWait();

		return info;
	}

	@Override
	public final void initialize(URL location, ResourceBundle resources) {
		pixelDistance.setText(String.valueOf(pixelUnit));

	}

	@FXML
	public void retrieveInfo(ActionEvent actionEvent) {
		TextField text = ((TextField) actionEvent.getSource());

		if (!text.getText().isEmpty()) {
			String[] data = text.getText().split(" ");

			if (Helper.isDouble(data[0])) {
				info = new Unit(Double.parseDouble(data[0]), Helper.PIXELS);

				if (data.length >= 2) {
					info.setUnit(data[1]);
				}

				Optional<ButtonType> buttonTypeOptional = confirmation.showAndWait();
				if (buttonTypeOptional.isPresent()) {
					if (buttonTypeOptional.get() == ButtonType.YES) {
						Helper.getWindow(actionEvent).hide();
					}
				}
			} else {
				text.setText(null);
			}
		}
	}
}