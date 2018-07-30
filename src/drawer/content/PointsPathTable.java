package drawer.content;

import calibration.Field;
import drawer.curves.PointAngleGroup;
import drawer.curves.PointsPathGroup;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

public class PointsPathTable extends PathTable<PointsPathGroup, PointAngleGroup> {

	/**
	 * Initializes a TableView that observers the defining points of a path
	 *
	 * @param pathGroup points of the path to observe
	 */
	public PointsPathTable(PointsPathGroup pathGroup) {
		super(pathGroup);

		/////////////////////// CONTEXT MENU INITIALIZATION    //////////////////////////
		ContextMenu pathTableContextMenu = new ContextMenu();
		MenuItem addPoint = new MenuItem("Add Point");
		addPoint.setOnAction(event -> pathGroup.add(new PointAngleGroup(0, 0)));

		MenuItem removePoint = new MenuItem("Remove Points");
		removePoint.setOnAction(event -> pathGroup.removeAll(getSelectionModel().getSelectedItems()));

		MenuItem showPointsDetails = new MenuItem("Show Points Details");
		showPointsDetails.setOnAction(event -> pathGroup.showAllPointDetails(getSelectionModel().getSelectedItems()));

		MenuItem hidePointsDetails = new MenuItem("Hide Points Details");
		hidePointsDetails.setOnAction(event -> pathGroup.hideAllPointDetails(getSelectionModel().getSelectedItems()));

		pathTableContextMenu.getItems().addAll(addPoint, removePoint, showPointsDetails, hidePointsDetails);
		setContextMenu(pathTableContextMenu);
		///////////////////////////////////////////////////////////////////////////////////

		//Adds column to observe the name property of points
		initializeStringColumn("Name", "name",
			cellEditEvent -> cellEditEvent.getTableView().getItems().get(
				cellEditEvent.getTablePosition().getRow())
				.setName(cellEditEvent.getNewValue()));

		//Adds column to observe the scaled X property of points
		initializeNumberColumn("X", "translatedX",
			cellEditEvent -> {
				PointAngleGroup pointAngleGroup = cellEditEvent.getTableView().getItems().get(
					cellEditEvent.getTablePosition().getRow());

				pointAngleGroup.getPositionPoint().setCenterX((
					cellEditEvent.getNewValue().doubleValue()) / Field.SCALE.get() + pointAngleGroup.getOriginPoint()
					.getPositionPoint()
					.getCenterX());
			}
		);

		//Adds column to observe the scaled Y property of points
		initializeNumberColumn("Y", "translatedY",
			cellEditEvent -> {
				PointAngleGroup pointAngleGroup = cellEditEvent.getTableView().getItems().get(
					cellEditEvent.getTablePosition().getRow());

				pointAngleGroup.getPositionPoint().setCenterY((
					cellEditEvent.getNewValue().doubleValue()) / Field.SCALE.get() + pointAngleGroup.getOriginPoint()
					.getPositionPoint()
					.getCenterY());
			});

		//Adds column to observe the scaled Y property of points
		initializeNumberColumn("Angle", "degrees",
			cellEditEvent -> {
				PointAngleGroup pointAngleGroup = cellEditEvent.getTableView().getItems().get(
					cellEditEvent.getTablePosition().getRow());

				pointAngleGroup
					.getObservedDirectionalArrow().angleProperty()
					.set(Math.toRadians(cellEditEvent.getNewValue().doubleValue()) + pointAngleGroup.getOriginPoint()
						.getObservedDirectionalArrow().getAngle());
			});


	}
}
