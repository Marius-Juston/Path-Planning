package drawer.curves.figures;

import drawer.curves.PointGroup;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.paint.Color;

public class OriginPoint extends PointGroup {

  private static int index;

  public OriginPoint(double centerX, double centerY) {
    super(centerX, centerY);

    getPositionPoint().setFill(Color.GREEN);

    setName(String.format("Origin %d", index++));

//		degreesProperty().addListener((observable, oldValue, newValue) -> System.out.println(newValue));
  }


  @Override
  public SimpleDoubleProperty degreesProperty() {
    return super.degreesProperty();
  }
}
