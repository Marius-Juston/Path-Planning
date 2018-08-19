package testing;

import calibration.obstacle.FieldBorder;
import javafx.application.Application;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.stage.Stage;

public class Main extends Application {


  public Main() {
  }

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public final void start(Stage primaryStage) {

    FieldBorder fieldBorder = new FieldBorder(new Path(new MoveTo(0, 0), new LineTo(3, 3)));

//    Parent root = FXMLLoader
//        .load(getClass().getResource("../testing/recreationFieldSelection.fxml"));
//
//    primaryStage.setTitle("Path Planner");
//    primaryStage.setScene(new Scene(root));
//    primaryStage.show();
  }
}
