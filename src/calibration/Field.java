package calibration;

import static calibration.Helper.PIXELS;
import static calibration.Helper.getImage;

import calibration.obstacle.AbstractObstacle;
import calibration.obstacle.FieldBorder;
import calibration.obstacle.Obstacle;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.WritableObjectValue;
import javafx.scene.Group;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javax.imageio.ImageIO;
import org.waltonrobotics.motion.Path;

public class Field {

  public static final double robotWidth = 0.8171; //TODO make it be set manually
  public static final SimpleDoubleProperty SCALE = new SimpleDoubleProperty(1);
  public static final WritableObjectValue<String> UNIT = new SimpleStringProperty(PIXELS);
  private static final Field instance = new Field();
  private static final String SUFFIX = ".field";
  //	public static BufferedImage bufferedImage;
  private static final String MATCH_PATTERN = "[0-9.]+ [a-zA-Z]+";
  private static final Alert useFieldValue = new Alert(AlertType.CONFIRMATION);
  private static final List<AbstractObstacle> fieldObstacles = new ArrayList<>();
  public static File imageFile;
  public static Image image;
  public static Group obstacleGroup = new Group();
  private static FieldBorder fieldBorder = null;

  static {
    useFieldValue
        .setContentText(
            "This file already has field information inside of it do you wish to load it?");

    Path.setRobotWidth(robotWidth);
  }
  private Field() {

  }

  public static Field getInstance() {
    return instance;
  }

  public static FieldBorder getFieldBorder() {
    return fieldBorder;
  }

  public static List<AbstractObstacle> getFieldObstacles() {
    return fieldObstacles;
  }

  public static Image loadData(File loadFile) throws IOException, java.io.FileNotFoundException {
    Image image = getImage(loadFile);

    imageFile = loadFile;
    Field.getInstance().image = image;

    try (BufferedReader bufferedReader = new BufferedReader(
        new InputStreamReader(new FileInputStream(loadFile), StandardCharsets.UTF_8))) {
      AtomicReference<String> lastLine = new AtomicReference<>();

      bufferedReader.lines().forEach(lastLine::set);
      lastLine.set(lastLine.get().trim());

      if (Pattern.matches(MATCH_PATTERN, lastLine.get())) {
        String[] data = lastLine.get().split("\\s");

        SCALE.set(Double.parseDouble(data[0]));
        UNIT.set(data[1]);
      } else {
        SCALE.set(1.0);
        UNIT.set(PIXELS);
      }

    }

    return image;
  }

  public static void saveData(File saveFile) throws IOException {
    if (saveFile.exists()) {
      System.out.println((saveFile.delete() ? "M" : "Did not m") + "anage to delete the file");
    }
    if (saveFile.createNewFile()) {

      {
        String splits = imageFile.getAbsolutePath()
            .substring(imageFile.getAbsolutePath().lastIndexOf('.') + 1);
        ImageIO.write(ImageIO.read(imageFile), "jpg".equals(splits) ? "jpeg" : "png", saveFile);
      }

      try (BufferedWriter bufferedWriter = Files
          .newBufferedWriter(saveFile.toPath(), StandardOpenOption.APPEND)) {

        bufferedWriter.newLine();
        bufferedWriter.write(String.format("%f %s", SCALE.get(), UNIT.get()));
      }
    }
  }

  public static boolean isFieldFile(File file) {
    if (file.getName().endsWith(SUFFIX)) {
      Optional<ButtonType> buttonTypeOptional = useFieldValue.showAndWait();

      return buttonTypeOptional.isPresent() && (buttonTypeOptional.get() == ButtonType.OK);
    }

    return false;
  }

  public static void addObstacle(Obstacle obstacle) {
    fieldObstacles.add(obstacle);
    obstacleGroup.getChildren().add(obstacle);

  }

  public static void addObstacle(FieldBorder obstacle) {

    if (fieldBorder != null) {
      fieldObstacles.remove(fieldBorder);
      obstacleGroup.getChildren().remove(fieldBorder);
    }

    fieldObstacles.add(obstacle);
    obstacleGroup.getChildren().add(obstacle);
    fieldBorder = obstacle;
  }

}