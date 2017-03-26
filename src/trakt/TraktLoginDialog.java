package trakt;

import java.awt.Dimension;
import java.io.IOException;

import javax.swing.JDialog;

import org.json.JSONException;
import org.json.JSONObject;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import utils.AppProperties;

public class TraktLoginDialog extends JDialog {
	private static final long serialVersionUID = -2621415244884006921L;
	private static final Dimension _webFrameDimension = new Dimension(800, 800);
	
	private JFXPanel _webpanel = new JFXPanel();
	private WebView _webview;
	private static String _authPackage = null;

	private TraktLoginDialog() {		
		Platform.runLater(new Runnable() {
			@Override public void run() {				
				_webview = new WebView();
				_webview.getEngine().load("https://trakt.tv/oauth/authorize?response_type=code"
						+ "&client_id=" + TraktManager.CLIENT_ID + "&redirect_uri=" + TraktManager.REDIRECT_URI);
				
				_webpanel.setScene(new Scene(_webview));
				_webview.getEngine().setOnStatusChanged(new EventHandler<WebEvent<String>>() {
					@Override public void handle(WebEvent<String> event) {
						WebEngine engine = (WebEngine) event.getSource();
						String location = engine.getLocation();

						// Base Case
						if (location.startsWith(TraktManager.REDIRECT_URI)) {
							
							// Permission Granted
							if (location.indexOf("code=") != -1) {
								String code = location.substring(location.indexOf("code=") + 5);
								_authPackage = exchangeCodeForAuthToken(code);
							}
							
							// Close the Window
							_webview.getEngine().setOnStatusChanged(null);
							TraktLoginDialog.this.dispose();
						}
					}
				});			
			}
		});
		
		setSize(_webFrameDimension);
		setModal(true);
		add(_webpanel);
	}
	
	private static String exchangeCodeForAuthToken(String code) {
		try {
			JSONObject object = new JSONObject();
			object.put("code", code);
			object.put("client_id", TraktManager.CLIENT_ID);
			object.put("client_secret", TraktManager.CLIENT_SECRET);
			object.put("redirect_uri", TraktManager.REDIRECT_URI);
			object.put("grant_type", "authorization_code");
			
			return TraktSource.doPost("oauth/token", "0", object.toString());
		} catch (JSONException | IOException ignore) {
			return null;
		}
	}
	
	public static String getAuthorizationToken() {
		// Dialog is Modal -- So This Thread Blocks
		(new TraktLoginDialog()).setVisible(true);
		
		// Save out the Auth & Refresh Tokens
		try {
			JSONObject auth = new JSONObject(_authPackage);
			String authToken = auth.getString("access_token");
			String refreshToken = auth.getString("refresh_token");
			
			AppProperties.setTraktAccessCode(authToken);
			AppProperties.setTraktRefreshCode(refreshToken);
			AppProperties.commitChanges();
			return authToken;
		} catch (JSONException ignore) {
			return null;
		}
	}
}
