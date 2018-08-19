package drawer;

import drawer.content.points.PointsPathTitledTab;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import org.waltonrobotics.motion.Path;

enum SplineSender {
  ;

  //	TODO make it so that you can change these and it will
//	public static final String IP_ADDRESS = "10.0.0.24"; //IP address to connect to when in server mode
  //	public static final String NETWORK_TABLE_TABLE_KEY = "SmartDashboard"; //network table to send the data to
  public static final String NETWORK_TABLE_TABLE_KEY = String
      .format("Path Planner - %s",
          System.getProperty("user.name")); //network table to send the data to
  public static final String SMARTDASHBOARD_NETWORKTABLE_KEY = "SmartDashboard";
  private static final int TEAM_NUMBER = 2974; // team number
  private static NetworkTable networkTable;
  private static boolean client; // if the program will send to robotRIO or not
  private static boolean hasBeenStarted;

  public static boolean isClient() {
    return client;
  }

  public static void setClient(boolean client) {

    NetworkTable.shutdown();

    if (client) {
      System.out.println("CLIENT MODE");
      NetworkTable.setClientMode();
      NetworkTable.setTeam(TEAM_NUMBER);
    } else {
      System.out.println("SERVER MODE");
      NetworkTable.setServerMode();
      NetworkTable.setIPAddress("localhost");
    }

    SplineSender.client = client;
  }

  /**
   * Initializes the network table with ip address settings etc...
   */
  private static void initNetworkTable() {

    if (!hasBeenStarted) {
      hasBeenStarted = true;

      setClient(true);

      networkTable = NetworkTable.getTable(NETWORK_TABLE_TABLE_KEY);
    }
  }

  public static void initNetworkTableParallel() {
    Thread thread = new Thread(SplineSender::initNetworkTable);
    thread.start();
  }

  public static void sendPath(PointsPathTitledTab titledPane) {
    sendPath(titledPane.getText(), titledPane.getPointsPathGroup().getDrawer().getActualPath());
  }

  private static void sendPath(String key, Path path) {
    networkTable.putString(key, path.convertToString());
  }

}
