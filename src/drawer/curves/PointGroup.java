package drawer.curves;

import drawer.curves.figures.ObservedDirectionalArrow;
import drawer.curves.figures.PositionPoint;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

public class PointGroup extends Group {


  private final Text name = new Text();
  //	private final SimpleStringProperty name;
  private final SimpleBooleanProperty selected;
  private final ObservedDirectionalArrow observedDirectionalArrow;
  private final Circle arrowRadius;
  private final PositionPoint positionPoint;
  private final SimpleDoubleProperty degrees = new SimpleDoubleProperty(1.0);
  private boolean beingDragged;

  protected PointGroup(double centerX, double centerY) {
    positionPoint = new PositionPoint(centerX, centerY);

    name.textOriginProperty().setValue(VPos.CENTER);
    name.xProperty().bind(positionPoint.centerXProperty().subtract(15));
    name.yProperty().bind(positionPoint.centerYProperty().subtract(15));

    double length = 50;

    observedDirectionalArrow = new ObservedDirectionalArrow(positionPoint, 0, length, false,
        Color.RED);
    observedDirectionalArrow.setVisible(false);
    observedDirectionalArrow.setDisable(true);

    arrowRadius = new Circle(length, Color.TRANSPARENT);
    arrowRadius.setStroke(Color.GREEN);
    arrowRadius.setDisable(true);
    arrowRadius.setVisible(false);

    arrowRadius.centerXProperty().bindBidirectional(positionPoint.centerXProperty());
    arrowRadius.centerYProperty().bindBidirectional(positionPoint.centerYProperty());

    positionPoint.setOnMouseClicked(this::handleMouseClicked);
    positionPoint.setOnMouseDragged(event -> {
      positionPoint.movePoint(event);
      beingDragged = true;
    });

    positionPoint.getStyleClass().add("position-point");

    selected = new SimpleBooleanProperty(false);
    selected.addListener((observable, oldValue, newValue) -> {
      if (newValue) {
        enable();
      } else {
        disable();
      }
    });

    DoubleBinding angleDegrees = Bindings
        .createDoubleBinding(
            () -> boundDegrees(StrictMath.toDegrees(observedDirectionalArrow.getAngle())),
            observedDirectionalArrow.angleProperty());
    degreesProperty().bind(angleDegrees);

//		degrees.addListener((observable, oldValue, newValue) -> System.out.println(newValue));
//		degrees.set(10);

    getChildren().addAll(name, arrowRadius, observedDirectionalArrow, positionPoint);
  }

  static double boundDegrees(double degrees) {
    if (degrees > 180) {
      return -360 + degrees;
    } else if (degrees < -180) {
      return 360 + degrees;
    }
    return degrees;
  }

  public boolean isSelected() {
    return selected.get();
  }

  public void setSelected(boolean selected) {
    this.selected.set(selected);
  }

  public SimpleBooleanProperty selectedProperty() {
    return selected;
  }

  public ObservedDirectionalArrow getObservedDirectionalArrow() {
    return observedDirectionalArrow;
  }

  public Circle getArrowRadius() {
    return arrowRadius;
  }

  public String getName() {
    return name.getText();
  }

  public void setName(String name) {
    this.name.setText(name);
  }

  public DoubleProperty centerXProperty() {
    return positionPoint.centerXProperty();
  }

  public DoubleProperty centerYProperty() {
    return positionPoint.centerYProperty();
  }

  public StringProperty nameProperty() {
    return name.textProperty();
  }

  private void setAngle(double value) {
    observedDirectionalArrow.angleProperty().set(value);
  }

  public double getDegrees() {
    return degrees.get();
  }

  private void setDegrees(double value) {
    observedDirectionalArrow.angleProperty().set(Math.toRadians(value));
  }

  public SimpleDoubleProperty degreesProperty() {
    return degrees;
  }

  public PositionPoint getPositionPoint() {
    return positionPoint;
  }

  public void handleMouseClicked(MouseEvent mouseEvent) {

    if (!beingDragged) {
      if (mouseEvent.getButton() == MouseButton.PRIMARY) {
        selected.set(!selected.get());
      }
    }

    beingDragged = false;
  }

  private void disable() {
    observedDirectionalArrow.setVisible(false);
    observedDirectionalArrow.setDisable(true);
    arrowRadius.setVisible(false);
    arrowRadius.setDisable(true);
  }

  private void enable() {
    observedDirectionalArrow.setVisible(true);
    observedDirectionalArrow.setDisable(false);
    arrowRadius.setVisible(true);
    arrowRadius.setDisable(false);
  }

  public SimpleDoubleProperty angleProperty() {
    return observedDirectionalArrow.angleProperty();
  }

}
