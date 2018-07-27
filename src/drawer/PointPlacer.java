package drawer;

import calibration.Controller;
import calibration.Field;
import calibration.Helper;
import drawer.context.PathTitledTab;
import drawer.curves.PointAngleGroup;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Accordion;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Shape;

public class PointPlacer implements Initializable {

	public ImageView field;
	@FXML
	public AnchorPane pointPlane;
	public Accordion titledPaneAccordion;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		if (Field.image == null) {
			try {
				File imageFile = new File("./src/FRC 2018 Field Drawings.png");
				Image defaultImage = Helper.getImage(imageFile);
				field.setImage(defaultImage);
				Field.image = field.getImage();
				Field.imageFile = imageFile;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			field.setImage(Field.image);
		}

		PathTitledTab pathTitledTab = new PathTitledTab();
		titledPaneAccordion.setExpandedPane(pathTitledTab);
		titledPaneAccordion.getPanes().add(pathTitledTab);
		pointPlane.getChildren().add(pathTitledTab.getKeyPoints());
	}

	public void saveData(ActionEvent actionEvent) {
	}

	public void loadData(ActionEvent actionEvent) {

	}

	public void openData(ActionEvent actionEvent) {

	}

	public void handlePointEvent(MouseEvent mouseEvent) {
		if (mouseEvent.getButton() == MouseButton.PRIMARY) {
			addPoint(mouseEvent);
		} else if (mouseEvent.getButton() == MouseButton.MIDDLE) {
			showPointInfo(mouseEvent);
		} else {
			removePoint(mouseEvent);
		}
	}

	private void removePoint(MouseEvent mouseEvent) {

	}

	/**
	 * Will show the angle of the point bold the point and allow you to change the angle direction
	 */
	private void showPointInfo(MouseEvent mouseEvent) {

	}


	public void addPoint(MouseEvent mouseEvent) {

		if (!(mouseEvent.getPickResult().getIntersectedNode() instanceof Shape)) {
			PointAngleGroup pointAngleCombo = new PointAngleGroup(mouseEvent.getX(), mouseEvent.getY());

			pointPlane.getChildren().add(pointAngleCombo);

			getExpandedPane().getKeyPoints().add(pointAngleCombo);
		}
	}

	private PathTitledTab getExpandedPane() {
		return (PathTitledTab) titledPaneAccordion.getExpandedPane();
	}

	public void goBackToFieldSelector(ActionEvent actionEvent) throws IOException {

		Parent root = FXMLLoader.load(Controller.class.getResource("../calibration/fieldSelection.fxml"));
		Helper.setRoot(actionEvent, root);
	}
}
