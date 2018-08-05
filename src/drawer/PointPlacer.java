package drawer;

import calibration.Controller;
import calibration.Field;
import calibration.Helper;
import drawer.content.OriginPathTable;
import drawer.content.PathTitledTab;
import drawer.content.PathTitledTab.PointsAdded;
import drawer.content.PointsPathTable;
import drawer.content.RenameDialog;
import drawer.curves.OriginPoint;
import drawer.curves.OriginsPathGroup;
import drawer.curves.PointAngleGroup;
import drawer.curves.PointsPathGroup;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
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

public class PointPlacer implements Initializable {
	/*
	TODO be able to place shapes that define the field borders when a path intersects or the robot width intersects
		- have two types of shapes:
		    - WARNING - in orange (Ex: goes over bumpy thing)
		    - ERROR - in red (Ex: goes through a wall)
		- use Polygon.intersect()

	TODO scale the points when displaying them to the screen otherwise the velocities will be incorrect

	TODO make the robot move along the path
	TODO make the program be able to calculate the shortest path between two point angle group all the while evading the obstacles

	TODO when creating a new PathTitledTab be able to choose existing or create a new origin point

	TODO make the TitledPane Content a VBOx with a HBOX (with two fields start and end scale) and then the table view
	TODO add a measuring tool in the edit menu Ctrl+M as shortcut (generalize it so that the calibration code can also use it)
	TODO make clipping so that when a point a close to another point it joins together to make a point turn
	TODO make clipping so that when a point is close to being in the same line as another it joins or you can select two points of the table view and it will find the closest point (intersecting perpendicular lines) and reposition itself there
	TODO make the send to SmartDashboard functionality work
	TODO make a receive from SmartDashboard functionality
	TODO do a save button

	TODO make origin point - Partially finished nee
	ds improvements

	TODO show name of point when showing details - DONE
	*/

	private static final double originsDividerPosition = 0.15772870662460567;
	private Alert confirmPoint = new Alert(AlertType.CONFIRMATION);
	@FXML
	private ImageView field;
	@FXML
	private AnchorPane pointPlane;
	@FXML
	private SplitPane splitPane;
	private Accordion titledPaneAccordion;
	private Accordion originsPaneAccordion = new Accordion();

	{
		confirmPoint
			.setContentText("There is an obstacle in this position are you sure you wish to place a point here?");
	}

