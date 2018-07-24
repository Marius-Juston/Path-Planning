package calibration;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.stage.Window;

public enum Helper {
	;

	public static final String PIXELS = "pixels";

	private Helper() {
	}

	public static boolean isDouble(String text) {
		try {
			Double.parseDouble(text);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public static Window getWindow(ActionEvent event) {
		return ((Node) event.getSource()).getScene().getWindow();
	}


	public static Image getImage(File file) throws FileNotFoundException {
		BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
		return new Image(bufferedInputStream);
	}

	public static class Unit {

		private final double value;
		private String unit;

		Unit(double value, String unit) {
			this.value = value;
			this.unit = unit;
		}

		public final double getValue() {
			return value;
		}

		public final String getUnit() {
			return unit;
		}

		public final void setUnit(String unit) {
			this.unit = unit;
		}

		@Override
		public final String toString() {
			return "Unit{" +
				"value=" + value +
				", UNIT='" + unit + '\'' +
				'}';
		}
	}
}