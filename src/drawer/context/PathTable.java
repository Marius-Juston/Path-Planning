package drawer.context;

import calibration.Field;
import drawer.curves.PointAngleGroup;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.NumberStringConverter;

// Copied from my CurveDrawer class from my POE-Project Repository
public class PathTable extends TableView<PointAngleGroup> {

	/**
	 * Initializes a TableView that observers the defining points of a path
	 *
	 * @param pathGroup points of the path to observe
	 */
	public PathTable(PathGroup pathGroup) {
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

		setEditable(true);
		setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);
		getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		//Adds column to observe the name property of points
		initializeStringColumn("Name", "name",
			cellEditEvent -> cellEditEvent.getTableView().getItems().get(
				cellEditEvent.getTablePosition().getRow())
				.setName(cellEditEvent.getNewValue()));

		//Adds column to observe the scaled X property of points
		initializeNumberColumn("X", "centerX",
			cellEditEvent -> cellEditEvent.getTableView().getItems().get(
				cellEditEvent.getTablePosition().getRow())
				.getPositionPoint().setCenterX(cellEditEvent.getNewValue().doubleValue() * Field.SCALE.get())
		);

		//Adds column to observe the scaled Y property of points
		initializeNumberColumn("Y", "centerY",
			cellEditEvent -> cellEditEvent.getTableView().getItems().get(
				cellEditEvent.getTablePosition().getRow())
				.getPositionPoint().setCenterY(cellEditEvent.getNewValue().doubleValue() * Field.SCALE.get()));

		//Adds column to observe the scaled Y property of points
		initializeNumberColumn("Angle", "degrees",
			cellEditEvent -> cellEditEvent.getTableView().getItems().get(
				cellEditEvent.getTablePosition().getRow())
				.getObservedDirectionalArrow().angleProperty()
				.set(Math.toRadians(cellEditEvent.getNewValue().doubleValue())));

		setItems(pathGroup.getKeyPoints());
	}

	/**
	 * Initializes columns that hold in number
	 *
	 * @param columnName name of the column
	 * @param property name of the property the column should be observing
	 * @param eventHandler event handler that handles when a value from the column is changed
	 */
	private void initializeNumberColumn(String columnName, String property,
		EventHandler<CellEditEvent<PointAngleGroup, Number>> eventHandler) {

		TableColumn<PointAngleGroup, Number> column = new TableColumn<>();
		column.setCellValueFactory(new PropertyValueFactory<>(property));
		column.setCellFactory(TextFieldTableCell.forTableColumn(new NumberStringConverter()));
		column.setOnEditCommit(eventHandler);

		column.setText(columnName);

		getColumns().add(column);
	}

	/**
	 * Initializes columns that hold in strings
	 *
	 * @param columnName name of the column
	 * @param property name of the property the column should be observing
	 * @param eventHandler event handler that handles when a value from the column is changed
	 */
	private void initializeStringColumn(String columnName, String property,
		EventHandler<CellEditEvent<PointAngleGroup, String>> eventHandler) {
		TableColumn<PointAngleGroup, String> column = new TableColumn<>();
		column.setCellValueFactory(new PropertyValueFactory<>(property));
		column.setCellFactory(TextFieldTableCell.forTableColumn());
		column.setOnEditCommit(eventHandler);

		column.setText(columnName);

		getColumns().add(column);
	}
}
