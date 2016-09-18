package data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public abstract class Source {
	private static String _address;
	private static String _port;
	private static boolean _secure;
	
	protected static String get_version() {
		return "88";
	}
	
	protected static String get_base_url() {
		return "http" + (_secure ? "s" : "") + "://" + _address + ":" + _port;
	}
	
	protected static String http_get(String url) throws IOException {
		String result;
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestProperty("Accept", "text/javascript");
		con.setRequestMethod("GET");
		
		if (con.getResponseCode() == HttpURLConnection.HTTP_OK) { 
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			StringBuffer response = new StringBuffer();
			String inputLine;

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			result = response.toString();
		} else {
			throw new IOException("Server returned status " + con.getResponseCode());
		}
		
		return result;
	}
	
	protected static String http_post(String url) throws IOException {
		String result;
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestProperty("Accept", "text/javascript");
		con.setRequestMethod("POST");
		
		if (con.getResponseCode() == HttpURLConnection.HTTP_OK) { 
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			StringBuffer response = new StringBuffer();
			String inputLine;

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			result = response.toString();
		} else {
			throw new IOException("Server returned status " + con.getResponseCode());
		}
		
		return result;
	}
	
	public static void set_address(String address) {
		_address = address;
	}
	
	public static void set_port(String port) {
		_port = port;
	}
	
	public static void set_secure(boolean secure) {
		_secure = secure;
	}
}
