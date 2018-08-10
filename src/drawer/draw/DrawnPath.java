package drawer.draw;

import drawer.curves.PointAngleGroup;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import org.waltonrobotics.controller.PathData;
import org.waltonrobotics.controller.Pose;
import org.waltonrobotics.motion.Path;

//TODO make this an ENUM
public class DrawnPath extends Group {

	private Path drawingPath;
	private Path actualPath;
	private PathType pathType;
	private boolean showVelocities = true;

	public DrawnPath(PathType pathType) {

		this.pathType = pathType;
	}

	public static List<Pose> extractPositionData(Collection<PathData> pathDataList) {
		return pathDataList.stream().map(PathData::getCenterPose).collect(Collectors.toList());
	}

	public boolean isShowVelocities() {
		return showVelocities;
	}

	public void setShowVelocities(boolean showVelocities) {
		this.showVelocities = showVelocities;
	}

	public PathType getPathType() {
		return pathType;
	}

	public void setPathType(PathType pathType) {

		this.pathType = pathType;
	}

	public Path getDrawingPath() {
		return drawingPath;
	}

	public void setDrawingPath(Path drawingPath) {
		this.drawingPath = drawingPath;
	}

	private void clearCreateAndAddPoints(ObservableList<? extends PointAngleGroup> list) {
		pathType.clearCreateAndAddPoints(this, list);
	}

	public void draw(Change<? extends PointAngleGroup> c) {
		clearCreateAndAddPoints(c.getList());
	}

	public Path getActualPath() {
		return actualPath;
	}

	public void setActualPath(Path actualPath) {
		this.actualPath = actualPath;
	}
}
