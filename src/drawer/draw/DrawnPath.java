package drawer.draw;

import drawer.curves.PointAngleGroup;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import org.waltonrobotics.controller.PathData;
import org.waltonrobotics.controller.Pose;
import org.waltonrobotics.motion.Path;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

//TODO make this an ENUM
public class DrawnPath extends Group {

    private final Group velocities;
    private final Group pathDefinition;
    private Path drawingPath;
    private Path actualPath;
    private PathType pathType;
    private SimpleBooleanProperty showVelocities = new SimpleBooleanProperty(true);

    public DrawnPath(PathType pathType) {

        this.pathType = pathType;
        velocities = new Group();
        pathDefinition = new Group();

        showVelocities.addListener((observable, oldValue, newValue) -> {

            if (newValue) {
                if (!getChildren().contains(velocities)) {
                    getChildren().add(velocities);
                }
            } else {
                getChildren().remove(velocities);
            }
        });

        getChildren().add(pathDefinition);
        getChildren().add(velocities);
    }

    public static List<Pose> extractPositionData(Collection<PathData> pathDataList) {
        return pathDataList.stream().map(PathData::getCenterPose).collect(Collectors.toList());
    }

    public boolean isShowVelocities() {
        return showVelocities.get();
    }

    public void setShowVelocities(boolean showVelocities) {
        this.showVelocities.set(showVelocities);
    }

    public void clearAllChildren() {
        clearVelocities();
        clearPathDefinition();
    }

    public void clearPathDefinition() {
        pathDefinition.getChildren().clear();
    }

    public Group getPathDefinition() {
        return pathDefinition;
    }

    public void clearVelocities() {
        velocities.getChildren().clear();
    }

    public SimpleBooleanProperty showVelocitiesProperty() {
        return showVelocities;
    }

    public Group getVelocities() {
        return velocities;
    }

    public PathType getPathType() {
        return pathType;
    }

    public void setPathType(PathType pathType) {

        this.pathType = pathType;
    }

    public Path getDrawingPath() {
        return drawingPath;
    }

    public void setDrawingPath(Path drawingPath) {
        this.drawingPath = drawingPath;
    }

    private void clearCreateAndAddPoints(ObservableList<? extends PointAngleGroup> list) {
        pathType.clearCreateAndAddPoints(this, list);
    }

    public void draw(Change<? extends PointAngleGroup> c) {
        clearCreateAndAddPoints(c.getList());
    }

    public Path getActualPath() {
        return actualPath;
    }

    public void setActualPath(Path actualPath) {
        this.actualPath = actualPath;
    }
}
