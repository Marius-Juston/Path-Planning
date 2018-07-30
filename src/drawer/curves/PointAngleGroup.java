package drawer.curves;

import calibration.Field;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.SimpleDoubleProperty;
import org.waltonrobotics.controller.Pose;

public class PointAngleGroup extends PointGroup {

	private static int index = 0;
	private OriginPoint originPoint;
	private SimpleDoubleProperty translatedX = new SimpleDoubleProperty(1.0);
	private SimpleDoubleProperty translatedY = new SimpleDoubleProperty(1.0);
	private SimpleDoubleProperty translatedAngle = new SimpleDoubleProperty(1.0);

	public PointAngleGroup(double centerX, double centerY) {
		super(centerX, centerY);

		name.setText(String.format("Point %d", index++));
	}

	public static List<Pose> mapToPoses(Collection<? extends PointAngleGroup> points) {
		return points.stream().map(PointAngleGroup::getPose).collect(Collectors.toList());
	}

	public PointGroup getOriginPoint() {
		return originPoint;
	}

	public SimpleDoubleProperty translatedXProperty() {
		return translatedX;
	}

	public SimpleDoubleProperty translatedYProperty() {
		return translatedY;
	}


	public SimpleDoubleProperty translatedAngleProperty() {
		return translatedAngle;
	}

	public Pose getPose() {
		return new Pose(getPositionPoint().getCenterX(), getPositionPoint().getCenterY(),
			-getObservedDirectionalArrow().getAngle());
	}

	public void setOrigin(OriginPoint originPoint) {
//TODO make setting the origin better
		this.originPoint = originPoint;

		translatedX.unbind();
		translatedX
			.bind(getPositionPoint().centerXProperty().subtract(originPoint.centerXProperty()).multiply(Field.SCALE));

		translatedY.unbind();
		translatedY
			.bind(getPositionPoint().centerYProperty().subtract(originPoint.centerYProperty()).multiply(Field.SCALE));

		translatedAngle.unbind();
		translatedAngle.bind(angleProperty().subtract(originPoint.angleProperty()));

		DoubleBinding angleDegrees = Bindings
			.createDoubleBinding(() -> boundDegrees(StrictMath.toDegrees(translatedAngle.get())),
				translatedAngle);

		degreesProperty().unbind();
		degreesProperty().bind(angleDegrees);
	}


}
