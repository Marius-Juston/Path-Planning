package calibration;

import static calibration.Helper.PIXELS;
import static calibration.Helper.getImage;
import static calibration.Helper.getWindow;

import calibration.Helper.Unit;
import calibration.obstacle.FieldBorder;
import calibration.obstacle.Obstacle;
import calibration.obstacle.ThreatLevel;
import drawer.PointPlacer;
import drawer.optimizer.Mesher;
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
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;


public class Controller implements Initializable {

  /**
   * TODO add more precise scaling, make a zoom functionality, use arrow keys to move points of measurement
   */

  private static final String defaultDistance = "Select 2 Points";
  private static final FileChooser fileChooser = new FileChooser();
  private static final FileChooser saver = new FileChooser();
  private static final FileChooser loader = new FileChooser();
  private static File startLoadLocation;
  private static File startOpenLocation;
  private static File startSaveLocation;


  static {
    fileChooser.getExtensionFilters()
        .add(new ExtensionFilter("Images", "*.png", "*.jpg", "*.field"));
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
  private final MenuItem confirmFieldBoarder = new MenuItem("Set as Field boarder");
  private final MenuItem confirmNormalObstacle = new MenuItem("Confirm Normal Obstacle");
  private final MenuItem confirmDangerousObstacle = new MenuItem("Confirm Dangerous Obstacle");
  private final MenuItem cancelObstacle = new MenuItem("Cancel Obstacle");
  private final ContextMenu confirmFishedObstacle = new ContextMenu(cancelObstacle, confirmFieldBoarder,
      confirmNormalObstacle, confirmDangerousObstacle);
  private final Polygon polygon = new Polygon();
  private final Button outlineToggleButton = new Button("Outline field");
  @FXML
  private ImageView fieldImage;
  @FXML
  private AnchorPane pointPlacement;
  @FXML
  private TextField distanceViewer;
  @FXML
  private HBox infoPane;
  private boolean firstConversion = true;
  private Selection scaleSelection = Selection.NO_SELECTION;
  private boolean calibrating = true;

  public Controller() {
    confirmFieldBoarder.setOnAction(event -> createFieldBorder());
    confirmNormalObstacle.setOnAction(event -> createNormalObstacle());
    confirmDangerousObstacle.setOnAction(event -> createDangerousObstacle());
    cancelObstacle.setOnAction(event -> polygon.getPoints().clear());
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
    polygon.getPoints().clear();
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
  private void handleMouseClicked(MouseEvent mouseEvent) throws IOException {
    if (calibrating) {
      selectPoint(mouseEvent);
    } else {
      outlineField(mouseEvent);

      if (mouseEvent.getButton() == MouseButton.SECONDARY) {
        if (polygon.getPoints().size() < 3) {
          setDisableObstacleConfirmationMenuItems(true);
        } else {
          setDisableObstacleConfirmationMenuItems(false);
        }
        confirmFishedObstacle.show(fieldImage, mouseEvent.getScreenX(), mouseEvent.getScreenY());
      } else {
        confirmFishedObstacle.hide();
      }

    }
  }

  private void setDisableObstacleConfirmationMenuItems(boolean isDisabled) {

    confirmFieldBoarder.setDisable(isDisabled);
    confirmNormalObstacle.setDisable(isDisabled);
    confirmDangerousObstacle.setDisable(isDisabled);
  }

  @FXML
  private void outlineField(MouseEvent mouseEvent) {

    if (!pointPlacement.getChildren().contains(polygon)) {
      pointPlacement.getChildren().add(polygon);
    }

//		PositionPoint positionPoint = new PositionPoint(mouseEvent.getX(), mouseEvent.getY());

    polygon.getPoints().addAll(mouseEvent.getX(), mouseEvent.getY());

    ////		Collections.addAll(array, mouseEvent.getX(), mouseEvent.getY());
//
////		if (polygon.getPoints().size() / 2 >= 8 && now) {
//		if (polygon.getPoints().size() / 2 >= 8 && now) {
//			now = false;
//
////			TODO fix this problem
//			Rectangle rectangle = new Rectangle(Field.getInstance().image.getWidth(), Field.getInstance().image.getHeight());
//			rectangle.setFill(Color.color(1, 0, 0, .3));
//
//			Polygon polygon = new Polygon(this.polygon.getPoints().stream().mapToDouble(value -> value).toArray());
//			Path subtract = (Path) Polygon.subtract(rectangle, polygon);
//			subtract.setFill(ThreatLevel.ERROR.getDisplayColor());
//			subtract.setStroke(ThreatLevel.ERROR.getDisplayColor());
//			subtract.setStrokeWidth(1);
//
//			polygon.setFill(ThreatLevel.WARNING.getDisplayColor());
//
//			pointPlacement.getChildren().addAll(subtract);
//			pointPlacement.getChildren().remove(this.polygon);
//
//			Field.getInstance().addObstacle(ObstacleType.FIELD_BORDER, new Obstacle(ThreatLevel.ERROR, subtract));
//		}
  }

  private void createFieldBorder() {
    //			TODO fix this problem
    Rectangle rectangle = new Rectangle(Field.getInstance().image.getWidth(), Field.getInstance().image.getHeight());
    rectangle.setFill(Color.color(1, 0, 0, 0.3));

    Polygon polygon = new Polygon(
        this.polygon.getPoints().stream().mapToDouble(value -> value).toArray());

    Path subtract = (Path) Shape.subtract(rectangle, polygon);
    subtract.setFill(ThreatLevel.ERROR.getDisplayColor());
    subtract.setStroke(ThreatLevel.ERROR.getDisplayColor());
    subtract.setStrokeWidth(1);

//		subtract.setTranslateX(-scrollPane.getLayoutX());
//		subtract.setTranslateY(-scrollPane.getLayoutY());

//		System.out.println(scrollPane.getLayoutX());
//		System.out.println(scrollPane.getLayoutY());

    polygon.setFill(ThreatLevel.WARNING.getDisplayColor());
//
//		subtract.setOnMouseClicked(event -> {
//			try {
//				handleMouseClicked(event);
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		});

    Field.getInstance().addObstacle(new FieldBorder(subtract));

    this.polygon.getPoints().clear();
  }

