package drawer.optimizer;

import calibration.Field;
import calibration.obstacle.AbstractObstacle;
import calibration.obstacle.FieldBorder;
import calibration.obstacle.ThreatLevel;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;

public class Mesher {

  private static final HashMap<AbstractObstacle, List<Point2D>> nodes = new HashMap<>();
  private static final Group GROUP = new Group();

  public static Group getGroup() {
    return GROUP;
  }

  public static void createMesh() {
    FieldBorder fieldBorder = Field.getInstance().getFieldBorder();
    List<AbstractObstacle> obstacles = Field.getInstance().getFieldObstacles();
    obstacles.remove(fieldBorder);

    HashMap<AbstractObstacle, List<Point2D>> nodes = new HashMap<>();
    nodes.clear();
    nodes.put(fieldBorder, getVertices(fieldBorder));
    nodes.putAll(getVertices(obstacles));
    obstacles.add(fieldBorder);

    Collection<? extends Line> closestPoint = findClosestPoint(nodes);

//    Shape[] abstractObstacleStream = Field.getInstance().getFieldObstacles().stream()
//        .filter(abstractObstacle -> abstractObstacle.getThreatLevel() == ThreatLevel.ERROR)
//        .map(AbstractObstacle::getDefiningShape).toArray(Shape[]::new);
//    closestPoint.stream().filter()
//
    GROUP.getChildren().addAll(closestPoint);

    nodes.clear();
    Mesher.nodes.putAll(nodes);
  }

  private static Collection<? extends Line> findClosestPoint(HashMap<AbstractObstacle, List<Point2D>> nodes) {
    Set<Line> nodeList = new LinkedHashSet<>();

    AbstractObstacle[] abstractObstacles = nodes.keySet().toArray(new AbstractObstacle[0]);
    for (int i = 0; i < abstractObstacles.length; i++) {

      for (Point2D point2D : nodes.get(abstractObstacles[i])) {

        Point2D closest = null;
        for (int y = 0; y < abstractObstacles.length; y++) {
          if (i != y) {
            for (Point2D otherPoint : nodes.get(abstractObstacles[y])) {
              if (closest == null) {
                closest = otherPoint;
              } else if (point2D.distance(closest) > point2D.distance(otherPoint)) {
                closest = otherPoint;
              }
            }
          }
        }

        nodeList.add(new Line(point2D, closest == null ? point2D : closest));
      }
    }
    return nodeList;
  }

  private static Map<? extends AbstractObstacle, ? extends List<Point2D>> getVertices(
      List<AbstractObstacle> obstacles) {

    HashMap<AbstractObstacle, List<Point2D>> obstacleListHashMap = new HashMap<>();

    for (AbstractObstacle obstacle : obstacles) {
      List<Point2D> point2DS = new LinkedList<>();
      ObservableList<Double> points = ((Polygon) obstacle.getDefiningShape()).getPoints();
      for (int i = 0; i < points.size(); i += 2) {
        point2DS.add(new Point2D(points.get(i), points.get(i + 1)));
      }

      obstacleListHashMap.put(obstacle, point2DS);
    }

    return obstacleListHashMap;
  }

  public static List<Point2D> getVertices(FieldBorder fieldBorder) {
    List<Point2D> obstacles = new LinkedList<>();

    for (PathElement pathElement : ((Path) fieldBorder.getDefiningShape()).getElements()) {
      if (pathElement instanceof MoveTo) {
        obstacles.add(new Point2D(((MoveTo) pathElement).getX(), ((MoveTo) pathElement).getY()));
      } else if (pathElement instanceof LineTo) {
        obstacles.add(new Point2D(((LineTo) pathElement).getX(), ((LineTo) pathElement).getY()));
      } else {
        break;
      }
    }
    return obstacles;
  }


  private static class Line extends javafx.scene.shape.Line {

    private final Point2D startPoint;
    private final Point2D endPoint;

    public Line(Point2D startPoint, Point2D endPoint) {
      super(startPoint.getX(), startPoint.getY(), endPoint.getX(), endPoint.getY());
      this.startPoint = startPoint;
      this.endPoint = endPoint;

      setStrokeWidth(1);
      setStroke(Color.RED);
    }

    @Override
    public int hashCode() {
      int result = 31 * endPoint.hashCode();
      result += (31 * startPoint.hashCode());

      return result;
    }
  }
}
