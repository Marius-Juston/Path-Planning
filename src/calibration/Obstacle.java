package calibration;

import javafx.scene.Group;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;

public class Obstacle extends Group {

	private final ThreatLevel threatLevel;
	private final Shape definingShape;

	//	TODO fix the problems hat the padding and all cause the shapes to be translated wrongly
	public Obstacle(ThreatLevel threatLevel, Polygon definingShape) {
		this.threatLevel = threatLevel;
		this.definingShape = new Polygon(definingShape.getPoints().stream().mapToDouble(value -> value).toArray());
		getChildren().add(definingShape);
	}

	public Obstacle(ThreatLevel threatLevel, Path definingShape) {
		this.threatLevel = threatLevel;

		this.definingShape = new Path(definingShape.getElements());
		getChildren().add(definingShape);
	}

	public ThreatLevel getThreatLevel() {
		return threatLevel;
	}

	public Shape getDefiningShape() {
		return definingShape;
	}
}
