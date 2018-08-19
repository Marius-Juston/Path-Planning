package calibration.obstacle;

import javafx.scene.shape.Path;

public class FieldBorder extends AbstractObstacle {

  public FieldBorder(Path definingShape) {
    super(ThreatLevel.ERROR, new Path(definingShape.getElements()));

    getDefiningShape().setFill(definingShape.getFill());
    getDefiningShape().setStroke(definingShape.getStroke());
    getDefiningShape().setStrokeWidth(definingShape.getStrokeWidth());

    getChildren().add(definingShape);
  }
}
