package trakt;

import java.io.IOException;

import javax.swing.JOptionPane;

import org.json.JSONException;
import org.json.JSONObject;

import data.Recording;
import utils.AppProperties;

public class TraktManager {
	public static final String BUILD_DATE = "2017-02-25";
	public static final String BUILD_VERSION = "1.0";
	
	public static final String BASE_URL = "https://api.trakt.tv/";
	public static final String REDIRECT_URI = "https://api.trakt.tv/";
	
	public static final String CLIENT_ID = "b9a03e552dc3c14a03928740f146f00b45d222401104da830eaded5711925878";
	public static final String CLIENT_SECRET = "45e36696a92f498121cd90b635e2531ea81df3e2bd10d36c1ccc6cbf7fcd408b";

	private static String _code = null;
	
	public static void notifyPlay(Recording r) {
		// Acquire Prerequisites
		String code = getCode();
		if (code == null) return;
		Long episodeId = r.get_trakt_episodeId(code);
		if (episodeId == null) return;
		
		// Scrobble
		try {
			JSONObject payload = new JSONObject();
			payload.put("app_date", BUILD_DATE);
			payload.put("app_version", BUILD_VERSION);
			payload.put("progress", 0f);
			payload.put("episode", (new JSONObject()).put("ids", (new JSONObject()).put("trakt", episodeId)));
			TraktSource.doPost("scrobble/start", code, payload.toString());
		} catch (JSONException | IOException e) {
			System.err.println("Scrobble Failed: " + e.getMessage());
		}
	}
	
	public static void notifyStop(Recording r, float progress) {
		// Acquire Prerequisites
		String code = getCode();
		if (code == null) return;
		Long episodeId = r.get_trakt_episodeId(code);
		if (episodeId == null) return;
		
		// Scrobble
		try {
			JSONObject payload = new JSONObject();
			payload.put("app_date", BUILD_DATE);
			payload.put("app_version", BUILD_VERSION);
			payload.put("progress", progress);
			payload.put("episode", (new JSONObject()).put("ids",  (new JSONObject()).put("trakt", episodeId)));
			TraktSource.doPost("scrobble/stop", code, payload.toString());
		} catch (JSONException | IOException e) {
			System.err.println("Scrobble Failed: " + e.getMessage());
		}
	}
	
	private static String getCode() {
		// Case 1: We already have the code from an earlier request.
		if (_code != null)
			return _code;
		
		// Case 2: Java Preferences contains the API code.
		String code = AppProperties.getTraktAccessCode();
		if (code != null && !code.isEmpty() && loginSucceeded(code)) {
			_code = code; 
			return _code;
		}
		
		// Case 3: Prompt User for Manual Authorization.
		int dialogResult = JOptionPane.showConfirmDialog(null, "Would you like to scrobble watch data to Trakt.TV?", 
				"Scrobble to Trakt.TV", JOptionPane.YES_NO_OPTION);
		if (dialogResult == JOptionPane.YES_OPTION) {
			_code = TraktLoginDialog.getAuthorizationToken();
			return _code;
		}
		
		// Case 4: Trakt.TV Scrobbling Disabled
		return null;
	}
	
	private static boolean loginSucceeded(String code) {
		try {
			TraktSource.doGet("sync/last_activities", code);
			
			// Login Succeeded
			return true;
		} catch (IOException e) {
			// Login Failed (or some other error occurred)
			return false;
		}
	}
}
