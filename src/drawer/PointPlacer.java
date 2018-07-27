package drawer;

import calibration.Controller;
import calibration.Field;
import calibration.Helper;
import drawer.curves.PointAngleCombo;
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

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		if (Field.image == null) {
			try {
				Image defaultImage = Helper.getImage(new File("./src/FRC 2018 Field Drawings.png"));
				field.setImage(defaultImage);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			field.setImage(Field.image);
		}
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

//		PositionPoint positionPoint = new PositionPoint(mouseEvent.getX(), mouseEvent.getY());

//		Arrow arrow = new Arrow(positionPoint.getCenterX(), positionPoint.getCenterY(), .8, 0, 0.195, true, -1, 0.4, "full", 0, false);
//		Arrow arrow = new Arrow(positionPoint.getCenterX(), positionPoint.getCenterY(), 360, 50, false);

//		ObservedDirectionalArrow arrow = new ObservedDirectionalArrow(positionPoint, 0, 50, false);

//		arrow.setFill(Color.BLACK);

//		pointPlane.getChildren().addAll(positionPoint, arrow);

		if (!(mouseEvent.getPickResult().getIntersectedNode() instanceof Shape)) {
			PointAngleCombo pointAngleCombo = new PointAngleCombo(mouseEvent.getX(), mouseEvent.getY());
			showPoint(pointAngleCombo);
		}
	}

	public void showPoint(PointAngleCombo pointAngleCombo) {
		pointPlane.getChildren()
			.addAll(pointAngleCombo.getArrowRadius(), pointAngleCombo.getObservedDirectionalArrow(), pointAngleCombo);
	}

	public void goBackToFieldSelector(ActionEvent actionEvent) throws IOException {

		Parent root = FXMLLoader.load(Controller.class.getResource("../calibration/fieldSelection.fxml"));
		Helper.setRoot(actionEvent, root);
	}
}
