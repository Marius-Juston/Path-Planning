package calibration;

import javafx.scene.paint.Color;

public enum ThreatLevel {
	WARNING(Color.rgb(0, 0, 255, .5), Color.ORANGE, "Warning the robot is passing on a dangerous place"), ERROR(Color.gray(.5, .6), Color.RED, "Error given the field constrains the robot would not be able to go here");

	private final Color displayColor;
	private final Color overlayColor;
	private final String message;

	ThreatLevel(Color displayColor, Color overlayColor, String message) {
		this.displayColor = displayColor;

		this.overlayColor = overlayColor;
		this.message = message;
	}

	public Color getOverlayColor() {
		return overlayColor;
	}

	public Color getDisplayColor() {
		return displayColor;
	}

	public String getMessage() {
		return message;
	}
}
