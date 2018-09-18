package drawer;

import calibration.Controller;
import calibration.Field;
import calibration.Helper;
import drawer.content.PathTitledTab;
import drawer.content.PathTitledTab.PointsAdded;
import drawer.content.RenameDialog;
import drawer.content.origin.OriginPathTable;
import drawer.content.origin.OriginsPathTitledTab;
import drawer.content.points.PointsPathTable;
import drawer.content.points.PointsPathTitledTab;
import drawer.curves.OriginsPathGroup;
import drawer.curves.PointAngleGroup;
import drawer.curves.figures.OriginPoint;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Shape;
import org.waltonrobotics.controller.Pose;
import org.waltonrobotics.motion.Path;

public class PointPlacer implements Initializable {
	/*
	TODO make it so that you can right click on the titled path and you could change the parameters of the curve (start/end velocities, start/end scale)
	TODO make the robot move along the path
	TODO make the program be able to calculate the shortest path between two point angle group all the while evading the obstacles
	TODO make a mesh a program

	TODO make the TitledPane Content a VBOx with a HBOX (with two fields start and end scale) and then the table view
	TODO add a measuring tool in the edit menu Ctrl+M as shortcut (generalize it so that the calibration code can also use it)
	TODO make clipping so that when a point a close to another point it joins together to make a point turn
	TODO make clipping so that when a point is close to being in the same line as another it joins or you can select two points of the table view and it will find the closest point (intersecting perpendicular lines) and reposition itself there
	TODO do a save button

  Partially finished:
	TODO scale the points when displaying them to the screen otherwise the velocities will be incorrect
	TODO when creating a new PathTitledTab be able to choose existing or create a new origin point
	TODO make the send to SmartDashboard functionality work
	TODO make a receive from SmartDashboard functionality

	Finished
	TODO make origin point
	TODO show name of point when showing details
	TODO be able to place shapes that define the field borders when a path intersects or the robot width intersects
		- have two types of shapes:
		    - WARNING - in orange (Ex: goes over bumpy thing)
		    - ERROR - in red (Ex: goes through a wall)
		- use Polygon.intersect()

	*/

  private static final double originsDividerPosition = 0.15772870662460567;
  private final ChoiceDialog<PathNetworkTableKeyPathPair> stringChoiceDialog = new ChoiceDialog<>();
  private final Alert confirmPoint = new Alert(AlertType.CONFIRMATION);
  private final Accordion originsPaneAccordion = new Accordion();
  @FXML
  private ImageView field;
  @FXML
  private AnchorPane pointPlane;
  @FXML
  private SplitPane splitPane;
  private Accordion pointsTitledPaneAccordion;

  public PointPlacer() {
    confirmPoint
        .setContentText(
            "There is an obstacle in this position are you sure you wish to place a point here?");
  }

  public static Parent getRoot() throws IOException {
    Parent root = FXMLLoader.load(PointPlacer.class.getResource("pointPlacer.fxml"));

    root.getStylesheets().add(PointPlacer.class.getResource("circles.css").toExternalForm());
    return root;
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    SplineSender.initNetworkTableParallel();

    if (Field.getInstance().getImage() == null) {
      try {
        File imageFile = new File("./src/FRC 2018 Field Drawings.png");
        Image defaultImage = Helper.getImage(imageFile);
        field.setImage(defaultImage);
        Field.getInstance().setImage(field.getImage());
        Field.getInstance().setImageFile(imageFile);
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }
    } else {
      field.setImage(Field.getInstance().getImage());

      pointPlane.getChildren().add(Field.getInstance().getObstacleGroup());
    }

    pointsTitledPaneAccordion = new Accordion();
    pointsTitledPaneAccordion.expandedPaneProperty().addListener((property, oldPane, newPane) -> {
      if (oldPane != null) {
        oldPane.setCollapsible(true);
      }
      if (newPane != null) {
        Platform.runLater(() -> newPane.setCollapsible(false));
      }
    });

    splitPane.widthProperty()
        .addListener((observable, oldValue, newValue) -> updateDividerPositions());

    stringChoiceDialog.showingProperty()
        .addListener((observable, oldValue, newValue) -> {
          List<PathNetworkTableKeyPathPair> availablePathChoices = findAvailablePathChoices(
              SplineSender.isClient() ? SplineSender.SMARTDASHBOARD_NETWORKTABLE_KEY
                  : SplineSender.NETWORK_TABLE_TABLE_KEY); //TODO make it so that you can see and select the table you want table

          stringChoiceDialog.getItems().setAll(availablePathChoices);

          if (!availablePathChoices.isEmpty()) {
            stringChoiceDialog.setSelectedItem(availablePathChoices.get(0));
          }
        });
  }

