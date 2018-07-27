package drawer.curves;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
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

	private DoubleBinding angleDegrees;
	private SimpleDoubleProperty degrees = new SimpleDoubleProperty();


	public PointAngleGroup(double centerX, double centerY) {
		positionPoint = new PositionPoint(centerX, centerY);

		this.name = new SimpleStringProperty(String.format("Point %d", index++));

		double length = 50;

		observedDirectionalArrow = new ObservedDirectionalArrow(positionPoint, 0, length, false, Color.RED);
		observedDirectionalArrow.setVisible(false);
		observedDirectionalArrow.setDisable(true);

		angleDegrees = Bindings.createDoubleBinding(() -> StrictMath.toDegrees(observedDirectionalArrow.getAngle()),
			observedDirectionalArrow.angleProperty());
		degrees.bind(angleDegrees);

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

	public String getName() {
		return name.get();
	}

	public void setName(String name) {
		this.name.set(name);
	}

	public DoubleProperty centerXProperty() {
		return positionPoint.centerXProperty();
	}


	public DoubleProperty centerYProperty() {
		return positionPoint.centerYProperty();
	}

	public SimpleStringProperty nameProperty() {
		return name;
	}


	private void setAngle(double value) {
		observedDirectionalArrow.angleProperty().set(Math.toRadians(value));
	}

	public double getDegrees() {
		return degrees.get();
	}

	public SimpleDoubleProperty degreesProperty() {
		return degrees;
	}

	public PositionPoint getPositionPoint() {
		return positionPoint;
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
