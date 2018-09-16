package calibration.obstacle;

import javafx.scene.Group;
import javafx.scene.shape.Shape;

/**
 * Abstract obstacle that could define a field.
 */
public abstract class AbstractObstacle extends Group {

  /**
   * Threat level of the obstacle
   */
  private final ThreatLevel threatLevel;
  /**
   * Shape that defines the obstacle
   */
  private final Shape definingShape;

  /**
   * Creates an obstacle with a threat level (how dangerous it is) and its shape
   *
   * @param threatLevel the threat level of the robot
   * @param definingShape the shape of the obstacle
   * @see Obstacle
   * @see FieldBorder
   */
  AbstractObstacle(ThreatLevel threatLevel, Shape definingShape) {
    this.threatLevel = threatLevel;
    this.definingShape = definingShape;

  }

  public ThreatLevel getThreatLevel() {
    return threatLevel;
  }

  public Shape getDefiningShape() {
    return definingShape;
  }
}
