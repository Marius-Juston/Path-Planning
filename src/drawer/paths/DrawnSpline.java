package drawer.paths;

import drawer.curves.ObservedDirectionalArrow;
import drawer.curves.PathPoint;
import drawer.curves.PointAngleGroup;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import org.waltonrobotics.controller.Pose;
import org.waltonrobotics.motion.Spline;

public class DrawnSpline extends DrawnPath {

	public void clearCreateAndAddPoints(ObservableList<? extends PointAngleGroup> list) {
		getChildren().clear();

		if (list.size() > 1) {
			System.out.println("Spline");
			List<Pose> poses = PointAngleGroup.mapToPoses(list);

			setPath(new Spline(4, 2, 0, 0, false, poses));
//			poses = extractPositionData(getPath().getPathData());

//			List<PathPoint> pathPoints = poses.stream().map(PathPoint::new).collect(Collectors.toList());
			double maxLength = 45;
			double everyPercent = 0.05;

			int every = (int) (getPath().getPathData().size() * everyPercent);

			final int[] index = {0};
//			List<Node> pathPoints = getPath().getPathData().stream().map(pathData -> {
//				if (index[0]++ % every == 0) {
//					double centerVelocity =
//						(pathData.getLeftState().getVelocity() + pathData.getRightState().getVelocity()) / 2.0;
//
//					return new ObservedDirectionalArrow(pathData.getCenterPose(),
//						(centerVelocity / getPath().getVCruise()) * maxLength,
//						Color.ORANGE);
//				} else {
//					return new PathPoint(pathData.getCenterPose());
//				}
//			}).collect(Collectors.toList());

			List<PathPoint> pathPoints = new ArrayList<>();
			List<ObservedDirectionalArrow> observedDirectionalArrows = new ArrayList<>();

			getPath().getPathData().forEach(pathData -> {
				if (index[0]++ % every == 0) {
					double centerVelocity =
						(pathData.getLeftState().getVelocity() + pathData.getRightState().getVelocity()) / 2.0;

					observedDirectionalArrows.add(new ObservedDirectionalArrow(pathData.getCenterPose(),
						(centerVelocity / getPath().getVCruise()) * maxLength,
						Color.ORANGE));
				} else {
					pathPoints.add(new PathPoint(pathData.getCenterPose()));
				}
			});

			getChildren().addAll(pathPoints);
			getChildren().addAll(observedDirectionalArrows);
		}
	}


}
