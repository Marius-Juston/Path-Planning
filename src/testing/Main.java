package testing;

import calibration.Field;
import calibration.Helper;
import calibration.obstacle.FieldBorder;
import calibration.obstacle.Obstacle;
import calibration.obstacle.ThreatLevel;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javafx.application.Application;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;

public class Main extends Application {


  public Main() {
  }

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public final void start(Stage primaryStage) {

    try {
      File file = new File("./src/FRC 2018 Field Drawings.png");
      Field.getInstance().imageFile = file;
      Field.getInstance().image = Helper.getImage(file);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

    try {

      Field.getInstance().addObstacle(new Obstacle(ThreatLevel.ERROR, new Polygon(0, 0, 0, 12, 10, 0, 1201, 0)));
      Field.getInstance().addObstacle(new FieldBorder(new Path(new MoveTo(0, 0), new LineTo(3, 3))));
      System.out.println(Field.getInstance().getFieldBorder().getDefiningShape());

      Field.getInstance().saveData(new File("save.dat"));
      Field.getInstance().loadData(new File("save.dat"));

      primaryStage.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
//    Parent root = FXMLLoader
//        .load(getClass().getResource("../testing/recreationFieldSelection.fxml"));
//
//    primaryStage.setTitle("Path Planner");
//    primaryStage.setScene(new Scene(root));
//    primaryStage.show();
  }
}
