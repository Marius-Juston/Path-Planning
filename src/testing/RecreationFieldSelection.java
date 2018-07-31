package testing;

import drawer.content.NotificationArrow;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class RecreationFieldSelection implements Initializable {

	public AnchorPane pointPlacement;
	public ImageView fieldImage;

	private Polygon polygon = new Polygon();
	private Color gray = Color.gray(.5, .6);
	private Color blue = Color.rgb(0, 0, 255, .5);
	private boolean now = true;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		fieldImage.setFitHeight(1000);
		fieldImage.setFitWidth(1000);

		polygon.setFill(Color.TRANSPARENT);
		polygon.setStroke(Color.RED);
		polygon.setStrokeWidth(1);

		pointPlacement.getChildren().addAll(new NotificationArrow(150, 150, "Hello is Marius, What is your? I love you is it mutual?"));
		pointPlacement.getChildren().addAll(new NotificationArrow(150, 350, "Hello"));


		//		pointPlacement.getChildren().add(new PositionPoint(100, 100));

//		pointPlacement.getChildren().add(polygon);
	}

	@FXML
	public void handleMouseClicked(MouseEvent mouseEvent) {
		outlineField(mouseEvent);
	}

	@FXML
	private void outlineField(MouseEvent mouseEvent) {

		polygon.getPoints().addAll(mouseEvent.getX(), mouseEvent.getY());

//		If the polygon has 8 defining points
		if (polygon.getPoints().size() / 2 >= 8 && now) {
			now = false;

			Rectangle rectangle = new Rectangle(fieldImage.getFitWidth(), fieldImage.getFitHeight());
			rectangle.setFill(Color.color(1, 0, 0, .3));

			Path subtract = (Path) Polygon.subtract(rectangle, polygon);
			subtract.setFill(gray);
			subtract.setStroke(gray);
			subtract.setStrokeWidth(1);

			polygon.setFill(blue);

			pointPlacement.getChildren().addAll(subtract);

		}
	}
}
