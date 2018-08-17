package calibration.obstacle;

import javafx.scene.shape.Path;

public class FieldBorder extends AbstractObstacle {

  public FieldBorder(Path definingShape) {
    super(ThreatLevel.ERROR, new Path(definingShape.getElements()));

    this.getDefiningShape().setFill(definingShape.getFill());
    this.getDefiningShape().setStroke(definingShape.getStroke());
    this.getDefiningShape().setStrokeWidth(definingShape.getStrokeWidth());
  }
}
