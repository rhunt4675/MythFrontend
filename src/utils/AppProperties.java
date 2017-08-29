package utils;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.prefs.Preferences;

public class AppProperties {
	private static final String _node = "mythfrontend";
	private enum PreferencesEnum {Address, Port, Secure, Player, TraktAccessCode, TraktRefreshCode, TraktPromptLogin}
	private static Preferences _preferences = Preferences.userRoot().node(_node);
	
	private static String _stagedAddress;
	private static String _stagedPort;
	private static String _stagedSecure;
	private static String _stagedPlayer;
	private static String _stagedTraktAccessCode;
	private static String _stagedTraktRefreshCode;
	private static boolean _stagedTraktPromptLogin;
	
	// Load settings at startup
	static { loadSettings(); }
	
	public static void commitChanges() {
		_preferences.put(PreferencesEnum.Address.toString(), _stagedAddress);
		_preferences.put(PreferencesEnum.Port.toString(), _stagedPort);
		_preferences.put(PreferencesEnum.Secure.toString(), _stagedSecure);
		_preferences.put(PreferencesEnum.Player.toString(), _stagedPlayer);
		_preferences.put(PreferencesEnum.TraktAccessCode.toString(), _stagedTraktAccessCode);
		_preferences.put(PreferencesEnum.TraktRefreshCode.toString(), _stagedTraktRefreshCode);
		_preferences.put(PreferencesEnum.TraktPromptLogin.toString(), Boolean.toString(_stagedTraktPromptLogin));
	}
	
	public static void loadSettings() {
		_stagedAddress = _preferences.get(PreferencesEnum.Address.toString(), "");
		_stagedPort = _preferences.get(PreferencesEnum.Port.toString(), "6544");
		_stagedSecure = _preferences.get(PreferencesEnum.Secure.toString(), "");
		_stagedPlayer = _preferences.get(PreferencesEnum.Player.toString(), "");
		_stagedTraktAccessCode = _preferences.get(PreferencesEnum.TraktAccessCode.toString(), "");
		_stagedTraktRefreshCode = _preferences.get(PreferencesEnum.TraktRefreshCode.toString(), "");
		_stagedTraktPromptLogin = Boolean.valueOf(_preferences.get(PreferencesEnum.TraktPromptLogin.toString(), Boolean.TRUE.toString()));
	}
	
	public static String getSourceAddress() {
		return _stagedAddress;
	}
	
	public static String getSourcePort() {
		return _stagedPort;
	}
	
	public static boolean isSourceSecure() {
		return Boolean.valueOf(_stagedSecure);
	}
	
	public static String getPlayer() {
		return _stagedPlayer;
	}
	
	public static String getTraktAccessCode() {
		return _stagedTraktAccessCode;
	}
	
	public static String getTraktRefreshCode() {
		return _stagedTraktRefreshCode;
	}
	
	private static void setSourceAddress(String address) throws NumberFormatException {
		if (INetAddress.validateIPv4(address))
			_stagedAddress = address;
		else
			throw new NumberFormatException("Invalid IP Address: " + address);
	}
	
	private static void setSourcePort(String port) throws NumberFormatException {
		int parsed_port = Integer.parseInt(port);
		if (parsed_port > 0)
			_stagedPort = port;
		else
			throw new NumberFormatException("Invalid Port Number: " + port);
	}
	
	private static void setSourceSecure(boolean secure) {
		_stagedSecure = Boolean.toString(secure);
	}
	
	private static void setPlayer(String executableFile) throws FileNotFoundException {
		// Test validity of 'executableFile'
		File file = new File(executableFile);
		if (file.exists() && file.canExecute())
			_stagedPlayer = executableFile;
		else
			throw new FileNotFoundException("The path " + executableFile + " does not point to an executable file.");
	}
	
	public static void setTraktAccessCode(String code) {
		_stagedTraktAccessCode = code;
	}
	
	public static void setTraktRefreshCode(String code) {
		_stagedTraktRefreshCode = code;
	}

	public static boolean getTraktPromptLogin() { return _stagedTraktPromptLogin; }

	public static void setTraktPromptLogin(boolean flag) { _stagedTraktPromptLogin = flag; }
	
