package ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class MenuBar extends JMenuBar implements ActionListener {
	private static final long serialVersionUID = 8648195999870453299L;
	private JMenuItem _refresh, _exit, _properties;
	
	public MenuBar() {
		// Define Menus
		JMenu file = new JMenu("File");
		file.setMnemonic(KeyEvent.VK_F);
		JMenu edit = new JMenu("Edit");
		edit.setMnemonic(KeyEvent.VK_P);
		JMenu view = new JMenu("View");
		view.setMnemonic(KeyEvent.VK_V);
		JMenu tools = new JMenu("Tools");
		tools.setMnemonic(KeyEvent.VK_T);
		JMenu help = new JMenu("Help");
		help.setMnemonic(KeyEvent.VK_H);
		
		// Build out "File" Menu
		_refresh = new JMenuItem("Refresh", KeyEvent.VK_R);
		_refresh.addActionListener(this);
		_refresh.setEnabled(false);
		_exit = new JMenuItem("Exit", KeyEvent.VK_E);
		_exit.addActionListener(this);
		file.add(_refresh);
		file.addSeparator();
		file.add(_exit);
		
		// Build out "Edit" Menu
		_properties = new JMenuItem("Properties", KeyEvent.VK_P);
		_properties.addActionListener(this);
		_properties.setEnabled(false);
		edit.add(_properties);
		
		// Add Menus to Menu Bar
		add(file);
		add(edit);
		add(view);
		add(tools);
		add(help);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == _refresh) {
			System.out.println("Refresh");
			
		} else if (e.getSource() == _exit) {
			JMenuItem menuItem = (JMenuItem) e.getSource();
	        JPopupMenu popupMenu = (JPopupMenu) menuItem.getParent();
	        JMenu invoker = (JMenu) popupMenu.getInvoker();
	        JFrame topLevel = (JFrame) invoker.getTopLevelAncestor();
	        
	        topLevel.dispose();
		} else if (e.getSource() == _properties) {
			
		}
	}
}
