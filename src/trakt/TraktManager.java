package trakt;

import data.Recording;
import data.Title;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import utils.AppProperties;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TraktManager {
	private static final String BUILD_DATE = "2017-02-25";
	private static final String BUILD_VERSION = "1.0";
	
	public static final String BASE_URL = "https://api.trakt.tv/";
	public static final String REDIRECT_URI = "https://api.trakt.tv/";

	public static final String CLIENT_ID = "b9a03e552dc3c14a03928740f146f00b45d222401104da830eaded5711925878";
	public static final String CLIENT_SECRET = "45e36696a92f498121cd90b635e2531ea81df3e2bd10d36c1ccc6cbf7fcd408b";
	private static final Logger LOGGER = Logger.getLogger(TraktManager.class.getName());

	private static String _code = null;

	/*public static boolean isEpisodeWatched(Recording recording) {
		// Acquire Prerequisites
		String code = getCode(false);
		if (code == null) return false;
		Long episodeId = recording.get_trakt_episodeId(code);
		if (episodeId == null) return false;

		// Ask Trakt
		try {
			String result = TraktSource.doGet("sync/history/episodes/" + episodeId, code);
			JSONArray response = new JSONArray(result);

			if (response.length() != 0)
				return true;
		} catch (IOException | JSONException e) {
			LOGGER.log(Level.SEVERE, "Get Episode History Failed: " + e.getMessage(), e);
		}

		return false;
	}*/

	public static Map<Integer, Set<Integer>> getSeasonProgress(Title title) {
		// Acquire Prerequisites
		Map<Integer, Set<Integer>> result = new HashMap<>();
		String code = getCode(false);
		if (code == null) return result;
		Long traktId = getShowTraktId(title.get_title(), code);
		if (traktId == null) return result;

		// Ask Trakt
		try {
			String rawResponse = TraktSource.doGet("sync/history/shows/" + traktId + "?limit=1000", code);
			JSONArray response = new JSONArray(rawResponse);
			for (int i = 0; i < response.length(); i++) {
				int season = response.getJSONObject(i).getJSONObject("episode").getInt("season");
				int episode = response.getJSONObject(i).getJSONObject("episode").getInt("number");
				if (!result.containsKey(season))
					result.put(season, new HashSet<>());
				result.get(season).add(episode);
			}
		} catch (IOException | JSONException e) {
			LOGGER.log(Level.SEVERE, "Get Show History Failed: " + e.getMessage(), e);
		}
		return result;
	}

	private static Long getShowTraktId(String title, String code) {
		// Ask Trakt
		try {
			String result = TraktSource.doGet("search/show?query=" + URLEncoder.encode(title, "UTF-8") + "&limit=1000", code);
			JSONArray response = new JSONArray(result);
			for (int i = 0; i < response.length(); i++) // Sorted by Likelihood
				if (((JSONObject) response.get(i)).getString("type").equals("show"))
					return ((JSONObject) response.get(i)).getJSONObject("show").getJSONObject("ids").getLong("trakt");
		} catch (IOException | JSONException e) {
			LOGGER.log(Level.SEVERE, "Get Show ID Failed: " + e.getMessage(), e);
		}

		// Failure
		return null;
	}
	
	public static void notifyPlay(Recording r) {
		// Acquire Prerequisites
		String code = getCode(false);
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
			LOGGER.log(Level.SEVERE, "Scrobble Failed: " + e.getMessage(), e);
		}
	}
	
	public static void notifyStop(Recording r, float progress) {
		// Acquire Prerequisites
		String code = getCode(true);
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
			LOGGER.log(Level.SEVERE, "Scrobble Failed: " + e.getMessage(), e);
		}
	}
	
	private static String getCode(boolean importantRequest) {
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
		if (importantRequest && shouldPromptForTraktLogin()) {
			promptForTraktLogin();
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

	public static void promptForTraktLogin() {
		// Check if Already Logged In
		_code = AppProperties.getTraktAccessCode();
		if (loginSucceeded(_code)) {
			JOptionPane.showMessageDialog(null, "Already logged in to Trakt.tv",
				"Trakt Login", JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		_code = TraktLoginDialog.getAuthorizationToken();
	}

	private static boolean shouldPromptForTraktLogin() {
		boolean promptForLogin = AppProperties.getTraktPromptLogin();
		if (!promptForLogin) return false;

		AtomicBoolean wantsLogin = new AtomicBoolean();
		AtomicBoolean dontAskAgain = new AtomicBoolean();
		class TraktPromptDialog extends JDialog implements ActionListener {
			private JCheckBox _dontAskAgain;
			private JButton _yes, _no;

			private TraktPromptDialog() {
				super(null, "Scrobble to Trakt.TV", Dialog.ModalityType.DOCUMENT_MODAL);
				setDefaultCloseOperation(DISPOSE_ON_CLOSE);

				_yes = new JButton("Yes");
				_yes.addActionListener(this);
				_yes.setMnemonic(KeyEvent.VK_O);
				_yes.setMinimumSize(new Dimension(75, 0));
				_no = new JButton("No");
				_no.addActionListener(this);
				_no.setMnemonic(KeyEvent.VK_C);
				_no.setMinimumSize(new Dimension(75, 0));
				_dontAskAgain = new JCheckBox();
				_dontAskAgain.setSelected(false);
				_dontAskAgain.setText("Don't ask me again");

				GroupLayout layout = new GroupLayout(getContentPane());
				getContentPane().setLayout(layout);

				JLabel title = new JLabel("Would you like to scrobble watch data to Trakt.TV?");

				layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(title)
						.addGroup(layout.createSequentialGroup()
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
										.addComponent(_dontAskAgain)
										.addGroup(layout.createSequentialGroup()
												.addComponent(_no)
												.addComponent(_yes)))));

				layout.setVerticalGroup(
						layout.createSequentialGroup()
								.addComponent(title)
								.addComponent(_dontAskAgain)
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addComponent(_no).addComponent(_yes)));

				layout.setAutoCreateContainerGaps(true);
				layout.setAutoCreateGaps(true);
				pack();

				setLocationRelativeTo(null);
				setResizable(false);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == _yes) {
					wantsLogin.set(true);
				} else if (e.getSource() == _no) {
					wantsLogin.set(false);
					dontAskAgain.set(_dontAskAgain.isSelected());
				}
				dispose();
			}
		}

		TraktPromptDialog promptDialog = new TraktPromptDialog();
		promptDialog.setModal(true);
		promptDialog.setVisible(true);

		if (dontAskAgain.get()) {
			AppProperties.setTraktPromptLogin(false);
			AppProperties.commitChanges();
		}
		return wantsLogin.get();
	}
}
