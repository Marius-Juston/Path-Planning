package calibration.obstacle;

import javafx.scene.paint.Color;

/**
 * Danger level of the obstacles
 */
public enum ThreatLevel {
  /**
   * The robot could bump into the obstacle and it might offset its course but it is still passable an example is a box
   * on the floor
   */
  WARNING(Color.rgb(0, 0, 255, 0.5), Color.ORANGE,
      "Warning the robot is passing on a dangerous place"),
  /**
   * An example of this threat would be a wall. The robot is unable to pass this obstacle in any way.
   */
  ERROR(
      Color.gray(0.5, 0.6), Color.RED.darker(),
      "Error given the field constrains the robot would not be able to go here");

  private final Color displayColor;
  private final Color overlayColor;
  private final String message;

  /**
   * @param displayColor color of the obstacle
   * @param overlayColor color of the overlapping robot path and the obstacle. Color of the intersection between the
   * robot path and the obstacle
   * @param message Message to be displayed by the {@link drawer.curves.figures.NotificationArrow}
   */
  ThreatLevel(Color displayColor, Color overlayColor, String message) {
    this.displayColor = displayColor;

    this.overlayColor = overlayColor;
    this.message = message;
  }

  public Color getOverlayColor() {
    return overlayColor;
  }

  public Color getDisplayColor() {
    return displayColor;
  }

  public String getMessage() {
    return message;
  }
}
