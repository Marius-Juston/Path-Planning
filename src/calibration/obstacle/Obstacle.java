package calibration.obstacle;

import javafx.scene.shape.Polygon;


public class Obstacle extends AbstractObstacle {

  //	TODO fix the problems hat the padding and all cause the shapes to be translated wrongly
  public Obstacle(ThreatLevel threatLevel, Polygon definingShape) {
    super(threatLevel, new Polygon(definingShape.getPoints().stream().mapToDouble(value -> value).toArray()));

    getDefiningShape().setFill(definingShape.getFill());
    getDefiningShape().setStroke(definingShape.getStroke());
    getDefiningShape().setStrokeWidth(definingShape.getStrokeWidth());

    getChildren().add(definingShape);
  }
}
