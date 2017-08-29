package utils;

import java.util.regex.Pattern;

public class INetAddress {
	public static boolean validateIPv4(String ip) {
		if (ip != null && ip.equals("localhost"))
			return true;

		String regex = "\\b((25[0–5]|2[0–4]\\d|[01]?\\d\\d?)(\\.)){3}(25[0–5]|2[0–4]\\d|[01]?\\d\\d?)\\b";
		return Pattern.matches(regex, ip);
	}
}