  private void createObstacle(ThreatLevel threatLevel) {
    Polygon polygon = new Polygon(
        this.polygon.getPoints().stream().mapToDouble(value -> value).toArray());
    polygon.setFill(threatLevel.getDisplayColor());
    polygon.setStroke(threatLevel.getDisplayColor());
    polygon.setStrokeWidth(1);

    Field.getInstance().addObstacle(new Obstacle(threatLevel, polygon));

    this.polygon.getPoints().clear();
  }

  private void createNormalObstacle() {
    createObstacle(ThreatLevel.WARNING);
  }

  private void createDangerousObstacle() {
    createObstacle(ThreatLevel.ERROR);
  }


  private void setConversion() throws IOException {
    double pixelDistance = line.getLineLength();
    Unit actualDistance = askActualDistance(pixelDistance);

    if (actualDistance != null) {

      Field.getInstance().SCALE.set(actualDistance.getValue() / pixelDistance);
      Field.getInstance().UNIT.set(actualDistance.getUnit());

      addExtraData();
      convertedInfo.setText(String.format("%.3f %s", actualDistance.getValue(), Field.getInstance().UNIT.get()));
    }
  }

  private void addExtraData() {
    if (firstConversion) {
      firstConversion = false;

      infoPane.getChildren().add(1, new Text("=>"));
      infoPane.getChildren().add(2, convertedInfo);

      if (!infoPane.getChildren().contains(outlineToggleButton)) {
        infoPane.getChildren().add(3, outlineToggleButton);
      }

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
        Field.getInstance().imageFile = image;
        fieldImage.setImage(Field.getInstance().image = getImage(image));
        distanceViewer.setText(defaultDistance);

        if (!infoPane.getChildren().contains(outlineToggleButton)) {
          infoPane.getChildren().add(1, outlineToggleButton);
        }
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

    outlineToggleButton.setOnAction(event -> {
      calibrating = !calibrating;
      outlineToggleButton.setText(calibrating ? "Outline field" : "Calibrate Field");
      cleanUp();
    });

    //		pointPlacement.setOnContextMenuRequested(
//			event -> {
//
//			});

    polygon.setFill(Color.TRANSPARENT);
    polygon.setStroke(Color.RED);
    polygon.setStrokeWidth(1);

    polygon.setOnMouseClicked(event -> {
      try {
        handleMouseClicked(event);
      } catch (IOException e) {
        e.printStackTrace();
      }
    });

    Field.getInstance().obstacleGroup.setOnMouseClicked(event -> {
      try {
        handleMouseClicked(event);
      } catch (IOException e) {
        e.printStackTrace();
      }
    });

    pointPlacement.getChildren().add(polygon);
    pointPlacement.getChildren().add(Field.getInstance().obstacleGroup);

    if (Field.getInstance().imageFile != null) {
      load(Field.getInstance().image);
    }

    pointPlacement.getChildren().add(Mesher.getGroup());
  }

  @FXML
  private void saveData(ActionEvent actionEvent) throws IOException {
    if (fieldImage != null) {

      saver.setInitialDirectory(startSaveLocation);

      File saveFile = saver
          .showSaveDialog(((MenuItem) actionEvent.getSource()).getParentPopup().getOwnerWindow());

      if (saveFile != null) {
        startSaveLocation = saveFile.getParentFile();

        Field.getInstance().saveData(saveFile);
      }
    }
  }

  @FXML
  private void loadData(ActionEvent actionEvent) throws IOException {
    loader.setInitialDirectory(startLoadLocation);

    File loadFile = loader
        .showOpenDialog(((MenuItem) actionEvent.getSource()).getParentPopup().getOwnerWindow());

    if (loadFile != null) {
      startLoadLocation = loadFile.getParentFile();

      load(loadFile);
    }
  }

  private void load(File loadFile) throws IOException {
    load(Field.getInstance().loadData(loadFile));
  }

  private void load(Image fieldImage) {
    this.fieldImage.setImage(fieldImage);

//		TODO add here?

    addExtraData();
    cleanUp();
  }

  public void optimizeImage(ActionEvent event) {
    Image image = Field.getInstance().image;
    WritableImage writableImage = new WritableImage(image.getPixelReader(), (int) image.getWidth(),
        (int) image.getHeight());
    System.out.println(image.getWidth() + "\t" + image.getHeight());
    System.out.println(writableImage.getWidth() + "\t" + writableImage.getHeight());

    PixelWriter pixelWriter = writableImage.getPixelWriter();
    PixelReader pixelReader = writableImage.getPixelReader();

    for (int i = 0; i < writableImage.getHeight(); i++) {
      for (int j = 0; j < writableImage.getWidth(); j++) {
        Color c = pixelReader.getColor(j, i);

        if (c.getOpacity() < 1) {
          pixelWriter.setColor(j, i, Color.WHITE);
        }
        if (c.getRed() > .5|| c.getGreen() > .5|| c.getBlue() > .5) {
          pixelWriter.setColor(j, i, Color.BLACK);
        }
      }
    }

    Field.getInstance().image = writableImage;
    fieldImage.setImage(writableImage);

  }

  public enum Selection {
    NO_SELECTION, ONE_POINT, TWO_POINT
  }

  static class SelectionPoint extends Circle {

    final Point2D point2D;

    SelectionPoint(double centerX, double centerY) {
      super(centerX, centerY, 4, Color.BLUE);

      point2D = new Point2D(centerX, centerY);
    }

    final Point2D getPoint2D() {
      return point2D;
    }
  }

  class SelectionLine extends Line {

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
        convertedInfo
            .setText(
                String.format("%.3f %s", Field.getInstance().SCALE.get() * distance, Field.getInstance().UNIT.get()));
      }

    }

    final double getLineLength() {
      return p1.distance(p2);
    }
  }
}
