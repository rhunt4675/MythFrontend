package utils;

import java.util.prefs.Preferences;

import data.Source;

public class AppProperties {
	private static final String _node = "mythfrontend";
	private static Preferences _preferences = Preferences.userRoot().node(_node);

	public static void readAndUpdate() {
		apply();
	}
	
	public static void updateAndWrite() {
		apply();
	}
	
	public static void displayPropertiesWindow() {
		AppPropertiesDialog dialog = new AppPropertiesDialog();
		dialog.setVisible(true); // Modal
	}

	private static void apply() {
		// Set Data Source Properties
		Source.set_address(_preferences.get("Address", null));
		Source.set_port(_preferences.get("Port", null));
		Source.set_secure(new Boolean(_preferences.get("Secure", null)));
	}
	
	public static String getSourceAddress() {
		return _preferences.get("Address", null);
	}
	
	public static String getSourcePort() {
		return _preferences.get("Port", null);
	}
	
	public static boolean isSourceSecure() {
		return new Boolean(_preferences.get("Secure", null));
	}
	
	public static void setSourceAddress(String address) throws NumberFormatException {
		if (INetAddress.validateIPv4(address))
			_preferences.put("Address", address);
		else
			throw new NumberFormatException("Invalid IP Address: " + address);
	}
	
	public static void setSourcePort(String port) throws NumberFormatException {
		try {
			int parsed_port = Integer.parseInt(port);
			if (parsed_port > 0)
				_preferences.put("Port", port);
			else
				throw new NumberFormatException();
			
		} catch (NumberFormatException e) {
			throw new NumberFormatException("Invalid Port Number: " + port);
		}
	}
	
	public static void setSourceSecure(boolean secure) {
		_preferences.put("Secure", Boolean.toString(secure));
	}
}
