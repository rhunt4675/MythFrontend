package data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.ImageIcon;

import utils.AppProperties;

public abstract class Source {
	private enum HTTP_METHOD {GET, POST, PUT, DELETE, OPTIONS, HEAD, CONNECT};
	//private static String _address;
	//private static String _port;
	//private static boolean _secure;
	
	protected static String get_version() {
		return "88";
	}
	
	private static String get_base_url() {
		return "http" + (AppProperties.isSourceSecure() ? "s" : "") + "://" 
				+ AppProperties.getSourceAddress() + ":" + AppProperties.getSourcePort();
	}
	
	private static String http_do(String url, HTTP_METHOD method, boolean timeout) throws IOException {
		String result;
		URL obj = new URL(url);
		
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestProperty("Accept", "text/javascript");
		con.setRequestMethod(method.toString());
		
		if (timeout) {
			con.setConnectTimeout(2000);
			con.setReadTimeout(2000);
		}
		
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
	
	protected static String http_get(String uri) throws IOException {
		return http_do(get_base_url() + uri, HTTP_METHOD.GET, false);
	}
	
	protected static String http_post(String uri) throws IOException {
		return http_do(get_base_url() + uri, HTTP_METHOD.POST, false);
	}
	
	protected static ImageIcon image_get(String uri) throws MalformedURLException {
		return new ImageIcon(new URL(get_base_url() + uri));
	}
	
	protected static String playback_url(String uri) {
		return get_base_url() + uri;
	}
	
	public static void test_connection() throws IOException {
		try {
			http_do(get_base_url() + "/Status/xml", HTTP_METHOD.GET, true);
		} catch (IOException e) {
			throw e;
		}
	}
}
