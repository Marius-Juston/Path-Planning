package calibration;

import javafx.scene.Group;
import javafx.scene.shape.Shape;

public class Obstacle extends Group {

	private final ThreatLevel threatLevel;
	private final Shape definingShape;

	public Obstacle(ThreatLevel threatLevel, Shape definingShape) {
		this.threatLevel = threatLevel;
		this.definingShape = definingShape;
		getChildren().add(definingShape);
	}

	public ThreatLevel getThreatLevel() {
		return threatLevel;
	}

	public Shape getDefiningShape() {
		return definingShape;
	}
}
