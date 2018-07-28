package drawer.paths;

import drawer.curves.PointAngleGroup;
import java.util.List;
import java.util.stream.Collectors;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import org.waltonrobotics.controller.PathData;
import org.waltonrobotics.controller.Pose;
import org.waltonrobotics.motion.Path;

//TODO make this an ENUM
public abstract class DrawnPath extends Group {

	public Path path;

	public static List<Pose> extractPositionData(List<PathData> pathDataList) {
		return pathDataList.stream().map(PathData::getCenterPose).collect(Collectors.toList());
	}

	public Path getPath() {
		return path;
	}

	public void setPath(Path path) {
		this.path = path;
	}

	public abstract void clearCreateAndAddPoints(ObservableList<? extends PointAngleGroup> list);
}
