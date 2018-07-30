package calibration;

import javafx.scene.paint.Color;

public enum ThreatLevel {
	WARNING(Color.rgb(0, 0, 255, .5), Color.ORANGE), ERROR(Color.gray(.5, .6), Color.RED);

	private final Color displayColor;
	private final Color overlayColor;

	ThreatLevel(Color displayColor, Color overlayColor) {
		this.displayColor = displayColor;

		this.overlayColor = overlayColor;
	}

	public Color getOverlayColor() {
		return overlayColor;
	}

	public Color getDisplayColor() {
		return displayColor;
	}
}