	public static Parent getRoot() throws IOException {
		Parent root = FXMLLoader.load(PointPlacer.class.getResource("pointPlacer.fxml"));

		root.getStylesheets().add(PointPlacer.class.getResource("circles.css").toExternalForm());
		return root;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		if (Field.image == null) {
			try {
				File imageFile = new File("./src/FRC 2018 Field Drawings.png");
				Image defaultImage = Helper.getImage(imageFile);
				field.setImage(defaultImage);
				Field.image = field.getImage();
				Field.imageFile = imageFile;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			field.setImage(Field.image);

			pointPlane.getChildren().add(Field.obstacleGroup);

//			Field.obstacleGroup.setOnMousePressed(this::handlePointEvent);
		}

		titledPaneAccordion = new Accordion();
		titledPaneAccordion.expandedPaneProperty().addListener((property, oldPane, newPane) -> {
			if (oldPane != null) {
				oldPane.setCollapsible(true);
			}
			if (newPane != null) {
//				newPane.setCollapsible(false);

				Platform.runLater(() -> newPane.setCollapsible(false));
			}
		});

//		TODO uncomment this to see Polygon.intersect example
//		Rectangle rectangle = new Rectangle(200, 200, 50, 50);
//		rectangle.setFill(Color.BLUE);
//
//		Line line = new Line(200, 190, 270, 260);
//		line.setStrokeWidth(5);
//		line.setStroke(Color.RED);
//
//		Path shape = (Path) Polygon.intersect(rectangle, line);
//		shape.setFill(Color.GREEN);
//
//		pointPlane.getChildren().addAll(rectangle, line, shape);
	}

	public void saveData(ActionEvent actionEvent) {
	}

	public void loadData(ActionEvent actionEvent) {

	}

	public void openData(ActionEvent actionEvent) {

	}

	public void handlePointEvent(MouseEvent mouseEvent) {
		if (mouseEvent.getButton() == MouseButton.PRIMARY) {
			addPoint(mouseEvent);
		} else if (mouseEvent.getButton() == MouseButton.MIDDLE) {
			showPointInfo(mouseEvent);
		} else {
			removePoint(mouseEvent);
		}
	}

	private void removePoint(MouseEvent mouseEvent) {

	}

	/**
	 * Will show the angle of the point bold the point and allow you to change the angle direction
	 */
	private void showPointInfo(MouseEvent mouseEvent) {

	}

	private void addPoint(MouseEvent mouseEvent) {

		Node intersectedNode = mouseEvent.getPickResult().getIntersectedNode();

		boolean isFieldObstacle = Field.getFieldObstacles().stream()
			.anyMatch(obstacle -> obstacle.getChildren().contains(intersectedNode));

		if (isFieldObstacle) {
			Optional<ButtonType> buttonType = confirmPoint.showAndWait();

			if (buttonType.isPresent() && buttonType.get() != ButtonType.OK) {
				isFieldObstacle = false;
			}
		}

		if (!(intersectedNode instanceof Shape) || isFieldObstacle) {

			PathTitledTab<PointsPathGroup> pointsPathTitledTab;
			if (titledPaneAccordion.getPanes().isEmpty()) {
//////				TODO clean this up ///////////////////////////////////////////////////////////////
				PathTitledTab<OriginsPathGroup> originsPathTitledTab = createOriginsPathTitledTab();

				pointsPathTitledTab = createAndSetupPathTitledTab();

//				originsPathTitledTab.getPointsPathGroup().add(originPoint);

				originsPathTitledTab.setCollapsible(false);
//				pointPlane.getChildren().add(originPoint);
				originsPathTitledTab.setText("Origin points");

				originsPaneAccordion.getPanes().add(originsPathTitledTab);
				originsPaneAccordion.setExpandedPane(originsPathTitledTab);

				splitPane.getItems().add(0, originsPaneAccordion);
				updateDividerPositions();

			} else {
				pointsPathTitledTab = (PathTitledTab<PointsPathGroup>) titledPaneAccordion.getExpandedPane();
			}

			if (pointsPathTitledTab.getPointNumber() == PointsAdded.FIRST_POINT) {
				pointsPathTitledTab.setPointNumber(PointsAdded.SECOND_POINT);

//				PathTitledTab<PointsPathGroup> pointsPathTitledTab = createAndSetupPathTitledTab();

				OriginPoint originPoint = pointsPathTitledTab.getPointsPathGroup().getOriginPoint();

				pointsPathTitledTab.getPointsPathGroup().setOriginPoint(mouseEvent.getX(), mouseEvent.getY());

				((PathTitledTab<OriginsPathGroup>) originsPaneAccordion.getExpandedPane()).getPointsPathGroup()
					.add(originPoint);

				pointPlane.getChildren().add(originPoint);

/////////////////////////////////////////////////////////////////////////////////////////
			} else {
				PointAngleGroup keyPoint = new PointAngleGroup(mouseEvent.getX(), mouseEvent.getY());

				if (pointsPathTitledTab.getPointNumber() == PointsAdded.SECOND_POINT) {
					pointsPathTitledTab.setPointNumber(PointsAdded.MORE);
					splitPane.getItems().add(titledPaneAccordion);
					updateDividerPositions();
				}

				pointPlane.getChildren().add(keyPoint);
				((PointsPathGroup) getExpandedPane().getPointsPathGroup()).add(keyPoint);
			}

		}
	}

	private PathTitledTab<OriginsPathGroup> createOriginsPathTitledTab() {
		PathTitledTab<OriginsPathGroup> pathTitledTab = new PathTitledTab<>(new OriginsPathGroup());

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

			pathTitledTab.setContent(new OriginPathTable(pathTitledTab.pointsPathGroup));
		}

		return pathTitledTab;
	}

	private PathTitledTab<PointsPathGroup> createPointsPathTitledTab() {

		PathTitledTab<PointsPathGroup> pathTitledTab = new PathTitledTab<>(new PointsPathGroup());

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
				ObservableList<TitledPane> panes = titledPaneAccordion.getPanes();

				if (panes.size() > 1) {

					if (titledPaneAccordion.getExpandedPane().equals(pathTitledTab)) {
						titledPaneAccordion.setExpandedPane(panes.get(panes.size() - 1));
					}

					pointPlane.getChildren().remove(pathTitledTab.getPointsPathGroup());
					panes.remove(pathTitledTab);
				} else {
//					pathTitledTab.clear();
				}
			});

			ContextMenu contextMenu = new ContextMenu(rename, delete);
			pathTitledTab.setContextMenu(contextMenu);

			pathTitledTab.setContent(new PointsPathTable(pathTitledTab.pointsPathGroup));
		}
		return pathTitledTab;
	}

	private PathTitledTab<PointsPathGroup> createAndSetupPathTitledTab() {

		PathTitledTab<PointsPathGroup> pathTitledTab = createPointsPathTitledTab();

		titledPaneAccordion.getPanes().add(pathTitledTab);
		pointPlane.getChildren().add(pathTitledTab.getPointsPathGroup());
		titledPaneAccordion.setExpandedPane(pathTitledTab);
		return pathTitledTab;
	}

	private PathTitledTab getExpandedPane() {
		return (PathTitledTab) titledPaneAccordion.getExpandedPane();
	}

	public void goBackToFieldSelector(ActionEvent actionEvent) throws IOException {

		Parent root = Controller.getRoot();
		Helper.setRoot(actionEvent, root);
	}

	public void newPath(ActionEvent actionEvent) {
		createAndSetupPathTitledTab();
	}

	public void togglePointTable(ActionEvent actionEvent) {
		if (splitPane.getItems().contains(titledPaneAccordion)) {
			splitPane.getItems().remove(titledPaneAccordion);
		} else {
			splitPane.getItems().add(titledPaneAccordion);
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
		if (splitPane.getItems().contains(titledPaneAccordion)) {
			splitPane.getDividers().get(splitPane.getDividers().size() - 1).setPosition(1 - originsDividerPosition);
		}
	}


}
