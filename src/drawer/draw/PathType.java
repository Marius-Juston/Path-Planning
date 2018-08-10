package drawer.draw;

import calibration.Field;
import calibration.Obstacle;
import drawer.content.NotificationArrow;
import drawer.curves.ObservedDirectionalArrow;
import drawer.curves.PathPoint;
import drawer.curves.PointAngleGroup;
import drawer.curves.PositionPoint;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.paint.Color;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;
import org.waltonrobotics.controller.PathData;
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

			System.out.println(org.waltonrobotics.motion.Path.getRobotWidth());

			group.getChildren().clear();

			if (keyPoints.size() > 1) {
				System.out.println("Spline");

				List<Pose> poses = PointAngleGroup.mapToPoses(keyPoints);

				Spline drawingSpline = new Spline(1, 1, 0, 0, false, poses);

				System.out.println(drawingSpline.getVCruise() + "\t\t" + drawingSpline.getAMax());

//				TODO improve this to be more efficient
				poses = PointAngleGroup.mapToRealPoses(keyPoints);
				System.out.println(poses);

				Spline actualSpline = new Spline(1, 1, 0, 0, false, poses);

				System.out.println(actualSpline.getPathData().getLast());
				System.out.println(actualSpline.getPathData().getFirst());

				group.setDrawingPath(drawingSpline);
				group.setActualPath(actualSpline);

				{
//					List<PathPoint> pathPoints = DrawnPath.extractPositionData(group.getDrawingPath().getPathData()).stream()
//						.map(PathPoint::new)
//						.collect(Collectors.toList());
//
//					group.getChildren().addAll(pathPoints);
//                  TODO optimize this
					List<CubicCurve> cubicCurves = ((Spline) group.getDrawingPath()).getDefiningBezierCurves().stream()
						.map(pathData -> {
							Pose p1 = pathData.getKeyPoints().get(0);
							Pose p2 = pathData.getKeyPoints().get(1);
							Pose p3 = pathData.getKeyPoints().get(2);
							Pose p4 = pathData.getKeyPoints().get(3);

							CubicCurve cubicCurve = new CubicCurve(p1.getX(), p1.getY(), p2.getX(), p2.getY(),
								p3.getX(), p3.getY(), p4.getX(), p4.getY());

							cubicCurve.setFill(null);
							cubicCurve.setStroke(Color.RED);
							cubicCurve.setStrokeWidth(3);

							return cubicCurve;
						}).collect(Collectors.toList());

					List<Path> intersections = new ArrayList<>();
					List<NotificationArrow> notificationArrows = new ArrayList<>();

					cubicCurves.forEach(cubicCurve ->
					{
						for (Obstacle obstacle : Field.getFieldObstacles()) {

							cubicCurve.setStrokeWidth(Field.robotWidth / Field.SCALE.get());
							Path intersection = (Path) Shape.intersect(obstacle.getDefiningShape(), cubicCurve);
							cubicCurve.setStrokeWidth(3);

							if (!intersection.getElements().isEmpty()) {

//							TODO fix this
								intersection.setFill(obstacle.getThreatLevel().getOverlayColor());
								intersections.add(intersection);

								Bounds layoutBounds = intersection.getLayoutBounds();

								double x = (layoutBounds.getMinX() + layoutBounds.getMaxX()) / 2.0;
								double y = (layoutBounds.getMinY() + layoutBounds.getMaxY()) / 2.0;

								NotificationArrow notificationArrow = new NotificationArrow(x, y,
									obstacle.getThreatLevel().getMessage());

								notificationArrows.add(notificationArrow);

							}
						}
					});

					group.getChildren().addAll(intersections);
					group.getChildren().addAll(cubicCurves);
					group.getChildren().addAll(notificationArrows);
				}

				if (group.isShowVelocities()) {
					double maxLength = 20;
					double everyPercent = 0.05;

					int every = (int) (group.getDrawingPath().getPathData().size() * everyPercent);

					int[] index = {0};
//			List<Node> pathPoints = getDrawingPath().getPathData().stream().map(pathData -> {
//				if (index[0]++ % every == 0) {
//					double centerVelocity =
//						(pathData.getLeftState().getVelocity() + pathData.getRightState().getVelocity()) / 2.0;
//
//					return new ObservedDirectionalArrow(pathData.getCenterPose(),
//						(centerVelocity / getDrawingPath().getVCruise()) * maxLength,
//						Color.ORANGE);
//				} else {
//					return new PathPoint(pathData.getCenterPose());
//				}
//			}).collect(Collectors.toList());
					Collection<PathPoint> pathPoints = new ArrayList<>();
					Collection<ObservedDirectionalArrow> observedDirectionalArrows = new ArrayList<>();

					group.getDrawingPath().getPathData().forEach(pathData -> {
						PathData velocities = group.getActualPath().getPathData().get(index[0]);

						if ((index[0]++ % every) == 0) {

							System.out.println(
								velocities.getLeftState().getLength() + "\t\t" + velocities.getRightState()
									.getLength());
							System.out.println(
								velocities.getLeftState().getVelocity() + "\t\t" + velocities.getRightState()
									.getVelocity());
							System.out.println();

							double centerVelocity =
								(velocities.getLeftState().getVelocity() + velocities.getRightState().getVelocity())
									/ 2.0;

							observedDirectionalArrows.add(new ObservedDirectionalArrow(pathData.getCenterPose(),
								(centerVelocity / group.getActualPath().getVCruise()) * maxLength,
								Color.ORANGE));

							double angle = pathData.getCenterPose().getAngle() + (StrictMath.PI / 2);
							double robotWidth = ((Field.robotWidth / 2) / Field.SCALE.get());

//							double robotWidth = 100;

//							observedDirectionalArrows.add(new ObservedDirectionalArrow(
//								pathData.getCenterPose()
//									.offset(-StrictMath.cos(angle) * robotWidth, -StrictMath.sin(angle) * robotWidth,
//										0.0),
//								(velocities.getLeftState().getVelocity() / group.getActualPath().getVCruise())
//									* maxLength,
//								Color.RED));
//
//							observedDirectionalArrows.add(new ObservedDirectionalArrow(
//								pathData.getCenterPose()
//									.offset(StrictMath.cos(angle) * robotWidth, StrictMath.sin(angle) * robotWidth,
//										0.0),
//								(velocities.getRightState().getVelocity() / group.getActualPath().getVCruise())
//									* maxLength,
//								Color.BLUE));

							observedDirectionalArrows.add(new ObservedDirectionalArrow(
								pathData.getCenterPose()
									.offset(-StrictMath.cos(angle) * robotWidth, -StrictMath.sin(angle) * robotWidth,
										0.0),
								(velocities.getLeftState().getVelocity() / group.getActualPath().getVCruise())
									* maxLength,
								Color.RED, false));

							observedDirectionalArrows.add(new ObservedDirectionalArrow(
								pathData.getCenterPose()
									.offset(StrictMath.cos(angle) * robotWidth, StrictMath.sin(angle) * robotWidth,
										0.0),
								(velocities.getRightState().getVelocity() / group.getActualPath().getVCruise())
									* maxLength,
								Color.BLUE, false));

						} else {
							pathPoints.add(new PathPoint(pathData.getCenterPose()));
						}
					});

					group.getChildren().addAll(pathPoints);
					group.getChildren().addAll(observedDirectionalArrows);
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

				group.setDrawingPath(pointTurn);
				List<Pose> poses = DrawnPath.extractPositionData(group.getDrawingPath().getPathData());
				double percentage = 255.0 / poses.size();

				int[] index = {poses.size()};

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

				group.setDrawingPath(pointTurn);
				List<Pose> poses = DrawnPath.extractPositionData(group.getDrawingPath().getPathData());
				double percentage = 255.0 / poses.size();

				int[] index = {poses.size()};

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
