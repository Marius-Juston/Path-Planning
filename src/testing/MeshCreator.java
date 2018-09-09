package testing;

import io.github.jdiemke.triangulation.DelaunayTriangulator;
import io.github.jdiemke.triangulation.NotEnoughPointsException;
import io.github.jdiemke.triangulation.Vector2D;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class MeshCreator implements Initializable {

  public AnchorPane anchorPane;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    List<Vector2D> point2DS = new LinkedList<>();
    point2DS.add(new Vector2D(10, 10));
    point2DS.add(new Vector2D(110, 110));
    point2DS.add(new Vector2D(55, 55));
    point2DS.add(new Vector2D(110, 10));
    point2DS.add(new Vector2D(10, 110));

//    anchorPane.getChildren()
//        .addAll(point2DS.stream().map(point2D -> new Circle(point2D.getX(), point2D.getY(), 2)).toArray(Circle[]::new));

//    float[] floatArray = new float[point2DS.size() * 3];
//    for (int i = 0; i < point2DS.size(); i += 3) {
//      floatArray[i] = (float) point2DS.get(i).getX();
//      floatArray[i + 1] = (float) point2DS.get(i).getY();
//      floatArray[i + 2] = 0f;
//    }

//    TriangleMesh triangleMesh = new TriangleMesh(VertexFormat.POINT_TEXCOORD);
//    triangleMesh.getTexCoords().addAll(0, 0);
//    triangleMesh.getPoints().addAll(floatArray);
//
//    triangleMesh.getFaces().addAll(
//        0, 0, 2, 0, 1, 0,          // Front left face
//        0, 0, 1, 0, 3, 0,          // Front right face
//        0, 0, 3, 0, 4, 0,          // Back right face
//        0, 0, 4, 0, 2, 0,          // Back left face
//        4, 0, 1, 0, 2, 0,          // Bottom rear face
//        4, 0, 3, 0, 1, 0           // Bottom front face
//    );
//
//    MeshView meshView = new MeshView(triangleMesh);
//    anchorPane.getChildren().add(meshView);

    DelaunayTriangulator delaunayTriangulator = new DelaunayTriangulator(point2DS);
    try {
      delaunayTriangulator.triangulate();
      List<Polygon> collect = delaunayTriangulator.getTriangles().stream().map(triangle2D -> {
        Polygon polygon = new Polygon(
            triangle2D.a.x, triangle2D.a.y,
            triangle2D.b.x, triangle2D.b.y,
            triangle2D.c.x, triangle2D.c.y
        );

        polygon.setFill(Color.color(0, 0, 1, .5));
        polygon.setStroke(Color.BLACK);

        return polygon;
      }).collect(Collectors.toList());

      anchorPane.getChildren().addAll(collect);

    } catch (NotEnoughPointsException e) {
      e.printStackTrace();
    }
  }
}
