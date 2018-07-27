package drawer.curves;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class PositionPoint extends Circle {

	private final Point point;

	public PositionPoint(double centerX, double centerY) {
		super(centerX, centerY, 4, Color.BLUE);

		point = new Point(centerX, centerY);

		centerXProperty().bindBidirectional(point.xProperty());
		centerYProperty().bindBidirectional(point.yProperty());

		setOnMouseDragged(this::movePoint);
	}


	public void movePoint(MouseEvent mouseEvent) {
		if (mouseEvent.getButton() == MouseButton.PRIMARY) {
			point.xProperty().set(mouseEvent.getX());
			point.yProperty().set(mouseEvent.getY());
		}
	}

	public Point getPoint() {
		return point;
	}
}
