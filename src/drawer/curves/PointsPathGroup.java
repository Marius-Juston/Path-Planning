package drawer.curves;

import drawer.content.PathGroup;
import drawer.curves.figures.OriginPoint;
import drawer.draw.DrawnPath;
import drawer.draw.PathType;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;

public class PointsPathGroup extends PathGroup<drawer.curves.PointAngleGroup> {

	private OriginPoint originPoint = new OriginPoint(10, 10);
	private DrawnPath drawer = new DrawnPath(PathType.SPLINE);

	public PointsPathGroup() {
		getKeyPoints().addListener((ListChangeListener<? super PointAngleGroup>) this::reDrawContent);

//		originPoint.setVisible(false);

		getChildren().addAll(drawer);
	}

	private static boolean isPointTurn(Change<? extends PointAngleGroup> c) {

		ObservableList<? extends PointAngleGroup> list = c.getList();
		PointAngleGroup p1 = list.get(0);
		PointAngleGroup p2 = list.get(1);

		return p1.getPositionPoint().equalsPosition(p2.getPositionPoint());
	}

	private static boolean isStraightLine(Change<? extends PointAngleGroup> c) {

		ObservableList<? extends PointAngleGroup> list = c.getList();
		PointAngleGroup p1 = list.get(0);
		PointAngleGroup p2 = list.get(1);

		boolean sameAngle = p1.getDegrees() == p2.getDegrees();

		double angleBetweenPoints = StrictMath
			.atan2(p2.getPositionPoint().getCenterY() - p1.getPositionPoint().getCenterY(),
				p2.getPositionPoint().getCenterX() - p1.getPositionPoint().getCenterX());

		return (sameAngle && (-angleBetweenPoints == p1.getObservedDirectionalArrow().getAngle()));
	}

	public OriginPoint getOriginPoint() {
		return originPoint;
	}

	public void setOriginPoint(double x, double y) {
		originPoint.centerXProperty().set(x);
		originPoint.centerYProperty().set(y);

//		originPoint.setVisible(true);
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
//			if (getPointsPathGroup().size() == 1) {
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
				if (getKeyPoints().size() == 1) {
					pointTurnCreation.setDisable(false);
				} else {
					pointTurnCreation.setDisable(true);
				}
//				if (getPointsPathGroup().size() != 2) {
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
		getKeyPoints().add(pointAngleCombo);
	}

	public DrawnPath getDrawer() {
		return drawer;
	}
}
