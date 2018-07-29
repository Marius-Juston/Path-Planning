package drawer.content;

import drawer.curves.PointAngleGroup;
import drawer.draw.DrawnPath;
import drawer.draw.PathType;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;

public class PathGroup extends Group {

	private final ObservableList<PointAngleGroup> keyPoints = FXCollections.observableArrayList(
		param -> new Observable[]{param.centerXProperty(),
			param.centerYProperty(), param.angleProperty()});
	public PointAngleGroup originPoint = new PointAngleGroup(0, 0);
	private DrawnPath drawer = new DrawnPath(PathType.SPLINE);

	public PathGroup() {
		keyPoints.addListener((ListChangeListener<? super PointAngleGroup>) this::reDrawContent);

		originPoint.getPositionPoint().setFill(Color.GREEN);
		getChildren().addAll(originPoint, drawer);
	}

	public void setOriginPoint(PointAngleGroup newOriginPoint) {
		this.originPoint.angleProperty().set(newOriginPoint.angleProperty().get());
		this.originPoint.centerXProperty().set(newOriginPoint.centerXProperty().get());
		this.originPoint.centerYProperty().set(newOriginPoint.centerYProperty().get());
	}

	private void updateDrawingType(Change<? extends PointAngleGroup> c) {
		if (c.getList().size() == 2) {
			if (isStraightLine(c)) {
				drawer.setPathType(PathType.STRAIGHT_LINE);
			} else if (isPointTurn(c)) {
				drawer.setPathType(PathType.POINT_TURN);
			} else {
				drawer.setPathType(PathType.SPLINE);
			}
		} else {
			drawer.setPathType(PathType.SPLINE);
		}
	}

	private void reDrawContent(Change<? extends PointAngleGroup> c) {
		updateDrawingType(c);
		drawer.draw(c);

	}

	private boolean isPointTurn(Change<? extends PointAngleGroup> c) {

		ObservableList<? extends PointAngleGroup> list = c.getList();
		PointAngleGroup p1 = list.get(0);
		PointAngleGroup p2 = list.get(1);

		return p1.getPositionPoint().equalsPosition(p2.getPositionPoint());
	}

	private boolean isStraightLine(Change<? extends PointAngleGroup> c) {

		ObservableList<? extends PointAngleGroup> list = c.getList();
		PointAngleGroup p1 = list.get(0);
		PointAngleGroup p2 = list.get(1);

		boolean sameAngle = p1.getDegrees() == p2.getDegrees();

		double angleBetweenPoints = StrictMath
			.atan2(p2.getPositionPoint().getCenterY() - p1.getPositionPoint().getCenterY(),
				p2.getPositionPoint().getCenterX() - p1.getPositionPoint().getCenterX());

		return (sameAngle && -angleBetweenPoints == p1.getObservedDirectionalArrow().getAngle());
	}

	public ObservableList<PointAngleGroup> getKeyPoints() {
		return keyPoints;
	}


	public void add(PointAngleGroup pointAngleCombo) {
		pointAngleCombo.setOrigin(originPoint);

		MenuItem pointTurnCreation = new MenuItem("Create point turn");
		pointTurnCreation.setOnAction(event -> {

			if (getKeyPoints().size() == 1) {
				drawer.setPathType(PathType.POINT_TURN);

				add(new PointAngleGroup(pointAngleCombo.centerXProperty().get(),
					pointAngleCombo.centerYProperty().get()));
			}
		});

//		MenuItem straightLineCreation = new MenuItem("Create straightLine");
//		straightLineCreation.setOnAction(event -> {
//
//			if (getKeyPoints().size() == 1) {
//
//				drawer = new DrawnStrightLine();
//				getChildren().set(0, drawer);
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
