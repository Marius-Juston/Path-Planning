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

  /**
   * Gets the ratio between the pixel distance and the actual distance on the Field
   *
   * @param pixelDistance the field distance selected on the field image
   * @return Returns the {@link Unit} with the ratio between pixel distance and its unit
   */
  private static Unit askActualDistance(double pixelDistance) throws IOException {
    return DistanceConverter.display(pixelDistance);
  }

  /**
   * Returns the JavaFX instance of this Controller; loads the fieldSelection.fxml file.
   */
  public static Parent getRoot() throws IOException {
    return FXMLLoader.load(Controller.class.getResource("fieldSelection.fxml"));
  }

  /**
   * Changes the Scene from the {@link Controller} to the {@link PointPlacer} so that you can place the curves.
   */
  @FXML
  private void gotToCurvePointPlacement(ActionEvent actionEvent) throws IOException {
    Parent root = PointPlacer.getRoot();
    Helper.setRoot(actionEvent, root);
  }

  /**
   * Cleans up the {@link #pointPlacement} :
   * <ul>
   * <li>Sets {@link #rescale} to disabled</li>
   * <li>Sets the {@link #line} between the points to invisible</li>
   * <li>Removes all the {@link #scaleSelectionPoints} from the {@link #pointPlacement} </li>
   * <li>Sets the {@link #distanceViewer} to its default value ({@link #defaultDistance})</li>
   * <li>Sets the {@link #convertedInfo} to its default value ({@link #defaultDistance})</li>
   * <li>Sets the {@link #scaleSelection} to its default value ({@link Selection#NO_SELECTION})</li>
   * <li>Clears all the defining points of the obstacle polygon</li>
   * </ul>
   */
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

    //If there are no points that are selected reset the elements just in case
    if (scaleSelection == Selection.NO_SELECTION) {
      cleanUp();
    }

    //Creates a point from the x, y coordinates of the mouse
    SelectionPoint selectionPoint = new SelectionPoint(mouseEvent.getX(), mouseEvent.getY());

    //Adds it to the array of selection point given the current selection
    scaleSelectionPoints[scaleSelection.ordinal()] = selectionPoint;
    // adds the point to the point placement anchor pane so that it can be seen by the user
    pointPlacement.getChildren().add(selectionPoint);

    // updates the number points selected for scaling
    scaleSelection = Selection.values()[scaleSelection.ordinal() + 1];

    // if there are two points
    if (scaleSelection == Selection.TWO_POINT) {
      // make it so that you can press the rescale button
      rescale.setDisable(false);

      // resets the scale selection number so that next time this method passes it cleans up the points
      scaleSelection = Selection.NO_SELECTION;

      /*Shows the line between the selection points at the location between the two points all the while adding its
      distance pixel to the TextField
       */
      line.showLine(scaleSelectionPoints[0], scaleSelectionPoints[1], distanceViewer);

      // If it is the first time the user has created the line and wants to show the scaling dialog
      if (firstConversion) {
        setConversion();
      }
    }
  }

  /**
   * Handles the mouse presses and when the user is calibrating or outlining the field for its obstacles.
   */
  @FXML
  private void handleMouseClicked(MouseEvent mouseEvent) throws IOException {
    // if the user is scaling the field
    if (calibrating) {
      selectPoint(mouseEvent);
    } else {
      // if the user is outlining the field for its borders and its obstacles
      outlineField(mouseEvent);

      // If the user right click
      if (mouseEvent.getButton() == MouseButton.SECONDARY) {

        // if the user has selected 3 points for the polygon to be defined
        if (polygon.getPoints().size() < 3) {
          //disable some menu items because the polygon has less than 3 vertices
          setDisableObstacleConfirmationMenuItems(true);
        } else {
          //enable the menu items because the polygon has more than 3 vertices
          setDisableObstacleConfirmationMenuItems(false);
        }

        //shows the context menu
        confirmFishedObstacle.show(fieldImage, mouseEvent.getScreenX(), mouseEvent.getScreenY());
      } else {
        //hides the context menu
        confirmFishedObstacle.hide();
      }

    }
  }

  /**
   * Disable or enable the menu items dependent on the number of vertices the defining polygon has.
   *
   * @param isDisabled if the menu items should be disabled or not
   */
  private void setDisableObstacleConfirmationMenuItems(boolean isDisabled) {

    confirmFieldBoarder.setDisable(isDisabled);
    confirmNormalObstacle.setDisable(isDisabled);
    confirmDangerousObstacle.setDisable(isDisabled);
  }

  /**
   * Adds the current mouse position to the defining polygon as vertices
   */
  @FXML
  private void outlineField(MouseEvent mouseEvent) {

    // if the polygon is not inside the pointPlacement anchor pane
    if (!pointPlacement.getChildren().contains(polygon)) {
      //Adds it so that it can be visualised
      pointPlacement.getChildren().add(polygon);
    }

    //Adds the mouse position as a polygon vertices location
    polygon.getPoints().addAll(mouseEvent.getX(), mouseEvent.getY());

//		PositionPoint positionPoint = new PositionPoint(mouseEvent.getX(), mouseEvent.getY());

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

  /**
   * Creates the {@link FieldBorder} that defines the field border given the selected polygon. Adds it to the {@link
   * Field} using {@link Field#addObstacle(FieldBorder)}
   */
  private void createFieldBorder() {
    //			TODO fix this problem
    //Creates a rectangle the same size as the image of the field
    Rectangle rectangle = new Rectangle(Field.getInstance().image.getWidth(), Field.getInstance().image.getHeight());
//    rectangle.setFill(Color.color(1, 0, 0, 0.3));

    /*
    Recreates the polygon with the obstacle defining point because of an offset problem with JavaFX that has
    been unresolved
     */
    Polygon polygon = new Polygon(this.polygon.getPoints().stream().mapToDouble(value -> value).toArray());
//    polygon.setFill(ThreatLevel.WARNING.getDisplayColor());

    /*
      Creates the Field border shape by subtracting the rectangle by its polygon creating a shape with a hole
      in its middle
      Also setting its color to be the same as an error (a wall) because it is the field border
     */
    Path subtract = (Path) Shape.subtract(rectangle, polygon);
    subtract.setFill(ThreatLevel.ERROR.getDisplayColor());
    subtract.setStroke(ThreatLevel.ERROR.getDisplayColor());
    subtract.setStrokeWidth(1);

//		subtract.setTranslateX(-scrollPane.getLayoutX());
//		subtract.setTranslateY(-scrollPane.getLayoutY());

//		System.out.println(scrollPane.getLayoutX());
//		System.out.println(scrollPane.getLayoutY());

//
//		subtract.setOnMouseClicked(event -> {
//			try {
//				handleMouseClicked(event);
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		});

    //Adds the field Obstacle to Field
    Field.getInstance().addObstacle(new FieldBorder(subtract));

    //Resets the polygon vertices
    this.polygon.getPoints().clear();
  }


  /**
   * Creates the {@link Obstacle} that defines an obstacle given the selected polygon and its {@link ThreatLevel}. Adds
   * it to the {@link Field} using {@link Field#addObstacle(Obstacle)}
   */
  private void createObstacle(ThreatLevel threatLevel) {
    /*
    Recreates the polygon with the obstacle defining point because of an offset problem with JavaFX that has
    been unresolved
    */
    Polygon polygon = new Polygon(
        this.polygon.getPoints().stream().mapToDouble(value -> value).toArray());
    polygon.setFill(threatLevel.getDisplayColor());
    polygon.setStroke(threatLevel.getDisplayColor());
    polygon.setStrokeWidth(1);

    //Adds the Obstacle to Field
    Field.getInstance().addObstacle(new Obstacle(threatLevel, polygon));

    //Resets the polygon vertices
    this.polygon.getPoints().clear();
  }

  /**
   * Creates an {@link Obstacle} with a {@link ThreatLevel} of {@link ThreatLevel#WARNING}
   */
  private void createNormalObstacle() {
    createObstacle(ThreatLevel.WARNING);
  }


  /**
   * Creates an {@link Obstacle} with a {@link ThreatLevel} of {@link ThreatLevel#ERROR}
   */
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
        if (c.getRed() > .5 || c.getGreen() > .5 || c.getBlue() > .5) {
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
