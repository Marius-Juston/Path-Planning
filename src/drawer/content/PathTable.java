package drawer.content;

import drawer.curves.PointGroup;
import javafx.event.EventHandler;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.NumberStringConverter;

// Copied from my CurveDrawer class from my POE-Project Repository
public class PathTable<K extends PathGroup, U extends PointGroup> extends TableView<U> {

  /**
   * Initializes a TableView that observers the defining points of a path
   *
   * @param pathGroup points of the path to observe
   */
  public PathTable(K pathGroup) {

    setEditable(true);
    setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);
    getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

    setItems(pathGroup.getKeyPoints());
  }

  /**
   * Initializes columns that hold in number
   *
   * @param columnName name of the column
   * @param property name of the property the column should be observing
   * @param eventHandler event handler that handles when a value from the column is changed
   */
  public void initializeNumberColumn(String columnName, String property,
      EventHandler<CellEditEvent<U, Number>> eventHandler) {

    TableColumn<U, Number> column = new TableColumn<>();
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
  public void initializeStringColumn(String columnName, String property,
      EventHandler<CellEditEvent<U, String>> eventHandler) {
    TableColumn<U, String> column = new TableColumn<>();
    column.setCellValueFactory(new PropertyValueFactory<>(property));
    column.setCellFactory(TextFieldTableCell.forTableColumn());
    column.setOnEditCommit(eventHandler);

    column.setText(columnName);

    getColumns().add(column);
  }
}
