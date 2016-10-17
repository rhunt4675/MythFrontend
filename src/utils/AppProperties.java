package utils;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.prefs.Preferences;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.GroupLayout.Alignment;

import data.Source;

public class AppProperties {
	private static final String _node = "mythfrontend";
	private static enum PreferencesEnum {Address, Port, Secure};
	private static Preferences _preferences = Preferences.userRoot().node(_node);
	
	private static String _stagedAddress = _preferences.get(PreferencesEnum.Address.toString(), null);
	private static String _stagedPort = _preferences.get(PreferencesEnum.Port.toString(), null);
	private static String _stagedSecure = _preferences.get(PreferencesEnum.Secure.toString(), null);
	

	public static void readAndUpdate() {
		apply();
	}
	
	public static void updateAndWrite() {
		_preferences.put(PreferencesEnum.Address.toString(), _stagedAddress);
		_preferences.put(PreferencesEnum.Port.toString(), _stagedPort);
		_preferences.put(PreferencesEnum.Secure.toString(), _stagedSecure);
		
		apply();
	}

	private static void apply() {
		// Set Data Source Properties
		Source.set_address(_preferences.get(PreferencesEnum.Address.toString(), null));
		Source.set_port(_preferences.get(PreferencesEnum.Port.toString(), null));
		Source.set_secure(new Boolean(_preferences.get(PreferencesEnum.Secure.toString(), null)));
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
	
	public static boolean displayPropertiesWindow() {
		
		class AppPropertiesDialog extends JDialog implements WindowListener, ActionListener {
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
				_ok.setMnemonic(KeyEvent.VK_O);
				_ok.setMinimumSize(new Dimension(75, 0));
				_cancel = new JButton("Cancel");
				_cancel.addActionListener(this);
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
				_portTextField = new JTextField(AppProperties.getSourcePort());
				_secureCheckBox = new JCheckBox();
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

			// Required WindowListener Events
			public void windowOpened(WindowEvent e) {}
			public void windowClosed(WindowEvent e) {}
			public void windowIconified(WindowEvent e) {}
			public void windowDeiconified(WindowEvent e) {}
			public void windowActivated(WindowEvent e) {}
			public void windowDeactivated(WindowEvent e) {}
		}

		AppPropertiesDialog dialog = new AppPropertiesDialog();
		dialog.setModal(true);
		dialog.setVisible(true);
		
		// This is our signal that the user cancelled configuration
		boolean cancelled = !dialog.isEnabled();
		dialog.dispose();
		return cancelled;
	}
}
