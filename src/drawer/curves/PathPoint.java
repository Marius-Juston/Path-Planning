package drawer.curves;

import static javafx.scene.paint.Color.RED;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.waltonrobotics.controller.Pose;

public class PathPoint extends Circle {


	public PathPoint(double centerX, double centerY, Color color) {
		super(centerX, centerY, 2, color);

		setOnMouseDragged(this::movePoint);

	}

	public PathPoint(double centerX, double centerY) {
		super(centerX, centerY, 2, RED);

		setOnMouseDragged(this::movePoint);

	}

	public PathPoint(Pose pose) {
		this(pose.getX(), pose.getY());
	}

	public PathPoint(Pose pose, Color rgb) {
		this(pose.getX(), pose.getY(), rgb);
	}


	public void movePoint(MouseEvent mouseEvent) {
		if (mouseEvent.getButton() == MouseButton.PRIMARY) {
			setCenterX(mouseEvent.getX());
			setCenterY(mouseEvent.getY());
		}
	}

}
