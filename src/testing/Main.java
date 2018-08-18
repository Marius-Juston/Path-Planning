package testing;

import calibration.Field;
import com.google.gson.Gson;
import java.io.IOException;
import javafx.application.Application;
import javafx.stage.Stage;
import org.hildan.fxgson.FxGson;

public class Main extends Application {


  public Main() {
  }

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public final void start(Stage primaryStage) throws IOException {

    Gson fxGsonWithExtras = FxGson.createWithExtras();
    String s = fxGsonWithExtras.toJson(Field.getInstance());
    System.out.println(s);

//    Parent root = FXMLLoader
//        .load(getClass().getResource("../testing/recreationFieldSelection.fxml"));
//
//    primaryStage.setTitle("Path Planner");
//    primaryStage.setScene(new Scene(root));
//    primaryStage.show();
  }
}
