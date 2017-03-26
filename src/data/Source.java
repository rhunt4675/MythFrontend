package data;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;

import utils.AppProperties;

public abstract class Source {
	private enum HTTP_METHOD {GET, POST, PUT, DELETE, OPTIONS, HEAD, CONNECT};
	
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
			con.setConnectTimeout(5000);
			con.setReadTimeout(5000);
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
	
	public static String playback_url(String uri) {
		return get_base_url() + "/Content/GetRecording?RecordedId=" + uri;
	}
	
	public static void test_connection() throws IOException {
		try {
			http_do(get_base_url() + "/Status/xml", HTTP_METHOD.GET, true);
		} catch (IOException e) {
			throw e;
		}
	}
	
	protected static byte[] image_get(String uri, StringBuffer filename /* Out */) throws IOException {
		byte[] result = null;
		URL obj = new URL(get_base_url() + uri);
		
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		
		try {
			if (con.getResponseCode() == HttpURLConnection.HTTP_OK) { 
				InputStream in = con.getInputStream();
				ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
				
				int bytesRead; byte[] swap = new byte[8092];
				while ((bytesRead = in.read(swap, 0, swap.length)) != -1)
					byteBuffer.write(swap, 0, bytesRead);
				in.close();
				byteBuffer.flush();
				
				// Extract Content-Disposition Filename Header
				String header = con.getHeaderField("Content-Disposition");
				if (header != null) header = header.split("\"")[1];
				
				// Return Results
				result = byteBuffer.toByteArray();
				if (filename != null) {
					filename.setLength(0);
					filename.append(header);
				}
			} else {
				// Suppress Image Load Errors (like ImageIcon does)
			}
		} catch (SocketException e) {
			// Suppress Image Load Errors (like ImageIcon does)
		}
		
		return result;
	}
}
