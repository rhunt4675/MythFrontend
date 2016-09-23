package utils;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class AppPropertiesDialog extends JDialog implements ActionListener, WindowListener {
	private static final long serialVersionUID = 8656466185102813421L;

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
		_cancel = new JButton("Cancel");
		_cancel.addActionListener(this);
		_cancel.setMnemonic(KeyEvent.VK_C);
		
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
				dispose();
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
		dispose();
		System.exit(1);
	}

	// Required WindowListener Events
	public void windowOpened(WindowEvent e) {}
	public void windowClosed(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowActivated(WindowEvent e) {}
	public void windowDeactivated(WindowEvent e) {}
}
