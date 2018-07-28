package drawer.content;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class RenameDialog {

	private static final Stage primaryStage = new Stage();
	private static String newName;

	static {
		primaryStage.initModality(Modality.APPLICATION_MODAL);
//		primaryStage.initStyle(StageStyle.UTILITY);
		primaryStage.setTitle("Unit converter");
	}

	public TextField pathName;

	public static String display(String previousName) throws IOException {

		newName = previousName;

		Parent root = FXMLLoader.load(RenameDialog.class.getResource("renameDialog.fxml"));
		((TextField) root.getChildrenUnmodifiable().get(1)).setText(previousName);

		primaryStage.setScene(new Scene(root));
		primaryStage.showAndWait();

		return newName;
	}

	public void submitName(ActionEvent actionEvent) {
		newName = pathName.getText();
		primaryStage.close();
	}

	public void cancel() {
		primaryStage.close();
	}
}
