package drawer.optimizer;

import calibration.Field;
import calibration.obstacle.AbstractObstacle;
import calibration.obstacle.FieldBorder;
import io.github.jdiemke.triangulation.DelaunayTriangulator;
import io.github.jdiemke.triangulation.NotEnoughPointsException;
import io.github.jdiemke.triangulation.Vector2D;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.Polygon;

public class Mesher {

  private static final Group GROUP = new Group();

  public static Group getGroup() {
    return GROUP;
  }

  public static void createMesh() {
    getGroup().getChildren().clear();
    FieldBorder fieldBorder = Field.getInstance().getFieldBorder();
    List<AbstractObstacle> obstacles = new LinkedList<>(Field.getInstance().getFieldObstacles());
    obstacles.remove(fieldBorder);

    List<Vector2D> vector2DS = new LinkedList<>();
    vector2DS.addAll(getVertices(fieldBorder));
    vector2DS.addAll(getVertices(obstacles));

    DelaunayTriangulator delaunayTriangulator = new DelaunayTriangulator(vector2DS);

    try {
      delaunayTriangulator.triangulate();
    } catch (NotEnoughPointsException e) {
      e.printStackTrace();
    }

    List<Polygon> collect = delaunayTriangulator.getTriangles().stream().map(triangle2D -> {
      Polygon polygon = new Polygon(
          triangle2D.a.x, triangle2D.a.y,
          triangle2D.b.x, triangle2D.b.y,
          triangle2D.c.x, triangle2D.c.y
      );

      polygon.setFill(Color.color(0, 0, 1, .1));
      polygon.setStroke(Color.RED);

      return polygon;
    }).collect(Collectors.toList());

    GROUP.getChildren().addAll(collect);
  }


  private static List<Vector2D> getVertices(
      List<AbstractObstacle> obstacles) {

    List<Vector2D> obstacleListHashMap = new LinkedList<>();

    for (AbstractObstacle obstacle : obstacles) {
      List<Vector2D> point2DS = new LinkedList<>();
      ObservableList<Double> points = ((Polygon) obstacle.getDefiningShape()).getPoints();
      for (int i = 0; i < points.size(); i += 2) {
        point2DS.add(new Vector2D(points.get(i), points.get(i + 1)));
      }

      obstacleListHashMap.addAll(point2DS);
    }

    return obstacleListHashMap;
  }

  public static List<Vector2D> getVertices(FieldBorder fieldBorder) {
    List<Vector2D> obstacles = new LinkedList<>();

    for (PathElement pathElement : ((Path) fieldBorder.getDefiningShape()).getElements()) {
      if (pathElement instanceof MoveTo) {
        obstacles.add(new Vector2D(((MoveTo) pathElement).getX(), ((MoveTo) pathElement).getY()));
      } else if (pathElement instanceof LineTo) {
        obstacles.add(new Vector2D(((LineTo) pathElement).getX(), ((LineTo) pathElement).getY()));
      } else {
        break;
      }
    }
    return obstacles;
  }
}
