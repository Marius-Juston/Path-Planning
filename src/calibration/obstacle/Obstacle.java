package calibration.obstacle;

import javafx.scene.shape.Polygon;


public class Obstacle extends AbstractObstacle {

  //	TODO fix the problems hat the padding and all cause the shapes to be translated wrongly
  public Obstacle(ThreatLevel threatLevel, Polygon definingShape) {
    super(threatLevel, new Polygon(definingShape.getPoints().stream().mapToDouble(value -> value).toArray()));

    this.getDefiningShape().setFill(definingShape.getFill());
    this.getDefiningShape().setStroke(definingShape.getStroke());
    this.getDefiningShape().setStrokeWidth(definingShape.getStrokeWidth());

    getChildren().add(definingShape);
  }
}
