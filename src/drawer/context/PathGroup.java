package drawer.context;

import drawer.curves.PointAngleGroup;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;

public class PathGroup extends Group {

	private final ObservableList<PointAngleGroup> keyPoints = FXCollections.observableArrayList();

	public ObservableList<PointAngleGroup> getKeyPoints() {
		return keyPoints;
	}


	public void add(PointAngleGroup pointAngleCombo) {
		getChildren().add(pointAngleCombo);
		keyPoints.add(pointAngleCombo);
	}

	public void removeAll(ObservableList<PointAngleGroup> selectedItems) {
		getChildren().removeAll(selectedItems);
		keyPoints.removeAll(selectedItems);
	}
}
