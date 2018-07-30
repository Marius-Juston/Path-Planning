package calibration;

import static calibration.Helper.PIXELS;
import static calibration.Helper.getImage;
import static calibration.Helper.getWindow;

import calibration.Helper.Unit;
import drawer.PointPlacer;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;

public class Controller implements Initializable {


	private static final String defaultDistance = "Select 2 Points";
	private static final FileChooser fileChooser = new FileChooser();
	private static final FileChooser saver = new FileChooser();
	private static final FileChooser loader = new FileChooser();
	private static File startLoadLocation;
	private static File startOpenLocation;
	private static File startSaveLocation;


	static {
		fileChooser.getExtensionFilters().add(new ExtensionFilter("Images", "*.png", "*.jpg", "*.field"));
		fileChooser.setTitle("Select Field Image");

		saver.getExtensionFilters().add(new ExtensionFilter("Field data", "*.field"));
		saver.setTitle("Save information");
		loader.getExtensionFilters().add(new ExtensionFilter("Field data", "*.field"));
		loader.setTitle("Load information");

		startOpenLocation = fileChooser.getInitialDirectory();
		startSaveLocation = saver.getInitialDirectory();
		startLoadLocation = saver.getInitialDirectory();
	}

	private final TextField convertedInfo = new TextField();
	private final SelectionPoint[] scaleSelectionPoints = new SelectionPoint[2];
	private final Button rescale = new Button("Rescale");
	private final SelectionLine line = new SelectionLine();
	private final Button moveToPointPlacement = new Button("Place curves");
	@FXML
	public ImageView fieldImage;
	@FXML
	public AnchorPane pointPlacement;
	@FXML
	public TextField distanceViewer;
	@FXML
	public HBox infoPane;
	private boolean firstConversion = true;
	private Selection scaleSelection = Selection.NO_SELECTION;

	private boolean calibrating = true;
	private Polygon polygon = new Polygon();
	private boolean now = true;

	public Controller() {
	}

	private static Unit askActualDistance(double pixelDistance) throws IOException {
		return DistanceConverter.display(pixelDistance);
	}

	public static Parent getRoot() throws IOException {
		return FXMLLoader.load(Controller.class.getResource("fieldSelection.fxml"));
	}

	@FXML
	private void gotToCurvePointPlacement(ActionEvent actionEvent) throws IOException {
		Parent root = PointPlacer.getRoot();
		Helper.setRoot(actionEvent, root);
	}

	private void cleanUp() {
		rescale.setDisable(true);
		line.setVisible(false);
		pointPlacement.getChildren().removeAll(scaleSelectionPoints);
		distanceViewer.setText(defaultDistance);
		convertedInfo.setText(defaultDistance);
		scaleSelection = Selection.NO_SELECTION;
	}

	@FXML
	private void selectPoint(MouseEvent mouseEvent) throws IOException {

		if (scaleSelection == Selection.NO_SELECTION) {
			cleanUp();
		}

		SelectionPoint selectionPoint = new SelectionPoint(mouseEvent.getX(), mouseEvent.getY());
		scaleSelectionPoints[scaleSelection.ordinal()] = selectionPoint;
		pointPlacement.getChildren().add(selectionPoint);

		scaleSelection = Selection.values()[scaleSelection.ordinal() + 1];

		if (scaleSelection == Selection.TWO_POINT) {
			rescale.setDisable(false);

			scaleSelection = Selection.NO_SELECTION;

			line.showLine(scaleSelectionPoints[0], scaleSelectionPoints[1], distanceViewer);

			if (firstConversion) {
				setConversion();
			}
		}
	}

	@FXML
	public void handleMouseClicked(MouseEvent mouseEvent) throws IOException {
		if (calibrating) {
			selectPoint(mouseEvent);
		} else {
			outlineField(mouseEvent);
		}
	}

	@FXML
	private void outlineField(MouseEvent mouseEvent) {
//		PositionPoint positionPoint = new PositionPoint(mouseEvent.getX(), mouseEvent.getY());

		polygon.getPoints().addAll(mouseEvent.getX(), mouseEvent.getY());

//		if (polygon.getPoints().size() / 2 >= 8 && now) {
		if (polygon.getPoints().size() / 2 >= 8 && now) {
			now = false;

//			TODO fix this problem
			Rectangle rectangle = new Rectangle(0, 0, Field.image.getWidth(), Field.image.getHeight());
			rectangle.setFill(Color.color(1, 0, 0, .3));

			Path subtract = (Path) Polygon.subtract(rectangle, polygon);
			subtract.setFill(ThreatLevel.ERROR.getDisplayColor());
			subtract.setStroke(ThreatLevel.ERROR.getDisplayColor());
			subtract.setStrokeWidth(1);
//			subtract.setTranslateY(-37);

			System.out.println(rectangle.getY());
			System.out.println(rectangle.getLayoutY());
			System.out.println(rectangle.getScaleY());
			System.out.println(rectangle.getTranslateY());

			System.out.println();
			System.out.println(polygon.getLayoutY());
			System.out.println(polygon.getScaleY());
			System.out.println(polygon.getTranslateY());

			System.out.println();
			System.out.println(subtract.getLayoutY());
			System.out.println(subtract.getScaleY());
			System.out.println(subtract.getTranslateY());

			System.out.println();
			System.out.println(pointPlacement.getLayoutY());
			System.out.println(pointPlacement.getScaleY());
			System.out.println(pointPlacement.getTranslateY());

			System.out.println();
			final double top = pointPlacement.snappedTopInset();
			final double right = pointPlacement.snappedRightInset();
			final double bottom = pointPlacement.snappedBottomInset();
			final double left = pointPlacement.snappedLeftInset();

			System.out.println(top);
			System.out.println(right);
			System.out.println(bottom);
			System.out.println(left);

			polygon.setFill(ThreatLevel.WARNING.getDisplayColor());

			pointPlacement.getChildren().addAll(subtract);

		}
	}

