package calibration;

import javafx.scene.shape.Shape;

public class Obstacle {

	private final ThreatLevel threatLevel;
	private final Shape definingShape;

	public Obstacle(ThreatLevel threatLevel, Shape definingShape) {
		this.threatLevel = threatLevel;
		this.definingShape = definingShape;
	}

	public ThreatLevel getThreatLevel() {
		return threatLevel;
	}

	public Shape getDefiningShape() {
		return definingShape;
	}
}
