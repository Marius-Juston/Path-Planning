package drawer.content;

import javafx.scene.control.TitledPane;

public class PathTitledTab<K extends PathGroup> extends TitledPane {

	private static int index = 0;
	public final K pointsPathGroup;

	public PathTitledTab(K pointsPathGroup) {
		this.pointsPathGroup = pointsPathGroup;
		setText(String.format("Path %d", index++));
	}

	public K getPointsPathGroup() {
		return pointsPathGroup;
	}

	public void clear() {
		pointsPathGroup.clear();
	}
}