	private void setConversion() throws IOException {
		double pixelDistance = line.getLineLength();
		Unit actualDistance = askActualDistance(pixelDistance);

		if (actualDistance != null) {

			Field.SCALE.set(actualDistance.getValue() / pixelDistance);
			Field.UNIT.set(actualDistance.getUnit());

			addExtraData();
			convertedInfo.setText(String.format("%.3f %s", actualDistance.getValue(), Field.UNIT.get()));
		}
	}

	private void addExtraData() {
		if (firstConversion) {
			firstConversion = false;

			infoPane.getChildren().add(1, new Text("=>"));
			infoPane.getChildren().add(2, convertedInfo);

			Button button = new Button("Outline field");
			button.setOnAction(event -> {
				calibrating = !calibrating;
				button.setText(calibrating ? "Outline field" : "Calibrate Field");
				cleanUp();
			});

			infoPane.getChildren().add(3, button);

			infoPane.getChildren().add(infoPane.getChildren().size() - 1, rescale);
			infoPane.getChildren().add(infoPane.getChildren().size() - 1, moveToPointPlacement);

		}
	}

	@FXML
	private void chooseImage(ActionEvent actionEvent) throws IOException {
		fileChooser.setInitialDirectory(startOpenLocation);

		Window window;

		try {
			window = getWindow(actionEvent);
		} catch (ClassCastException e) {
			window = ((MenuItem) actionEvent.getSource()).getParentPopup().getOwnerWindow();
		}

		File image = fileChooser.showOpenDialog(window);

		if (image != null) {

			startOpenLocation = image.getParentFile();

			if (Field.isFieldFile(image)) {
				load(image);

			} else {
				Field.imageFile = image;
				fieldImage.setImage(Field.image = getImage(image));
				distanceViewer.setText(defaultDistance);
			}
		}
	}

	@Override
	public final void initialize(URL location, ResourceBundle resources) {
		line.setVisible(false);
		pointPlacement.getChildren().add(line);
		convertedInfo.setEditable(false);
		rescale.setOnAction((actionEvent) -> {
			try {
				setConversion();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

		moveToPointPlacement.setOnAction(actionEvent -> {
			try {
				gotToCurvePointPlacement(actionEvent);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		moveToPointPlacement.setDefaultButton(true);

		polygon.setFill(Color.TRANSPARENT);
		polygon.setStroke(Color.RED);
		polygon.setStrokeWidth(1);
		pointPlacement.getChildren().add(polygon);
	}

	@FXML
	private void saveData(ActionEvent actionEvent) throws IOException {
		if (fieldImage != null) {

			saver.setInitialDirectory(startSaveLocation);

			File saveFile = saver
				.showSaveDialog(((MenuItem) actionEvent.getSource()).getParentPopup().getOwnerWindow());

			if (saveFile != null) {
				startSaveLocation = saveFile.getParentFile();

				Field.saveData(saveFile);
			}
		}
	}

	@FXML
	private void loadData(ActionEvent actionEvent) throws IOException {
		loader.setInitialDirectory(startLoadLocation);

		File loadFile = loader.showOpenDialog(((MenuItem) actionEvent.getSource()).getParentPopup().getOwnerWindow());

		if (loadFile != null) {
			startLoadLocation = loadFile.getParentFile();

			load(loadFile);
		}
	}

	private void load(File loadFile) throws IOException {
		fieldImage.setImage(Field.loadData(loadFile));

		addExtraData();
		cleanUp();
	}


	public enum Selection {
		NO_SELECTION, ONE_POINT, TWO_POINT
	}

	public static class SelectionPoint extends Circle {

		final Point2D point2D;

		SelectionPoint(double centerX, double centerY) {
			super(centerX, centerY, 4, Color.BLUE);

			point2D = new Point2D(centerX, centerY);
		}

		final Point2D getPoint2D() {
			return point2D;
		}
	}

	public class SelectionLine extends Line {

		Point2D p1 = new Point2D(0, 0);
		Point2D p2 = new Point2D(0, 0);

		SelectionLine() {
			setFill(Color.RED);
			setStrokeWidth(2);
		}

		final void showLine(SelectionPoint point, SelectionPoint point2, TextField distanceViewer) {
			setVisible(true);

			p1 = point.getPoint2D();
			p2 = point2.getPoint2D();

			setStartX(point.getCenterX());
			setStartY(point.getCenterY());

			setEndX(point2.getCenterX());
			setEndY(point2.getCenterY());

			double distance = getLineLength();

			distanceViewer.setText(String.format("%.4f %s", distance, PIXELS));

			if (!firstConversion) {
				convertedInfo.setText(String.format("%.3f %s", Field.SCALE.get() * distance, Field.UNIT.get()));
			}

		}

		final double getLineLength() {
			return p1.distance(p2);
		}
	}
}
