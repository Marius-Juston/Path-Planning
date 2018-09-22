package calibration;

import calibration.Helper.Unit;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Dialog to get the conversion between pixel distance and real length
 */
public class DistanceConverter implements Initializable {

  private static final Alert confirmation = new Alert(AlertType.CONFIRMATION,
      "Are you sure this is the correct conversion?", ButtonType.YES, ButtonType.NO);
  private static final Stage primaryStage = new Stage();
  private static Unit info;
  private static double pixelUnit;

  static {
//    Sets the modality of the window i.e you have to close this program in order to continue
    primaryStage.initModality(Modality.APPLICATION_MODAL);
    primaryStage.setTitle("Unit converter");
  }

  private Pattern unitCapture = Pattern.compile("(\\d+(?:[.\\d]+)?)\\s?([a-zA-Z]+)?");
  @FXML
  private TextField dataInput;
  @FXML
  private TextField pixelDistance;

  /**
   * Displays the {@link DistanceConverter} to make the user define what the scaling is. From pixels to actual units
   *
   * @param pixelUnit the pixel distance you want to find the scale of
   */
  static Unit display(double pixelUnit) throws IOException {
//    Resets the info
    info = null;
//    the pixel distance to display
    DistanceConverter.pixelUnit = pixelUnit;

//    Creates the dialog
    Parent root = FXMLLoader.load(DistanceConverter.class.getResource("conversion.fxml"));

//    Displays the dialog
    primaryStage.setScene(new Scene(root));
    primaryStage.showAndWait();

    return info;
  }

  @Override
  public final void initialize(URL location, ResourceBundle resources) {
//    Set the text to the pixel unit
    pixelDistance.setText(String.valueOf(pixelUnit));

//    Added a unit filter so that the text can only be in a certain format otherwise you cannot type it
    UnaryOperator<Change> objectUnaryOperator = change -> {
      String newText = change.getControlNewText();
      if (newText.matches("\\d+(\\.\\d*)?\\s?([A-Za-z]+)?")) {
        return change;
      }
      return null;
    };

    dataInput.setTextFormatter(new TextFormatter<>(objectUnaryOperator));
  }

  /**
   * Retrieve the information such as the actual distance and the unit from the textfield and
   */
  @FXML
  public void retrieveInfo(ActionEvent actionEvent) {
//    Gets the text from the text field
    TextField text = ((TextField) actionEvent.getSource());

//    If there is text
    if (!text.getText().isEmpty()) {
//      Match text to pattern
      Matcher matcher = unitCapture.matcher(text.getText());

//      If there is at least a number
      if (matcher.find() && matcher.groupCount() >= 1) {
//        Get the number and convert it to a double
        info = new Unit(Double.parseDouble(matcher.group(1)), Helper.PIXELS);

//        If the user placed units
        if (matcher.groupCount() >= 2) {
//          Get the units
          info.setUnit(matcher.group(2));
        }

//        Show confirmation dialog
        Optional<ButtonType> buttonTypeOptional = confirmation.showAndWait();
        if (buttonTypeOptional.isPresent()) {
//        If the user says yes then the DistanceConverter dialog will disappear
          if (buttonTypeOptional.get() == ButtonType.YES) {
            Helper.getWindow(actionEvent).hide();
          }
        }
      } else {
        text.setText(null);
      }
    }
  }
}