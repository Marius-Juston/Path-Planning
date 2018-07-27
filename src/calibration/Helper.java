package calibration;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
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

	public static void setRoot(ActionEvent actionEvent, Parent rootToSetTo) {
		((Button) actionEvent.getSource()).getScene().setRoot(rootToSetTo);
	}

	public static double[][] dotProduct(double[][] matrixA, double[][] matrixB) {
		double[][] matrix = new double[matrixA.length][matrixB[0].length];

		if (matrixA[0].length != matrixB.length) {
			throw new IllegalArgumentException(
				"Matrix A number of columns and matrix B number of rows need to be the same");
		}

		for (int i = 0; i < matrixA.length; i++) {
			for (int z = 0; z < matrixB[0].length; z++) {
				double sum = 0;
				for (int j = 0; j < matrixB.length; j++) {
					sum += (matrixA[i][j] * matrixB[z][j]);
				}

				matrix[i][z] = sum;
			}
		}

		return matrix;
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