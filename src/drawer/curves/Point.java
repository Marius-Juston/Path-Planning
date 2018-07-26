package drawer.curves;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import org.waltonrobotics.controller.Pose;

public class Point {

	private static int index = 0;
	private final SimpleStringProperty name;
	private final SimpleDoubleProperty x;
	private final SimpleDoubleProperty y;
	private final SimpleDoubleProperty angle;

	public Point(double x, double y) {
		this(String.format("Point %d", index++), x, y, 0);
	}

	public Point(String name, double x, double y, double angle) {
		this.name = new SimpleStringProperty(name);
		this.x = new SimpleDoubleProperty(x);
		this.y = new SimpleDoubleProperty(y);
		this.angle = new SimpleDoubleProperty(angle);
	}

	public String getName() {
		return name.get();
	}

	public void setName(String name) {
		this.name.set(name);
	}

	public SimpleStringProperty nameProperty() {
		return name;
	}

	public double getX() {
		return x.get();
	}

	public void setX(double x) {
		this.x.set(x);
	}

	public SimpleDoubleProperty xProperty() {
		return x;
	}

	public double getY() {
		return y.get();
	}

	public void setY(double y) {
		this.y.set(y);
	}

	public SimpleDoubleProperty yProperty() {
		return y;
	}

	public double getAngle() {
		return angle.get();
	}

	public void setAngle(double angle) {
		this.angle.set(angle);
	}

	public SimpleDoubleProperty angleProperty() {
		return angle;
	}

	public Pose getPose() {
		return new Pose(getX(), getY(), getAngle());
	}
}
