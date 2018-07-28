package drawer.context;

import javafx.scene.control.TitledPane;

public class PathTitledTab extends TitledPane {

	private static int index = 0;
	public final PathGroup keyPoints = new PathGroup();

	public PathTitledTab() {
		setText(String.format("Path %d", index++));


	}

	public PathGroup getKeyPoints() {
		return keyPoints;
	}

	public void clear() {
		keyPoints.clear();
	}
}
