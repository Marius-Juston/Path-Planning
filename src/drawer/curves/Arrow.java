package drawer.curves;

import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class Arrow extends Polygon {


	public Arrow(double x, double y, double dx, double dy,
		double width, boolean length_includes_head,
		double head_width, double head_length, String shape, double overhang,
		boolean head_starts_at_zero, Color fill) {
		if (head_width == -1) {
			head_width = 3 * width;
		}
		if (head_length == -1) {
			head_length = 1.5 * head_width;
		}

		double distance = Math.hypot(dx, dy);

		double length;
		if (length_includes_head) {
			length = distance;
		} else {
			length = distance + head_length;
		}

//		double vertices[][];

		double[][] coords;

		if (length == -1) {
//			vertices = new double[0][0];  //display nothing if empty
			coords = new double[0][0];
		} else {
//            #start by drawing horizontal arrow, point at(0, 0)
			double hw = head_width;
			double hl = head_length;
			double hs = overhang;
			double lw = width;
//				, hl, hs, lw = head_width, head_length, overhang, width
			double[][] left_half_arrow = new double[][]{
				{0.0, 0.0},                  //tip
				{-hl, -hw / 2.0},             //leftmost
				{-hl * (1 - hs), -lw / 2.0},  //meets stem
				{-length, -lw / 2.0},          //bottom left
				{-length, 0},
			};

			//if we 're not including the head, shift up by head length
			if (!length_includes_head) {
				for (int i = 0; i < left_half_arrow.length; i++) {
					left_half_arrow[i][0] += head_length;
				}
			}
			//if the head starts at 0, shift up by another head length
			if (head_starts_at_zero) {
				for (int i = 0; i < left_half_arrow.length; i++) {
					left_half_arrow[i][0] += (head_length / 2.0);
				}
			}

			//figure out the shape, and complete accordingly
			if (shape.equals("left")) {
				coords = left_half_arrow;
			} else {
				double[][] right_half_arrow = new double[left_half_arrow.length][];

				for (int i = 0; i < left_half_arrow.length; i++) {
					right_half_arrow[i] = new double[]{left_half_arrow[i][0], left_half_arrow[i][1] * -1};
				}

				if (shape.equals("right")) {
					coords = right_half_arrow;
				} else if (shape.equals("full")) {
					//The half -arrows contain the midpoint of the stem,
					//which we can omit from the full arrow.Including it
					//twice caused a problem with xpdf.
					int size = left_half_arrow.length - 1 + right_half_arrow.length - 1;

					coords = new double[size][2];

					int i = 0;
					for (; i < left_half_arrow.length - 1; i++) {
						coords[i] = left_half_arrow[i];
					}
					for (int j = right_half_arrow.length - 2; j >= 0; j--, i++) {
						coords[i] = right_half_arrow[j];
					}
				} else {
					throw new IllegalArgumentException(String.format("Got unknown shape: %s", shape));
				}
			}
		}

		double cx, sx;
		if (distance != 0) {
			cx = dx / distance;
			sx = dy / distance;
		} else {
			//Account for division by zero
			cx = 0;
			sx = 1;
		}
		double[][] M = new double[][]{{cx, sx}, {-sx, cx}};
		double[][] vertices = new double[coords.length][2];

//		Does the dot product of coords and M
		for (int i = 0; i < coords.length; i++) {
			for (int z = 0; z < M[0].length; z++) {
				double sum = 0;
				for (int j = 0; j < M.length; j++) {
					sum += (coords[i][j] * M[z][j]);
				}

				vertices[i][z] = sum;
			}
		}

		for (int i = 0; i < vertices.length; i++) {
			vertices[i][0] += (x + dx);
			vertices[i][1] += (y - dy)/*Due to graphics reason - instead of +*/;
		}

		for (double[] vert : vertices) {
			for (double aVert : vert) {
				getPoints().add(aVert);
			}
		}

		setFill(fill);
	}

	public Arrow(double x, double y, double dx, double dy) {
		this(x, y, dx, dy, Color.RED);
	}

	public Arrow(double x, double y, double dx, double dy, Color fill) {
		this(x, y, dx, dy, 4, true, -1, 6, "full", 0, false, fill);
	}


	public Arrow(double x, double y, double angle, double length, boolean isRadians, Color fill) {
		this(x, y, StrictMath.cos((isRadians ? angle : (angle = StrictMath.toRadians(angle)))) * length,
			StrictMath.sin(angle) * length, fill);
	}


	public Arrow(double x, double y, double angle, double length, boolean isRadians) {
		this(x, y, angle, length, isRadians, Color.RED);
	}
}
