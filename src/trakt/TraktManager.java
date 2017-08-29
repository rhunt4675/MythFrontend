package trakt;

import data.Recording;
import org.json.JSONException;
import org.json.JSONObject;
import utils.AppProperties;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TraktManager {
	public static final String BUILD_DATE = "2017-02-25";
	public static final String BUILD_VERSION = "1.0";
	
	public static final String BASE_URL = "https://api.trakt.tv/";
	public static final String REDIRECT_URI = "https://api.trakt.tv/";
	
	public static final String CLIENT_ID = "b9a03e552dc3c14a03928740f146f00b45d222401104da830eaded5711925878";
	public static final String CLIENT_SECRET = "45e36696a92f498121cd90b635e2531ea81df3e2bd10d36c1ccc6cbf7fcd408b";
	private static final Logger LOGGER = Logger.getLogger(TraktManager.class.getName());

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
			LOGGER.log(Level.SEVERE, "Scrobble Failed: " + e.getMessage(), e);
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
			LOGGER.log(Level.SEVERE, "Scrobble Failed: " + e.getMessage(), e);
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
		if (shouldPromptForTraktLogin()) {
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
