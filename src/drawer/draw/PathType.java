package drawer.draw;

import drawer.curves.ObservedDirectionalArrow;
import drawer.curves.PathPoint;
import drawer.curves.PointAngleGroup;
import drawer.curves.PositionPoint;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import org.waltonrobotics.controller.Pose;
import org.waltonrobotics.motion.Line;
import org.waltonrobotics.motion.PointTurn;
import org.waltonrobotics.motion.Spline;

//import org.waltonrobotics.motion.Line;

public enum PathType {
	//	TODO make it so that you do not recreate the points every time reuse the points but just add, remove and reposition them
	SPLINE {
		@Override
		public void clearCreateAndAddPoints(DrawnPath group, ObservableList<? extends PointAngleGroup> keyPoints) {
			super.clearCreateAndAddPoints(group, keyPoints);

			group.getChildren().clear();

			if (keyPoints.size() > 1) {
				System.out.println("Spline");
				List<Pose> poses = PointAngleGroup.mapToPoses(keyPoints);

				group.setPath(new Spline(4, 2, 0, 0, false, poses));
//			poses = extractPositionData(getPath().getPathData());

				if (group.isShowVelocities()) {
//			List<PathPoint> pathPoints = poses.stream().map(PathPoint::new).collect(Collectors.toList());
					double maxLength = 45;
					double everyPercent = 0.05;

					int every = (int) (group.getPath().getPathData().size() * everyPercent);

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

					group.getPath().getPathData().forEach(pathData -> {
						if (index[0]++ % every == 0) {
							double centerVelocity =
								(pathData.getLeftState().getVelocity() + pathData.getRightState().getVelocity()) / 2.0;

							observedDirectionalArrows.add(new ObservedDirectionalArrow(pathData.getCenterPose(),
								(centerVelocity / group.getPath().getVCruise()) * maxLength,
								Color.ORANGE));
						} else {
							pathPoints.add(new PathPoint(pathData.getCenterPose()));
						}
					});

					group.getChildren().addAll(pathPoints);
					group.getChildren().addAll(observedDirectionalArrows);
				} else {

					List<PathPoint> pathPoints = DrawnPath.extractPositionData(group.getPath().getPathData()).stream()
						.map(PathPoint::new)
						.collect(Collectors.toList());

					group.getChildren().addAll(pathPoints);
				}
			}
		}
	}, POINT_TURN {
		@Override
		public void clearCreateAndAddPoints(DrawnPath group, ObservableList<? extends PointAngleGroup> keyPoints) {
			super.clearCreateAndAddPoints(group, keyPoints);

			group.getChildren().clear();
			System.out.println("Turn");

			if (keyPoints.size() == 2) {
				PointTurn pointTurn = new PointTurn(4, 2, keyPoints.get(0).getPose(),
					keyPoints.get(1).getPose().getAngle());

				group.setPath(pointTurn);
				List<Pose> poses = DrawnPath.extractPositionData(group.getPath().getPathData());
				double percentage = 255.0 / poses.size();

				final int[] index = {poses.size()};

				List<ObservedDirectionalArrow> pathPoints = poses.stream()
					.map(pose -> new ObservedDirectionalArrow(new PositionPoint(pose), -pose.getAngle(), 30, true,
						Color.rgb(255, (int) ((index[0]) * percentage), (int) ((index[0]--) * percentage))
//					Color.RED)
					))
					.collect(Collectors.toList());
//
//				for (ObservedDirectionalArrow observedDirectionalArrow : pathPoints) {
//					System.out.println(StrictMath.toDegrees(observedDirectionalArrow.getAngle()));
//				}

				group.getChildren().addAll(pathPoints);
			}
		}
	}, STRAIGHT_LINE {
		//		TODO make this work
		@Override
		public void clearCreateAndAddPoints(DrawnPath group, ObservableList<? extends PointAngleGroup> keyPoints) {
			super.clearCreateAndAddPoints(group, keyPoints);

			group.getChildren().clear();

			if (keyPoints.size() == 2) {
				Line pointTurn = new Line(4, 2, 0, 0, false, keyPoints.get(0).getPose(), keyPoints.get(1).getPose());

				group.setPath(pointTurn);
				List<Pose> poses = DrawnPath.extractPositionData(group.getPath().getPathData());
				double percentage = 255.0 / poses.size();

				final int[] index = {poses.size()};

				List<PathPoint> pathPoints = poses.stream()
					.map(pose -> new PathPoint(pose,
						Color.rgb(255, (int) ((index[0]) * percentage), (int) ((index[0]--) * percentage))))
					.collect(Collectors.toList());

//				for (ObservedDirectionalArrow observedDirectionalArrow : pathPoints) {
//					System.out.println(StrictMath.toDegrees(observedDirectionalArrow.getAngle()));
//				}

				group.getChildren().addAll(pathPoints);
			}
			System.out.println("Straight");
		}
	};

	public void clearCreateAndAddPoints(DrawnPath group, ObservableList<? extends PointAngleGroup> keyPoints) {
	}
}
