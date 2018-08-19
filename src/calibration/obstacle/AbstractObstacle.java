package calibration.obstacle;

import javafx.scene.Group;
import javafx.scene.shape.Shape;

public abstract class AbstractObstacle extends Group {

  private final ThreatLevel threatLevel;
  private final Shape definingShape;

  AbstractObstacle(ThreatLevel threatLevel, Shape definingShape) {
    this.threatLevel = threatLevel;
    this.definingShape = definingShape;

    definingShape.setFill(threatLevel.getDisplayColor());
    definingShape.setStroke(threatLevel.getDisplayColor());
    definingShape.setStrokeWidth(1);
  }

  public ThreatLevel getThreatLevel() {
    return threatLevel;
  }

  public Shape getDefiningShape() {
    return definingShape;
  }
}
