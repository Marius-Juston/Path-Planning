package calibration.obstacle;

import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;

/**
 * The border of the field.
 */
public class FieldBorder extends AbstractObstacle {

  /**
   * Creates a field border given its Path. Recreates the given path to a new Path while just keeping its elements due
   * to javaFX referencing and anchoring problems which offset the shapes after they have been assigned to a Pane
   *
   * @param definingShape Path of the field border (usually created using the Path.subtract method)
   * @see Path#subtract(Shape, Shape)
   */
  public FieldBorder(Path definingShape) {
    super(ThreatLevel.ERROR, new Path(definingShape.getElements()));

    getChildren().add(definingShape);
  }
}
