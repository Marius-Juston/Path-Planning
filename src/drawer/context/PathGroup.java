package drawer.context;

import drawer.curves.PointAngleGroup;
import drawer.paths.DrawnPath;
import drawer.paths.DrawnPointTurn;
import drawer.paths.DrawnSpline;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;

public class PathGroup extends Group {

	private final ObservableList<PointAngleGroup> keyPoints = FXCollections.observableArrayList(
		param -> new Observable[]{param.centerXProperty(),
			param.centerYProperty(), param.angleProperty()});

	private DrawnPath splinePathGroup = new DrawnSpline();

	public PathGroup() {

		keyPoints.addListener(
			(ListChangeListener<? super PointAngleGroup>) c -> splinePathGroup.clearCreateAndAddPoints(c.getList()));

		getChildren().add(splinePathGroup);
	}

	public ObservableList<PointAngleGroup> getKeyPoints() {
		return keyPoints;
	}


	public void add(PointAngleGroup pointAngleCombo) {

		MenuItem pointTurnCreation = new MenuItem("Create point turn");
		pointTurnCreation.setOnAction(event -> {

			if (getKeyPoints().size() == 1) {

				splinePathGroup = new DrawnPointTurn();
				getChildren().set(0, splinePathGroup);

				add(new PointAngleGroup(pointAngleCombo.centerXProperty().get(),
					pointAngleCombo.centerYProperty().get()));
			}

		});

//		MenuItem straightLineCreation = new MenuItem("Create straightLine");
//		straightLineCreation.setOnAction(event -> {
//
//			if (getKeyPoints().size() == 1) {
//
//				splinePathGroup = new DrawnStrightLine();
//				getChildren().set(0, splinePathGroup);
//
//				add(new PointAngleGroup(pointAngleCombo.centerXProperty().get(),
//					pointAngleCombo.centerYProperty().get()));
//			}
//
//		});

		ContextMenu contextMenu = new ContextMenu(pointTurnCreation
//			, straightLineCreation
		);
		pointAngleCombo.getPositionPoint().setOnMouseClicked(event -> {
			pointAngleCombo.handleMouseClicked(event);

			if (event.getButton() == MouseButton.SECONDARY) {
				if (getKeyPoints().size() != 1) {
					pointTurnCreation.setDisable(true);
				} else {
					pointTurnCreation.setDisable(false);
				}
//				if (getKeyPoints().size() != 2) {
//					straightLineCreation.setDisable(true);
//				} else {
//					straightLineCreation.setDisable(false);
//				}

				contextMenu.show(pointAngleCombo.getPositionPoint(), event.getScreenX(), event.getScreenY());
			} else {
				contextMenu.hide();
			}
		});

		getChildren().add(pointAngleCombo);
		keyPoints.add(pointAngleCombo);
	}

	public void removeAll(ObservableList<PointAngleGroup> selectedItems) {
		getChildren().removeAll(selectedItems);
		keyPoints.removeAll(selectedItems);
	}

	public void showAllPointDetails(ObservableList<PointAngleGroup> selectedItems) {
		for (PointAngleGroup pointAngleGroup : selectedItems) {
			pointAngleGroup.setSelected(true);
		}
	}


	public void hideAllPointDetails(ObservableList<PointAngleGroup> selectedItems) {
		for (PointAngleGroup pointAngleGroup : selectedItems) {
			pointAngleGroup.setSelected(false);
		}
	}

	public void clear() {
		getKeyPoints().clear();
		getChildren().clear();
	}
}
