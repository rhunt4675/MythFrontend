package trakt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class TraktSource {	
	private enum HTTP_METHOD { GET, POST, PUT, DELETE; };
	
	public static String doGet(String uri, String authCode) throws IOException {
		return httpDo(HTTP_METHOD.GET, uri, authCode, "");
	}
	
	public static String doPost(String uri, String authCode, String postData) throws IOException {
		return httpDo(HTTP_METHOD.POST, uri, authCode, postData);
	}
	
	public static String doPut(String uri, String authCode, String putData) throws IOException {
		return httpDo(HTTP_METHOD.PUT, uri, authCode, putData);
	}
	
	public static String doDelete(String uri, String authCode) throws IOException {
		return httpDo(HTTP_METHOD.DELETE, uri, authCode, "");
	}
	
	private static String httpDo(HTTP_METHOD method, String uri, String authCode, String bodyContent) throws IOException {
		String result = null;
		
		// Build the URL
		URL obj;
		try {
			obj = new URL(TraktManager.BASE_URL + uri);
		} catch (MalformedURLException e) {
			e.printStackTrace(); return null;
		}
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// Set the Connection Protocol
		try {
			con.setRequestMethod(method.toString());
		} catch (ProtocolException e) {
			e.printStackTrace(); return result;
		}

		// Set Important Connection Headers
		con.setRequestProperty("Connection", "close");
		con.setRequestProperty("Content-Type", "application/json");
		con.setRequestProperty("trakt-api-key", TraktManager.CLIENT_ID);
		con.setRequestProperty("trakt-api-version", "2");
		if (authCode != null) con.setRequestProperty("Authorization", "Bearer " + authCode);
		
		// Special POST/PUT Data
		if ((method == HTTP_METHOD.POST || method == HTTP_METHOD.PUT) && bodyContent != null) {
			con.setDoOutput(true);
			con.setRequestProperty("Content-Length", String.valueOf(bodyContent.length()));
			con.getOutputStream().write(bodyContent.getBytes());
		}
		con.connect();
		 
		// Read Server Response
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		StringBuffer response = new StringBuffer();
		String inputLine;
		while ((inputLine = in.readLine()) != null)
			response.append(inputLine);
		in.close();
			
		result = response.toString();
		return result;
	}
}
