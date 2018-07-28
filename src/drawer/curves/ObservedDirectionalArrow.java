package drawer.curves;

import calibration.Helper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import org.waltonrobotics.controller.Pose;

public class ObservedDirectionalArrow extends Polygon {

	private final SimpleDoubleProperty x;
	private final SimpleDoubleProperty y;
	private final SimpleDoubleProperty angle; //In radians
	private final double length;

	private double dx;
	private double dy;

	public ObservedDirectionalArrow(PositionPoint xy, double angle, double length, boolean isRadians,
		double width, boolean length_includes_head,
		double head_width, double head_length, String shape, double overhang,
		boolean head_starts_at_zero, Color fill) {

		dx = StrictMath.cos((isRadians ? angle : (angle = StrictMath.toRadians(angle)))) * length;
		dy = StrictMath.sin(angle) * length;
		this.length = length;

		if (head_width == -1) {
			head_width = 3 * width;
		}
		if (head_length == -1) {
			head_length = 1.5 * head_width;
		}

		double distance = Math.hypot(dx, dy);

		if (length_includes_head) {
			length = distance;
		} else {
			length = distance + head_length;
		}

		{
			x = new SimpleDoubleProperty(xy.getCenterX());
			y = new SimpleDoubleProperty(xy.getCenterY());
			this.angle = new SimpleDoubleProperty(angle);

			xy.centerXProperty().bindBidirectional(x);

			xy.centerXProperty().addListener((observable, oldValue, newValue) -> {
				followPoint(oldValue, newValue, true);
//
//				for (int i = 0; i < getPoints().size(); i += 2) {
//					getPoints().set(i, (getPoints().get(i) - oldValue.doubleValue()) + newValue.doubleValue());
//				}
			});

			xy.centerYProperty().bindBidirectional(y);

			xy.centerYProperty().addListener((observable, oldValue, newValue) -> {
				followPoint(oldValue, newValue, false);

//				for (int i = 1; i < getPoints().size(); i += 2) {
//					getPoints().set(i, (getPoints().get(i) - oldValue.doubleValue()) + newValue.doubleValue());
//				}
			});

			this.angle.addListener(this::rotateArrow);

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
//		double[][] vertices = new double[coords.length][2];

//		Does the dot product of coords and M
		double[][] vertices = Helper.dotProduct(coords, M);

//		for (int i = 0; i < coords.length; i++) {
//			for (int z = 0; z < M[0].length; z++) {
//				double sum = 0;
//				for (int j = 0; j < M.length; j++) {
//					sum += (coords[i][j] * M[z][j]);
//				}
//
//				vertices[i][z] = sum;
//			}
//		}

		translate(vertices, xy.getCenterX(), xy.getCenterY(), dx, dy);
//
//		for (int i = 0; i < vertices.length; i++) {
//
//			vertices[i][0] += (xy.getCenterX() + dx);
//			vertices[i][1] += (xy.getCenterY() - dy)/*Due to graphics reason - instead of +*/;
//		}

		setArrowPoints(vertices);

		setFill(fill);
		setOnMouseDragged(this::movePoint);
	}

	public ObservedDirectionalArrow(PositionPoint positionPoint, double angle, double length, boolean isRadians,
		Color fill) {
		this(positionPoint, angle, length, isRadians, 4, true, -1, 6, "full", 0, false, fill);
	}

	public ObservedDirectionalArrow(PositionPoint positionPoint, double angle, double length, boolean isRadians) {
		this(positionPoint, angle, length, isRadians, Color.RED);
	}

	public ObservedDirectionalArrow(Pose centerPose, double length, Color color) {
		this(new PositionPoint(centerPose), -centerPose.getAngle(), length, true, color);
	}

	public void followPoint(Number oldValue, Number newValue, boolean isX) {
		for (int i = isX ? 0 : 1; i < getPoints().size(); i += 2) {
			getPoints().set(i, (getPoints().get(i) - oldValue.doubleValue()) + newValue.doubleValue());
		}
	}

	private void rotateArrow(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
		double rotation = oldValue.doubleValue() - newValue.doubleValue();

		double[][] points = new double[8][2];

		for (int i = 0; i < getPoints().size(); i++) {
			int row = i / points[0].length;
			int column = i - (row * points[0].length);

//						Gets the points translated so that 0,0 is the origin
			points[row][column] = getPoints().get(i) + ((column == 0) ? -x.get() - dx : -y.get() + dy);
		}

//					double[][] rotatedVertices = new double[8][2];
		double[][] rotationMatrix = {{StrictMath.cos(rotation), -StrictMath.sin(rotation)},
			{StrictMath.sin(rotation), StrictMath.cos(rotation)}};

		double[][] rotatedVertices = Helper.dotProduct(points, rotationMatrix);
//					for (int i = 0; i < points.length; i++) {
//						for (int z = 0; z < rotationMatrix[0].length; z++) {
//							double sum = 0;
//							for (int j = 0; j < rotationMatrix.length; j++) {
//								sum += (points[i][j] * rotationMatrix[z][j]);
//							}
//
//							rotatedVertices[i][z] = sum;
//						}
//					}

//					Need to recalculate the dx and dys because of the new angle
		dx = StrictMath.cos(newValue.doubleValue()) * length;
		dy = StrictMath.sin(newValue.doubleValue()) * length;

		translate(rotatedVertices, x.get(), y.get(), dx, dy);
//
//					for (int i = 0; i < rotatedVertices.length; i++) {
//
//						rotatedVertices[i][0] += (xy.getCenterX() + newDx);
//						rotatedVertices[i][1] += (xy.getCenterY() - newDy)/*Due to graphics reason - instead of +*/;
//					}

		setArrowPoints(rotatedVertices);
	}

//	public ObservedArrow(double x, double y, double dx, double dy) {
//		this(x, y, dx, dy, Color.RED);
//	}

//	public ObservedArrow(double x, double y, double dx, double dy, Color fill) {
//		this(x, y, dx, dy, 4, true, -1, 6, "full", 0, false, fill);
//	}

	public void translate(double[][] vertices, double x, double y, double dx, double dy) {
		for (int i = 0; i < vertices.length; i++) {
			vertices[i][0] += (x + dx);
			vertices[i][1] += (y - dy)/*Due to graphics reason - instead of +*/;
		}
	}

	public void setArrowPoints(double[][] vertices) {
		getPoints().clear();

		for (double[] vert : vertices) {
			for (double aVert : vert) {
				getPoints().add(aVert);
			}
		}
	}

	public void movePoint(MouseEvent mouseEvent) {

		if (mouseEvent.getButton() == MouseButton.PRIMARY) {
//			x.set(mouseEvent.getX());
//			y.set(mouseEvent.getY());

//			setAngle(Math.atan2(mouseEvent.getX() , mouseEvent.getY() ));

			double x = mouseEvent.getX() - this.x.get();
			double y = -(mouseEvent.getY() - this.y.get());

			double angle = Math.atan2(y, x);

			setAngle(angle);
		} else {

		}
	}

	public double getAngle() {
		return angle.get();
	}

	/**
	 * Angles in radians
	 */
	public void setAngle(double angle) {
		this.angle.set(angle);
	}

	public SimpleDoubleProperty angleProperty() {
		return angle;
	}
}
