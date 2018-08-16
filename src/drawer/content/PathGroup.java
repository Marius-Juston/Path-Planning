package drawer.content;

import drawer.curves.PointGroup;
import java.util.Collection;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;

public class PathGroup<K extends PointGroup> extends Group {

  private final ObservableList<K> keyPoints = FXCollections.observableArrayList(
      param -> new Observable[]{param.centerXProperty(),
          param.centerYProperty(), param.angleProperty()});

  public ObservableList<K> getKeyPoints() {
    return keyPoints;
  }


  public void add(K pointAngleCombo) {
    getChildren().add(pointAngleCombo);
    keyPoints.add(pointAngleCombo);
  }

  public void removeAll(Collection<K> selectedItems) {
    getChildren().removeAll(selectedItems);
    keyPoints.removeAll(selectedItems);
  }

  public void showAllPointDetails(Iterable<K> selectedItems) {
    for (K pointAngleGroup : selectedItems) {
      pointAngleGroup.setSelected(true);
    }
  }


  public void hideAllPointDetails(Iterable<K> selectedItems) {
    for (K pointAngleGroup : selectedItems) {
      pointAngleGroup.setSelected(false);
    }
  }

  public void clear() {
    keyPoints.clear();
    getChildren().clear();
  }
}
