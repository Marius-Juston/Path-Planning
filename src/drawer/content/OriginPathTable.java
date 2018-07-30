package drawer.content;

import calibration.Field;
import drawer.curves.OriginPoint;
import drawer.curves.OriginsPathGroup;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

public class OriginPathTable extends PathTable<OriginsPathGroup, OriginPoint> {

	/**
	 * Initializes a TableView that observers the defining points of a path
	 *
	 * @param pathGroup points of the path to observe
	 */
	public OriginPathTable(OriginsPathGroup pathGroup) {
		super(pathGroup);
		/////////////////////// CONTEXT MENU INITIALIZATION    //////////////////////////
		ContextMenu pathTableContextMenu = new ContextMenu();

		MenuItem showPointsDetails = new MenuItem("Show Points Details");
		showPointsDetails.setOnAction(event -> pathGroup.showAllPointDetails(getSelectionModel().getSelectedItems()));

		MenuItem hidePointsDetails = new MenuItem("Hide Points Details");
		hidePointsDetails.setOnAction(event -> pathGroup.hideAllPointDetails(getSelectionModel().getSelectedItems()));

		pathTableContextMenu.getItems().addAll(showPointsDetails, hidePointsDetails);
		setContextMenu(pathTableContextMenu);
		///////////////////////////////////////////////////////////////////////////////////

		//Adds column to observe the name property of points
		initializeStringColumn("Name", "name",
			cellEditEvent -> cellEditEvent.getTableView().getItems().get(
				cellEditEvent.getTablePosition().getRow())
				.setName(cellEditEvent.getNewValue()));

		//Adds column to observe the scaled X property of points
		initializeNumberColumn("X", "centerX",
			cellEditEvent -> {
				OriginPoint pointAngleGroup = cellEditEvent.getTableView().getItems().get(
					cellEditEvent.getTablePosition().getRow());

				pointAngleGroup.getPositionPoint().setCenterX((
					cellEditEvent.getNewValue().doubleValue()) / Field.SCALE.get());
			}
		);

		//Adds column to observe the scaled Y property of points
		initializeNumberColumn("Y", "centerY",
			cellEditEvent -> {
				OriginPoint pointAngleGroup = cellEditEvent.getTableView().getItems().get(
					cellEditEvent.getTablePosition().getRow());

				pointAngleGroup.getPositionPoint().setCenterY((
					cellEditEvent.getNewValue().doubleValue()) / Field.SCALE.get());
			});

		//Adds column to observe the scaled Y property of points
		initializeNumberColumn("Angle", "degrees",
			cellEditEvent -> {
				OriginPoint pointAngleGroup = cellEditEvent.getTableView().getItems().get(
					cellEditEvent.getTablePosition().getRow());

				pointAngleGroup
					.getObservedDirectionalArrow().angleProperty()
					.set(Math.toRadians(cellEditEvent.getNewValue().doubleValue()));
			});

	}
}
