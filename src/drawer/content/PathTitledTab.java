package drawer.content;

import javafx.scene.control.TitledPane;

public abstract class PathTitledTab<K extends PathGroup> extends TitledPane {

  private static int index = 0;
  private final K pointsPathGroup;
  private PointsAdded pointNumber = PointsAdded.FIRST_POINT;

  public PathTitledTab(K pointsPathGroup) {
    this.pointsPathGroup = pointsPathGroup;
    setText(String.format("Path %d", index++));
  }

  public PointsAdded getPointNumber() {
    return pointNumber;
  }

  public void setPointNumber(PointsAdded pointNumber) {
    this.pointNumber = pointNumber;
  }

  public K getPointsPathGroup() {
    return pointsPathGroup;
  }

  public void clear() {
    pointsPathGroup.clear();
  }

  public enum PointsAdded {
    FIRST_POINT, SECOND_POINT, MORE
  }
}
