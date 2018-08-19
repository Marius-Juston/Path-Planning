package drawer.curves.figures;

import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class NotificationArrow extends Group {

  private final Group messageTextBox = new Group();

  public NotificationArrow(double x, double y, String message) {
    Polygon polygon = new Polygon();

    Circle circle = new Circle(x, y - 20, 8);

    // find tangents
    double dx = circle.getCenterX() - x;
    double dy = circle.getCenterY() - y;
    double dd = Math.sqrt((dx * dx) + (dy * dy));
    double a = StrictMath.asin(circle.getRadius() / dd);
    double b = StrictMath.atan2(dy, dx);

    polygon.getPoints().addAll(
        x, y);

    double t = b - a;
    polygon.getPoints().addAll((circle.getRadius() * StrictMath.sin(t)) + circle.getCenterX(),
        (circle.getRadius() * -StrictMath.cos(t)) + circle.getCenterY());
    t = b + a;
    polygon.getPoints().addAll((circle.getRadius() * -StrictMath.sin(t)) + circle.getCenterX(),
        (circle.getRadius() * StrictMath.cos(t)) + circle.getCenterY());

    Shape shape = Shape.union(circle, polygon);
    Circle innerCircle = new Circle(x, y - 20, 3);
    innerCircle.setFill(Color.BLACK);
    Group group = new Group();
    group.getChildren().addAll(shape
        , innerCircle
    );
    shape.setFill(Color.RED);

    getChildren().add(group);

    Text text = new Text(message);
    text.setWrappingWidth(120);
    text.setTextOrigin(VPos.CENTER);
    text.setTextAlignment(TextAlignment.CENTER);

    double arrowWidth = 10;

    text.setX(x - (text.getLayoutBounds().getWidth() / 2.0));
    text.setY(circle.getCenterY() - circle.getRadius() - (text.getLayoutBounds().getHeight() / 2.0)
        - arrowWidth);

    Polygon textPolygon = new Polygon(
        text.getX(),
        text.getY() - (text.getLayoutBounds().getHeight() / 2.0),

        text.getX() + (text.getLayoutBounds().getWidth()),
        text.getY() - (text.getLayoutBounds().getHeight() / 2.0),

        text.getX() + (text.getLayoutBounds().getWidth()),
        text.getY() + (text.getLayoutBounds().getHeight() / 2.0),

        circle.getCenterX() + (arrowWidth / 2.0),
        text.getY() + (text.getLayoutBounds().getHeight() / 2.0),

        circle.getCenterX(),
        circle.getCenterY() - circle.getRadius(),

        circle.getCenterX() - (arrowWidth / 2.0),
        text.getY() + (text.getLayoutBounds().getHeight() / 2.0),

        text.getX(),
        text.getY() + (text.getLayoutBounds().getHeight() / 2.0)

    );

    textPolygon.setFill(Color.WHITE);
    textPolygon.setStroke(Color.BLACK);
    textPolygon.setStrokeWidth(1);

    messageTextBox.getChildren().add(textPolygon);
    messageTextBox.getChildren().add(text);

    getChildren().add(messageTextBox);

    messageTextBox.setVisible(false);

    setOnMouseEntered(this::showMessage);
    setOnMouseExited(this::hideMessage);
  }

  private void hideMessage(MouseEvent mouseEvent) {
    messageTextBox.setVisible(false);
  }

  private void showMessage(MouseEvent mouseEvent) {
    messageTextBox.setVisible(true);
  }
}