  private List<PathNetworkTableKeyPathPair> findAvailablePathChoices(String table) {
    NetworkTable networkTable = NetworkTable.getTable(table);

    Set<String> networkTableKeys = networkTable.getKeys(4);//Through testing key 4 is strings

    List<PathNetworkTableKeyPathPair> pathNetworkTableKeyPathPairs = new LinkedList<>();

    for (String key : networkTableKeys) {
      String stringValue = networkTable.getString(key, "");

      try {
        Path path = Path.loadingPathFromString(stringValue, Field.getInstance().SCALE.get());

        pathNetworkTableKeyPathPairs.add(new PathNetworkTableKeyPathPair(key, path));
      } catch (InstantiationException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException | IllegalAccessException e) {
        e.printStackTrace();
      }
    }

    return pathNetworkTableKeyPathPairs;
  }

  public void saveData(ActionEvent actionEvent) {
    //TODO
  }

  public void loadData(ActionEvent actionEvent) {
    //TODO
  }

  public void openData(ActionEvent actionEvent) {
    //TODO
  }

  public void handlePointEvent(MouseEvent mouseEvent) {
    if (mouseEvent.getButton() == MouseButton.PRIMARY) {
      addPoint(mouseEvent);
    }
  }

  private void addPoint(MouseEvent mouseEvent) {

    Node intersectedNode = mouseEvent.getPickResult().getIntersectedNode();

    boolean isFieldObstacle = Field.getInstance().getFieldObstacles().stream()
        .anyMatch(obstacle -> obstacle.getChildren().contains(intersectedNode));

    if (isFieldObstacle) {
      Optional<ButtonType> buttonType = confirmPoint.showAndWait();

      if (buttonType.isPresent() && (buttonType.get() != ButtonType.OK)) {
        isFieldObstacle = false;
      }
    }

    if (!(intersectedNode instanceof Shape) || isFieldObstacle) {

      PointsPathTitledTab pointsPathTitledTab;
      if (pointsTitledPaneAccordion.getPanes().isEmpty()) {
//////				TODO clean this up ///////////////////////////////////////////////////////////////
        PathTitledTab<OriginsPathGroup> originsPathTitledTab = createOriginsPathTitledTab();

        pointsPathTitledTab = createAndSetupPathTitledTab();

        originsPathTitledTab.setCollapsible(false);
        originsPathTitledTab.setText("Origin points");

        originsPaneAccordion.getPanes().add(originsPathTitledTab);
        originsPaneAccordion.setExpandedPane(originsPathTitledTab);

        splitPane.getItems().add(0, originsPaneAccordion);
        updateDividerPositions();

      } else {
        pointsPathTitledTab = getExpandedPane();
      }

      if (pointsPathTitledTab.getPointNumber() == PointsAdded.FIRST_POINT) {
        pointsPathTitledTab.setPointNumber(PointsAdded.SECOND_POINT);

        OriginPoint originPoint = pointsPathTitledTab.getPointsPathGroup().getOriginPoint();

        pointsPathTitledTab.getPointsPathGroup()
            .setOriginPoint(mouseEvent.getX(), mouseEvent.getY());

        ((OriginsPathTitledTab) originsPaneAccordion.getExpandedPane()).getPointsPathGroup()
            .add(originPoint);

        pointPlane.getChildren().add(originPoint);

/////////////////////////////////////////////////////////////////////////////////////////
      } else {
        PointAngleGroup keyPoint = new PointAngleGroup(mouseEvent.getX(), mouseEvent.getY());

        if (pointsPathTitledTab.getPointNumber() == PointsAdded.SECOND_POINT) {
          pointsPathTitledTab.setPointNumber(PointsAdded.MORE);
          splitPane.getItems().add(pointsTitledPaneAccordion);
          updateDividerPositions();
        }

        pointPlane.getChildren().add(keyPoint);
        getExpandedPane().getPointsPathGroup().add(keyPoint);
      }

    }
  }

  private PathTitledTab<OriginsPathGroup> createOriginsPathTitledTab() {
    OriginsPathTitledTab pathTitledTab = new OriginsPathTitledTab();

    {
      MenuItem rename = new MenuItem("Rename");
      rename.setOnAction(event -> {
        try {
          pathTitledTab.setText(RenameDialog.display(pathTitledTab.getText()));
        } catch (IOException e) {
          e.printStackTrace();
        }
      });

      ContextMenu contextMenu = new ContextMenu(rename);
      pathTitledTab.setContextMenu(contextMenu);

      pathTitledTab.setContent(
          new OriginPathTable(pathTitledTab.getPointsPathGroup(), pointsTitledPaneAccordion));
    }

    return pathTitledTab;
  }

  private PointsPathTitledTab createPointsPathTitledTab() {

    PointsPathTitledTab pathTitledTab = new PointsPathTitledTab();

    {
      MenuItem rename = new MenuItem("Rename");
      rename.setOnAction(event -> {
        try {
          pathTitledTab.setText(RenameDialog.display(pathTitledTab.getText()));
        } catch (IOException e) {
          e.printStackTrace();
        }
      });

      MenuItem delete = new MenuItem("Delete");
      delete.setOnAction(event -> {
        ObservableList<TitledPane> panes = pointsTitledPaneAccordion.getPanes();

        if (panes.size() > 1) {

          if (pointsTitledPaneAccordion.getExpandedPane().equals(pathTitledTab)) {
            pointsTitledPaneAccordion.setExpandedPane(panes.get(panes.size() - 1));
          }

          pointPlane.getChildren().remove(pathTitledTab.getPointsPathGroup());
          panes.remove(pathTitledTab);
        } else {
//					pathTitledTab.clear();
        }
      });

      MenuItem sendPath = new MenuItem("Send");
      sendPath.setOnAction(this::sendCurrentPath);

      ContextMenu contextMenu = new ContextMenu(rename, delete, sendPath);
      pathTitledTab.setContextMenu(contextMenu);

      pathTitledTab.setContent(new PointsPathTable(pathTitledTab.getPointsPathGroup()));
    }
    return pathTitledTab;
  }

