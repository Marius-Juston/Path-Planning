package drawer.curves;

import calibration.Field;
import java.util.List;
import java.util.stream.Collectors;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import org.waltonrobotics.controller.Pose;

public class PointAngleGroup extends Group {

	private static int index = -2;
	//	private final SimpleStringProperty name;
	public SimpleBooleanProperty selected;
	Text name = new Text();
	private boolean beingDragged;
	private ObservedDirectionalArrow observedDirectionalArrow;
	private Circle arrowRadius;
	private PositionPoint positionPoint;
	private PointAngleGroup originPoint;
	private SimpleDoubleProperty degrees = new SimpleDoubleProperty();
	private SimpleDoubleProperty translatedX = new SimpleDoubleProperty(1.0);
	private SimpleDoubleProperty translatedY = new SimpleDoubleProperty(1.0);

	public PointAngleGroup(double centerX, double centerY) {
		positionPoint = new PositionPoint(centerX, centerY);

		name.setText(String.format("Point %d", index++));
		name.textOriginProperty().setValue(VPos.CENTER);
		name.xProperty().bind(positionPoint.centerXProperty().subtract(15));
		name.yProperty().bind(positionPoint.centerYProperty().subtract(15));

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

		selected = new SimpleBooleanProperty(false);
		selected.addListener((observable, oldValue, newValue) -> {
			if (newValue) {
				enable();
			} else {
				disable();
			}
		});

		getChildren().addAll(name, arrowRadius, observedDirectionalArrow, positionPoint);
	}

	public static List<Pose> mapToPoses(ObservableList<? extends PointAngleGroup> points) {
		return points.stream().map(PointAngleGroup::getPose).collect(Collectors.toList());
	}

	public PointAngleGroup getOriginPoint() {
		return originPoint;
	}

	public double getTranslatedX() {
		return translatedX.get();
	}

	public void setTranslatedX(double translatedX) {
		this.translatedX.set(translatedX);
	}

	public SimpleDoubleProperty translatedXProperty() {
		return translatedX;
	}

	public boolean isSelected() {
		return selected.get();
	}

	public void setSelected(boolean selected) {
		this.selected.set(selected);
	}

	public SimpleBooleanProperty selectedProperty() {
		return selected;
	}

	public ObservedDirectionalArrow getObservedDirectionalArrow() {
		return observedDirectionalArrow;
	}

	public Circle getArrowRadius() {
		return arrowRadius;
	}

	public String getName() {
		return name.getText();
	}

	public void setName(String name) {
		this.name.setText(name);
	}

	public DoubleProperty centerXProperty() {
		return positionPoint.centerXProperty();
	}

	public DoubleProperty centerYProperty() {
		return positionPoint.centerYProperty();
	}

	public StringProperty nameProperty() {
		return name.textProperty();
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

	public void handleMouseClicked(MouseEvent mouseEvent) {

		if (!beingDragged) {
			if (mouseEvent.getButton() == MouseButton.PRIMARY) {
				selected.set(!selected.get());
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
		return new Pose(positionPoint.getCenterX(), positionPoint.getCenterY(), -observedDirectionalArrow.getAngle());
	}

	public double getTranslatedY() {
		return translatedY.get();
	}

	public void setTranslatedY(double translatedY) {
		this.translatedY.set(translatedY);
	}

	public SimpleDoubleProperty translatedYProperty() {
		return translatedY;
	}

	public SimpleDoubleProperty angleProperty() {
		return observedDirectionalArrow.angleProperty();
	}

	public void setOrigin(PointAngleGroup originPoint) {
//TODO make setting the origin better
		this.originPoint = originPoint;

		translatedX
			.bind(positionPoint.centerXProperty().subtract(originPoint.centerXProperty()).multiply(Field.SCALE));
		translatedY
			.bind(positionPoint.centerYProperty().subtract(originPoint.centerYProperty()).multiply(Field.SCALE));

		DoubleBinding angleDegrees = Bindings
			.createDoubleBinding(() -> boundDegrees(StrictMath
					.toDegrees(observedDirectionalArrow.getAngle() - originPoint.getObservedDirectionalArrow().getAngle())),
				observedDirectionalArrow.angleProperty(), originPoint.getObservedDirectionalArrow().angleProperty());

		degrees.bind(angleDegrees);
	}

	public double boundDegrees(double degrees) {
		if (degrees > 180) {
			return -360 + degrees;
		} else if (degrees < -180) {
			return 360 + degrees;
		}
		return degrees;
	}
}
