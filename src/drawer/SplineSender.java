package drawer;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class SplineSender {

	public static final String IP_ADDRESS = "10.0.0.24"; //IP address to connect to when in server mode
	public static final String NETWORK_TABLE_TABLE_KEY = "SmartDashboard"; //network table to send the data to
	private static final int TEAM_NUMBER = 2974; // team number
	private static final boolean IS_CLIENT = true; // if the program will send to robotRIO or not
	public static NetworkTable networkTable;

	private static boolean hasBeenStarted = false;

	/**
	 * Initializes the network table with ip address settings etc...
	 */
	public static void initNetworkTable() {
//        SettingController.addNumber("TEAM_NUMBER", 2974, NumberType.INTEGER);
//        SettingController.<Integer>getValue("TEAM_NUMBER").addListener((observable, oldValue, newValue) -> {
//            if (!newValue.equals(oldValue))
//            {
//                NetworkTable.setTeam(newValue);
//            }
//        });

		if (!hasBeenStarted) {
			System.out.println("Hello");

			hasBeenStarted = true;

			if (IS_CLIENT) {
				NetworkTable.setClientMode();
				NetworkTable.setTeam(TEAM_NUMBER);
			} else {
				NetworkTable.setServerMode();
				NetworkTable.setIPAddress(IP_ADDRESS);
			}

			networkTable = NetworkTable.getTable(NETWORK_TABLE_TABLE_KEY);
		}
	}


	public static void initNetworkTableParallel() {
		Thread thread = new Thread(SplineSender::initNetworkTable);
		thread.start();

	}
}
