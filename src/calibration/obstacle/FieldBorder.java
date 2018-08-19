package calibration.obstacle;

import javafx.scene.shape.Path;

public class FieldBorder extends AbstractObstacle {

  public FieldBorder(Path definingShape) {
    super(ThreatLevel.ERROR, new Path(definingShape.getElements()));

    getChildren().add(definingShape);
  }
}
