package utils;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.prefs.Preferences;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.GroupLayout.Alignment;

public class AppProperties {
	private static final String _node = "mythfrontend";
	private static enum PreferencesEnum {Address, Port, Secure, Player};
	private static Preferences _preferences = Preferences.userRoot().node(_node);
	
	private static String _stagedAddress;
	private static String _stagedPort;
	private static String _stagedSecure;
	private static String _stagedPlayer;
	
	// Load settings at startup
	static { loadSettings(); }
	
	public static void commitChanges() {
		_preferences.put(PreferencesEnum.Address.toString(), _stagedAddress);
		_preferences.put(PreferencesEnum.Port.toString(), _stagedPort);
		_preferences.put(PreferencesEnum.Secure.toString(), _stagedSecure);
		_preferences.put(PreferencesEnum.Player.toString(), _stagedPlayer);
	}
	
	public static void loadSettings() {
		_stagedAddress = _preferences.get(PreferencesEnum.Address.toString(), "");
		_stagedPort = _preferences.get(PreferencesEnum.Port.toString(), "");
		_stagedSecure = _preferences.get(PreferencesEnum.Secure.toString(), "");
		_stagedPlayer = _preferences.get(PreferencesEnum.Player.toString(), "");
	}
	
	public static String getSourceAddress() {
		return _stagedAddress;
	}
	
	public static String getSourcePort() {
		return _stagedPort;
	}
	
	public static boolean isSourceSecure() {
		return new Boolean(_stagedSecure);
	}
	
	public static String getPlayer() {
		return _stagedPlayer;
	}
	
	public static void setSourceAddress(String address) throws NumberFormatException {
		if (INetAddress.validateIPv4(address))
			_stagedAddress = address;
		else
			throw new NumberFormatException("Invalid IP Address: " + address);
	}
	
	public static void setSourcePort(String port) throws NumberFormatException {
		try {
			int parsed_port = Integer.parseInt(port);
			if (parsed_port > 0)
				_stagedPort = port;
			else
				throw new NumberFormatException();
			
		} catch (NumberFormatException e) {
			throw new NumberFormatException("Invalid Port Number: " + port);
		}
	}
	
	public static void setSourceSecure(boolean secure) {
		_stagedSecure = Boolean.toString(secure);
	}
	
	public static void setPlayer(String executableFile) throws FileNotFoundException {
		// Test validity of 'executableFile'
		File file = new File(executableFile);
		if (file.exists() && file.canExecute())
			_stagedPlayer = executableFile;
		else
			throw new FileNotFoundException("The path " + executableFile + " does not point to an executable file.");
	}
	
	public static boolean displayBackendPropertiesWindow() {
		
		class AppPropertiesDialog extends JDialog implements WindowListener, ActionListener, KeyListener {
			private static final long serialVersionUID = 8432521595616564569L;

			private JTextField _addressTextField, _portTextField;
			private JCheckBox _secureCheckBox;
			private JButton _ok, _cancel;

			public AppPropertiesDialog() {
				super(null, "Connect to the Backend", Dialog.ModalityType.DOCUMENT_MODAL);
				setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
				addWindowListener(this);
				
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
						setVisible(false);
					} catch (NumberFormatException ex) {
						JOptionPane.showMessageDialog(this, ex.getMessage());
						return;
					}
				} else if (e.getSource() == _cancel) {
					dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
				}
			}
			
			@Override
			public void windowClosing(WindowEvent e) {
				setEnabled(false);
				setVisible(false);
				return;
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				switch (e.getKeyCode()) {
				case KeyEvent.VK_ESCAPE:
					dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
					break;
				}
			}
			
			// Required WindowListener Events
			@Override public void windowOpened(WindowEvent e) {}
			@Override public void windowClosed(WindowEvent e) {}
			@Override public void windowIconified(WindowEvent e) {}
			@Override public void windowDeiconified(WindowEvent e) {}
			@Override public void windowActivated(WindowEvent e) {}
			@Override public void windowDeactivated(WindowEvent e) {}
			@Override public void keyTyped(KeyEvent e) {}
			@Override public void keyPressed(KeyEvent e) {}
		}

		AppPropertiesDialog dialog = new AppPropertiesDialog();
		dialog.setModal(true);
		dialog.setVisible(true);
		
		// This is our signal that the user cancelled configuration
		boolean cancelled = !dialog.isEnabled();
		dialog.dispose();
		return cancelled;
	}

	public static void displayPlayerPropertiesWindow() {
		
		class PlayerPropertiesDialog extends JDialog implements ActionListener, KeyListener {
			private static final long serialVersionUID = 6505123236194576995L;
			
			private JTextField _playerPath;
			private JButton _ok, _cancel, _browse;

			public PlayerPropertiesDialog() {
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
						return;
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
