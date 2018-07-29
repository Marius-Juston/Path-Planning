package calibration;

import static calibration.Helper.PIXELS;
import static calibration.Helper.getImage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.WritableObjectValue;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javax.imageio.ImageIO;

public class Field {

	public static final SimpleDoubleProperty SCALE = new SimpleDoubleProperty(1);
	public static final WritableObjectValue<String> UNIT = new SimpleStringProperty(Helper.PIXELS);
	public static final String SUFFIX = ".field";
	//	public static BufferedImage bufferedImage;
	public static final String MATCH_PATTERN = "[0-9.]+ [a-zA-Z]+";
	private static final Alert useFieldValue = new Alert(AlertType.CONFIRMATION);
	public static File imageFile;
	public static Image image;

	static {
		useFieldValue
			.setContentText("This file already has field information inside of it do you wish to load it?");
	}

	public static Image loadData(File loadFile) throws IOException {
		Image image = getImage(loadFile);

		Field.imageFile = loadFile;
		Field.image = image;

		try (BufferedReader bufferedReader = new BufferedReader(
			new InputStreamReader(new FileInputStream(loadFile)))) {
			AtomicReference<String> lastLine = new AtomicReference<>();

			bufferedReader.lines().forEach(lastLine::set);
			lastLine.set(lastLine.get().trim());

			if (Pattern.matches(MATCH_PATTERN, lastLine.get())) {
				String[] data = lastLine.get().split("\\s");

				Field.SCALE.set(Double.parseDouble(data[0]));
				Field.UNIT.set(data[1]);
			} else {
				Field.SCALE.set(1.0);
				Field.UNIT.set(PIXELS);
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
				String splits = Field.imageFile.getAbsolutePath()
					.substring(Field.imageFile.getAbsolutePath().lastIndexOf('.') + 1);
				ImageIO.write(ImageIO.read(Field.imageFile), "jpg".equals(splits) ? "jpeg" : "png", saveFile);
			}

			try (BufferedWriter bufferedWriter = Files
				.newBufferedWriter(saveFile.toPath(), StandardOpenOption.APPEND)) {

				bufferedWriter.newLine();
				bufferedWriter.write(String.format("%f %s", Field.SCALE.get(), Field.UNIT.get()));
			}
		}
	}

	public static boolean isFieldFile(File file) {
		if (file.getName().endsWith(SUFFIX)) {
			Optional<ButtonType> buttonTypeOptional = useFieldValue.showAndWait();

			return buttonTypeOptional.isPresent() && buttonTypeOptional.get() == ButtonType.OK;
		}

		return false;
	}
}
