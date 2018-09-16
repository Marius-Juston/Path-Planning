package calibration.obstacle;

import javafx.scene.shape.Polygon;

/**
 * Generic obstacle.
 */
public class Obstacle extends AbstractObstacle {

  //	TODO fix the problems hat the padding and all cause the shapes to be translated wrongly

  /**
   * Creates an obstacle given its defining shape (Polygon). Recreates the given polygon to a new Polygon while just
   * keeping its points due to javaFX referencing and anchoring problems which offset the shapes after they have been
   * assigned to a Pane
   *
   * @param definingShape Path of the obstacle
   */
  public Obstacle(ThreatLevel threatLevel, Polygon definingShape) {
    super(threatLevel, new Polygon(definingShape.getPoints().stream().mapToDouble(value -> value).toArray()));

    getChildren().add(definingShape);
  }
}