  private PointsPathTitledTab createAndSetupPathTitledTab() {

    PointsPathTitledTab pathTitledTab = createPointsPathTitledTab();

    pointsTitledPaneAccordion.getPanes().add(pathTitledTab);
    pointPlane.getChildren().add(pathTitledTab.getPointsPathGroup());
    pointsTitledPaneAccordion.setExpandedPane(pathTitledTab);
    return pathTitledTab;
  }

  private PointsPathTitledTab getExpandedPane() {
    return (PointsPathTitledTab) pointsTitledPaneAccordion.getExpandedPane();
  }

  public void goBackToFieldSelector(ActionEvent actionEvent) throws IOException {

    Parent root = Controller.getRoot();
    Helper.setRoot(actionEvent, root);
  }

  public void newPath(ActionEvent actionEvent) {
    createAndSetupPathTitledTab();
  }

  public void togglePointTable(ActionEvent actionEvent) {
    if (splitPane.getItems().contains(pointsTitledPaneAccordion)) {
      splitPane.getItems().remove(pointsTitledPaneAccordion);
    } else {
      splitPane.getItems().add(pointsTitledPaneAccordion);
    }
    updateDividerPositions();
  }

  public void toggleOriginsTable(ActionEvent actionEvent) {
    if (splitPane.getItems().contains(originsPaneAccordion)) {
      splitPane.getItems().remove(originsPaneAccordion);
    } else {
      splitPane.getItems().add(0, originsPaneAccordion);
    }
    updateDividerPositions();
  }

  private void updateDividerPositions() {
    if (splitPane.getItems().contains(originsPaneAccordion)) {
      splitPane.getDividers().get(0).setPosition(originsDividerPosition);
    }
    if (splitPane.getItems().contains(pointsTitledPaneAccordion)) {
      splitPane.getDividers().get(splitPane.getDividers().size() - 1)
          .setPosition(1 - originsDividerPosition);
    }
  }

  private void sendCurrentPath(ActionEvent event) {
    SplineSender.sendPath(getExpandedPane());
  }

  public void toggleShowingVelocityArrows(ActionEvent actionEvent) {
    getExpandedPane().toggleShowingVelocity();
  }

  public void sendAllToSmartDashboard(ActionEvent event) {
    for (TitledPane titledTab : pointsTitledPaneAccordion.getPanes()) {
      PointsPathTitledTab pointsPathTitledTab = (PointsPathTitledTab) titledTab;
      SplineSender.sendPath(pointsPathTitledTab);
    }
  }

  public void loadPathFromNetworkTable(ActionEvent event) {
    Optional<PathNetworkTableKeyPathPair> pathNetworkTableKeyPathPair = stringChoiceDialog
        .showAndWait();

    if (pathNetworkTableKeyPathPair.isPresent()) {
      Path path = pathNetworkTableKeyPathPair.get().path;

      ChoiceDialog<OriginPoint> choiceDialog = new ChoiceDialog<>();
      ObservableList<OriginPoint> keyPoints = ((OriginsPathTitledTab) originsPaneAccordion
          .getExpandedPane()).getPointsPathGroup().getKeyPoints();

      OriginPoint originPoint;
      if (keyPoints.isEmpty()) {
        originPoint = null;
      } else if (keyPoints.size() > 1) {
        choiceDialog.getItems().setAll(keyPoints);
        choiceDialog.setSelectedItem(keyPoints.get(0));

        Optional<OriginPoint> originPoint1 = choiceDialog.showAndWait();

        originPoint = originPoint1.orElse(null);
      } else {
        originPoint = keyPoints.get(0);
      }

      PointsPathTitledTab pointsPathTitledTab = createAndSetupPathTitledTab();

      for (Pose keyPoint : path.getKeyPoints()) {
        pointsPathTitledTab.getPointsPathGroup().add(new PointAngleGroup(keyPoint));
      }

      pointsPathTitledTab.getPointsPathGroup().changeOrigin(originPoint);
    }
  }

  public void toggleClientServerMode(ActionEvent event) {

    SplineSender.setClient(!SplineSender.isClient());
  }

  private static class PathNetworkTableKeyPathPair {

    private final String networkTableKey;
    private final Path path;

    PathNetworkTableKeyPathPair(String networkTableKey, Path path) {
      this.networkTableKey = networkTableKey;
      this.path = path;
    }

    public String getNetworkTableKey() {
      return networkTableKey;
    }

    public Path getPath() {
      return path;
    }

    @Override
    public String toString() {
      return networkTableKey;
    }
  }
}