	public static boolean displayBackendPropertiesWindow(JFrame parent) {
		AtomicBoolean userCancelled = new AtomicBoolean(false);
		
		class AppPropertiesDialog extends JDialog implements /*WindowListener,*/ ActionListener, KeyListener {
			private JTextField _addressTextField, _portTextField;
			private JCheckBox _secureCheckBox;
			private JButton _ok, _cancel;

			private AppPropertiesDialog() {
				super(parent, "Connect to the Backend", Dialog.ModalityType.DOCUMENT_MODAL);
				//setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
				//addWindowListener(this);
				
				_ok = new JButton("OK");
				_ok.addActionListener(this);
				_ok.addKeyListener(this);
				_ok.setMnemonic(KeyEvent.VK_O);
				_ok.setMinimumSize(new Dimension(75, 0));
				_cancel = new JButton("Cancel");
				_cancel.addActionListener(this);
				_cancel.addKeyListener(this);
				_cancel.setMnemonic(KeyEvent.VK_C);
				_cancel.setMinimumSize(new Dimension(75, 0));
				
				GroupLayout layout = new GroupLayout(getContentPane());
				getContentPane().setLayout(layout);
				
				JLabel title = new JLabel("MythTV Server Settings");
				title.setFont(new Font("Arial", Font.BOLD, 14));
				JLabel address = new JLabel("IPv4 Address: ");
				JLabel port = new JLabel("Port: ");
				JLabel secure = new JLabel("Secure? (https): ");
				
				_addressTextField = new JTextField(AppProperties.getSourceAddress());
				_addressTextField.setMinimumSize(new Dimension(150, 0));
				_addressTextField.addKeyListener(this);
				_portTextField = new JTextField(AppProperties.getSourcePort());
				_portTextField.addKeyListener(this);
				_secureCheckBox = new JCheckBox();
				_secureCheckBox.addKeyListener(this);
				_secureCheckBox.setSelected(AppProperties.isSourceSecure());
				
				layout.setHorizontalGroup(layout.createParallelGroup(Alignment.CENTER)
					.addComponent(title)
					.addGroup(layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
							.addComponent(address)
							.addComponent(port)
							.addComponent(secure))
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(_addressTextField)
							.addComponent(_portTextField)
							.addComponent(_secureCheckBox)
							.addGroup(layout.createSequentialGroup()
									.addComponent(_cancel)
									.addComponent(_ok)))));

				layout.setVerticalGroup(
					layout.createSequentialGroup()
						.addComponent(title)
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
							.addComponent(address).addComponent(_addressTextField))
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
							.addComponent(port).addComponent(_portTextField))
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
							.addComponent(secure).addComponent(_secureCheckBox))
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
							.addComponent(_cancel).addComponent(_ok)));
				
				layout.setAutoCreateContainerGaps(true);
				layout.setAutoCreateGaps(true);
				pack();
				
				setLocationRelativeTo(null);
				setResizable(false);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == _ok) {
					try {
						AppProperties.setSourceAddress(_addressTextField.getText());
						AppProperties.setSourcePort(_portTextField.getText());
						AppProperties.setSourceSecure(_secureCheckBox.isSelected());
						dispose();
					} catch (NumberFormatException ex) {
						JOptionPane.showMessageDialog(this, ex.getMessage());
					}
				} else if (e.getSource() == _cancel) {
					userCancelled.set(true);
					dispose();
				}
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				switch (e.getKeyCode()) {
				case KeyEvent.VK_ESCAPE:
					dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
					break;
				}
			}
			
			@Override public void keyTyped(KeyEvent e) {}
			@Override public void keyPressed(KeyEvent e) {}
		}

		AppPropertiesDialog dialog = new AppPropertiesDialog();
		dialog.setModal(true);
		dialog.setVisible(true);
		return userCancelled.get();
	}

	public static void displayPlayerPropertiesWindow() {
		
		class PlayerPropertiesDialog extends JDialog implements ActionListener, KeyListener {
			private JTextField _playerPath;
			private JButton _ok, _cancel, _browse;

			private PlayerPropertiesDialog() {
				super(null, "Playback Configuration", Dialog.ModalityType.DOCUMENT_MODAL);
				setDefaultCloseOperation(DISPOSE_ON_CLOSE);
				addKeyListener(this);
				
				_ok = new JButton("OK");
				_ok.addActionListener(this);
				_ok.setMnemonic(KeyEvent.VK_O);
				_ok.addKeyListener(this);
				_ok.setMinimumSize(new Dimension(75, 0));
				_cancel = new JButton("Cancel");
				_cancel.addActionListener(this);
				_cancel.setMnemonic(KeyEvent.VK_C);
				_cancel.addKeyListener(this);
				_cancel.setMinimumSize(new Dimension(75, 0));
				_browse = new JButton("Browse");
				_browse.addActionListener(this);
				_browse.setMnemonic(KeyEvent.VK_B);
				_browse.addKeyListener(this);
				_browse.setMinimumSize(new Dimension(75, 0));
				_playerPath = new JTextField(AppProperties.getPlayer());
				_playerPath.addKeyListener(this);
				_playerPath.setMinimumSize(new Dimension(150, 0));
				
				GroupLayout layout = new GroupLayout(getContentPane());
				getContentPane().setLayout(layout);
				
				JLabel title = new JLabel("Video Player Settings");
				title.setFont(new Font("Arial", Font.BOLD, 14));
				
				layout.setHorizontalGroup(layout.createParallelGroup(Alignment.CENTER)
					.addComponent(title)
					.addGroup(layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
							.addGroup(layout.createSequentialGroup()
									.addComponent(_playerPath)
									.addComponent(_browse))
							.addGroup(layout.createSequentialGroup()
									.addComponent(_cancel)
									.addComponent(_ok)))));

				layout.setVerticalGroup(
					layout.createSequentialGroup()
						.addComponent(title)
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
							.addComponent(_playerPath).addComponent(_browse))
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
							.addComponent(_cancel).addComponent(_ok)));
				
				layout.setAutoCreateContainerGaps(true);
				layout.setAutoCreateGaps(true);
				pack();
				
				setLocationRelativeTo(null);
				setResizable(false);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == _ok) {
					try {
						AppProperties.setPlayer(_playerPath.getText());
						AppProperties.commitChanges();
						this.dispose();
					} catch (FileNotFoundException ex) {
						JOptionPane.showMessageDialog(this, ex.getMessage());
					}
				} else if (e.getSource() == _browse) {
					final JFileChooser fc = new JFileChooser();
					int returnVal = fc.showDialog(this, "Select");
					
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						_playerPath.setText(fc.getSelectedFile().getAbsolutePath());
					}
				} else if (e.getSource() == _cancel) {
					this.dispose();
				}
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				switch (e.getKeyCode()) {
				case KeyEvent.VK_ESCAPE: this.dispose(); break;
				}
			}
			@Override public void keyTyped(KeyEvent e) {}
			@Override public void keyPressed(KeyEvent e) {}
		}

		PlayerPropertiesDialog dialog = new PlayerPropertiesDialog();
		dialog.setModal(true);
		dialog.setVisible(true);
	}
}
