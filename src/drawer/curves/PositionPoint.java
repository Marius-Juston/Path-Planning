package drawer.curves;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class PositionPoint extends Circle {


	public PositionPoint(double centerX, double centerY) {
		super(centerX, centerY, 4, Color.BLUE);

		setOnMouseDragged(this::movePoint);
	}


	public void movePoint(MouseEvent mouseEvent) {
		if (mouseEvent.getButton() == MouseButton.PRIMARY) {
			setCenterX(mouseEvent.getX());
			setCenterY(mouseEvent.getY());
		}
	}

}
