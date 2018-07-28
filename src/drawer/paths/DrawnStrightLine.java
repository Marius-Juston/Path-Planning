package drawer.paths;

import drawer.curves.ObservedDirectionalArrow;
import drawer.curves.PointAngleGroup;
import drawer.curves.PositionPoint;
import java.util.List;
import java.util.stream.Collectors;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import org.waltonrobotics.controller.Pose;
import org.waltonrobotics.motion.Line;

public class DrawnStrightLine extends DrawnPath {


	@Override
	public void clearCreateAndAddPoints(ObservableList<? extends PointAngleGroup> list) {
		getChildren().clear();
		System.out.println("Straight");

		if (list.size() == 2) {
			Line pointTurn = new Line(4, 2, 0, 0, false, list.get(0).getPose(), list.get(1).getPose());

			setPath(pointTurn);
			List<Pose> poses = extractPositionData(getPath().getPathData());
			double percentage = 255.0 / poses.size();

			final int[] index = {poses.size()};

			List<ObservedDirectionalArrow> pathPoints = poses.stream()
				.map(pose -> new ObservedDirectionalArrow(new PositionPoint(pose), -pose.getAngle(), 30, true,
					Color.rgb(255, (int) ((index[0]) * percentage), (int) ((index[0]--) * percentage))
//					Color.RED)
				))
				.collect(Collectors.toList());

			for (ObservedDirectionalArrow observedDirectionalArrow : pathPoints) {
				System.out.println(StrictMath.toDegrees(observedDirectionalArrow.getAngle()));
			}

			getChildren().addAll(pathPoints);
		}
	}
}
