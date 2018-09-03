package calibration;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.Polygon;
import javafx.stage.Window;

public enum Helper {
  ;

  public static final String PIXELS = "pixels";

  Helper() {
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
    ((Node) actionEvent.getSource()).getScene().setRoot(rootToSetTo);
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

  public static String convertPolygonToString(Polygon polygon) {

    StringBuilder stringBuilder = new StringBuilder();

    for (double point : polygon.getPoints()) {
      stringBuilder.append(point);
      stringBuilder.append(' ');
    }

    return stringBuilder.toString();
  }


  public static Polygon loadPolygonFromString(String polygon) {
    double[] doubles = convertStringDouble(polygon);
    return new Polygon(doubles);
  }

  public static String convertPathToString(Path path) {
    StringBuilder stringBuilder = new StringBuilder();

    for (PathElement pathElement : path.getElements()) {
      if (pathElement instanceof MoveTo) {
        MoveTo moveTo = (MoveTo) pathElement;
        stringBuilder.append('M');
        stringBuilder.append(' ');

        addXY(stringBuilder, moveTo.getX(), moveTo.getY());
      } else if (pathElement instanceof LineTo) {
        stringBuilder.append('L');
        stringBuilder.append(' ');

        LineTo lineTo = (LineTo) pathElement;

        addXY(stringBuilder, lineTo.getX(), lineTo.getY());
      } else {
        stringBuilder.append('C');
        stringBuilder.append(' ');
      }
    }

    return stringBuilder.toString();
  }

  public static Path convertStringToPath(String path) {
    List<PathElement> pathElements = new LinkedList<>();

    String[] points = path.split("\\s");

    for (int i = 0; i < points.length; i += 3) {

      String type = points[i];

      PathElement pathElement;
      if (type.equals("C")) {
        pathElement = new ClosePath();
        i -= 2; //Because there is no extra information go back 2 cases to move just one

      } else {
        double x = Double.parseDouble(points[i + 1]);
        double y = Double.parseDouble(points[i + 2]);
        if (type.equals("M")) {
          pathElement = new MoveTo(x, y);
        } else /*if (type.equals("L")) */ {
          pathElement = new LineTo(x, y);
        }
      }

      pathElements.add(pathElement);
    }

    return new Path(pathElements);
  }

  public static void addXY(StringBuilder stringBuilder, double x, double y) {
    stringBuilder.append(x);
    stringBuilder.append(' ');
    stringBuilder.append(y);
    stringBuilder.append(' ');
  }

  public static double[] convertStringDouble(String doubles) {
    return Arrays.stream(doubles.split("\\s")).mapToDouble(Double::parseDouble).toArray();
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