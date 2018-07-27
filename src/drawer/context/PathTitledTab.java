package drawer.context;

import java.io.IOException;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TitledPane;

public class PathTitledTab extends TitledPane {

	private static int index = 0;
	public final PathGroup keyPoints = new PathGroup();

	public PathTitledTab() {
		setText(String.format("Path %d", index++));

		MenuItem rename = new MenuItem("Rename");
		rename.setOnAction(event -> {
			try {
				this.setText(RenameDialog.display(this.getText()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

		ContextMenu contextMenu = new ContextMenu(rename);
		setContextMenu(contextMenu);

		setContent(new PathTable(keyPoints));
	}

	public PathGroup getKeyPoints() {
		return keyPoints;
	}
}
