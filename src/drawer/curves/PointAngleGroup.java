package drawer.curves;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Group;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.waltonrobotics.controller.Pose;

public class PointAngleGroup extends Group {

	private static int index = 0;
	private final SimpleStringProperty name;
	public boolean selected;
	private boolean beingDragged;
	private ObservedDirectionalArrow observedDirectionalArrow;
	private Circle arrowRadius;
	private PositionPoint positionPoint;

	public PointAngleGroup(double centerX, double centerY) {
		positionPoint = new PositionPoint(centerX, centerY);

		this.name = new SimpleStringProperty(String.format("Point %d", index++));

		double length = 50;

		observedDirectionalArrow = new ObservedDirectionalArrow(positionPoint, 0, length, false, Color.RED);
		observedDirectionalArrow.setVisible(false);
		observedDirectionalArrow.setDisable(true);

		arrowRadius = new Circle(length, Color.TRANSPARENT);
		arrowRadius.setStroke(Color.GREEN);
		arrowRadius.setDisable(true);
		arrowRadius.setVisible(false);

		arrowRadius.centerXProperty().bindBidirectional(positionPoint.centerXProperty());
		arrowRadius.centerYProperty().bindBidirectional(positionPoint.centerYProperty());

		positionPoint.setOnMouseClicked(this::handleMouseClicked);
		positionPoint.setOnMouseDragged(event -> {
			positionPoint.movePoint(event);
			beingDragged = true;
		});

		positionPoint.getStyleClass().add("position-point");

		getChildren().addAll(arrowRadius, observedDirectionalArrow, positionPoint);
	}

	public ObservedDirectionalArrow getObservedDirectionalArrow() {
		return observedDirectionalArrow;
	}

	public Circle getArrowRadius() {
		return arrowRadius;
	}

	private void handleMouseClicked(MouseEvent mouseEvent) {

		if (!beingDragged) {
			if (mouseEvent.getButton() == MouseButton.PRIMARY) {
				if (!selected) {
					selected = true;
					enable();
				} else {
					selected = false;
					disable();
				}
			}
		}

		beingDragged = false;
	}

	public void disable() {
		observedDirectionalArrow.setVisible(false);
		observedDirectionalArrow.setDisable(true);
		arrowRadius.setVisible(false);
		arrowRadius.setDisable(true);
	}

	public void enable() {
		observedDirectionalArrow.setVisible(true);
		observedDirectionalArrow.setDisable(false);
		arrowRadius.setVisible(true);
		arrowRadius.setDisable(false);
	}

	public Pose getPose() {
		return new Pose(positionPoint.getCenterX(), positionPoint.getCenterY(), observedDirectionalArrow.getAngle());
	}
}
