package drawer;

import calibration.Controller;
import calibration.Field;
import calibration.Helper;
import drawer.content.PathTable;
import drawer.content.PathTitledTab;
import drawer.content.RenameDialog;
import drawer.curves.PointAngleGroup;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Accordion;
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
	TODO make origin point
	TODO make the TitledPane Content a VBOx with a HBOX (with two fields start and end scale) and then the table view
	TODO when creating a a new table be able to choose existing or create a new origin point
	TODO add a measuring tool in the edit menu Ctrl+M as shortcut (generalize it so that the calibration code can also use it)
	TODO make clipping so that when a point a close to another point it joins together to make a point turn
	TODO make clipping so that when a point is close to being in the same line as another it joins or you can select two points of the table view and it will find the closest point (intersecting perpendicular lines) and reposition itself there
	TODO make the send to SmartDashboard functionality work
	TODO make a receive from SmartDashboard functionality
	TODO do a save button
	*/

	public ImageView field;
	@FXML
	public AnchorPane pointPlane;
	public Accordion titledPaneAccordion;
	public SplitPane splitPane;
	private boolean isFirstPoint = true;

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

	public void addPoint(MouseEvent mouseEvent) {

		if (!(mouseEvent.getPickResult().getIntersectedNode() instanceof Shape)) {
			PointAngleGroup pointAngleCombo = new PointAngleGroup(mouseEvent.getX(), mouseEvent.getY());

			pointPlane.getChildren().add(pointAngleCombo);

			if (isFirstPoint) {
				isFirstPoint = false;

				splitPane.getItems().add(titledPaneAccordion);

				createAndSetupPathTitledTab();

			}
			getExpandedPane().getKeyPoints().add(pointAngleCombo);
		}
	}

	public void createAndSetupPathTitledTab() {

		PathTitledTab pathTitledTab = new PathTitledTab();

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

					pointPlane.getChildren().remove(pathTitledTab.getKeyPoints());
					panes.remove(pathTitledTab);
				} else {
//					pathTitledTab.clear();
				}
			});

			ContextMenu contextMenu = new ContextMenu(rename, delete);
			pathTitledTab.setContextMenu(contextMenu);

			pathTitledTab.setContent(new PathTable(pathTitledTab.keyPoints));
		}

		titledPaneAccordion.getPanes().add(pathTitledTab);
		pointPlane.getChildren().add(pathTitledTab.getKeyPoints());
		titledPaneAccordion.setExpandedPane(pathTitledTab);
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

	public void tooglePointTable(ActionEvent actionEvent) {
		if (splitPane.getItems().contains(titledPaneAccordion)) {
			splitPane.getItems().remove(titledPaneAccordion);
		} else {
			splitPane.getItems().add(titledPaneAccordion);
		}
	}
}
