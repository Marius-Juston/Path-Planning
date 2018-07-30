package drawer.curves;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.waltonrobotics.controller.Pose;

public class PositionPoint extends Circle {


	public PositionPoint(double centerX, double centerY) {
		super(centerX, centerY, 4, Color.BLUE);

		setOnMouseDragged(this::movePoint);


	}

	public PositionPoint(Pose pose) {
		this(pose.getX(), pose.getY());
	}


	public void movePoint(MouseEvent mouseEvent) {
		if (mouseEvent.getButton() == MouseButton.PRIMARY) {
			setCenterX(mouseEvent.getX());
			setCenterY(mouseEvent.getY());
		}
	}

	public boolean equalsPosition(PositionPoint positionPoint) {
		return (getCenterX() == positionPoint.getCenterX()) && (getCenterY() == positionPoint.getCenterY());
	}
}
