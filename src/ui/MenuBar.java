package ui;

import data.Source;
import trakt.TraktManager;
import utils.AppProperties;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;

public class MenuBar extends JMenuBar implements ActionListener {
	private JMenuItem _refresh, _exit, _properties, _player, _trakt;
	
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
		_exit = new JMenuItem("Exit", KeyEvent.VK_E);
		_exit.addActionListener(this);
		file.add(_refresh);
		file.addSeparator();
		file.add(_exit);
		
		// Build out "Edit" Menu
		_properties = new JMenuItem("Properties", KeyEvent.VK_P);
		_properties.addActionListener(this);
		_player = new JMenuItem("Player Settings", KeyEvent.VK_L);
		_player.addActionListener(this);
		_trakt = new JMenuItem("Login to Trakt", KeyEvent.VK_T);
		_trakt.addActionListener(this);
		edit.add(_properties);
		edit.add(_player);
		edit.add(_trakt);
		
		// Add Menus to Menu Bar
		add(file);
		add(edit);
		add(view);
		add(tools);
		add(help);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JMenuItem menuItem = (JMenuItem) e.getSource();
		JPopupMenu popupMenu = (JPopupMenu) menuItem.getParent();
		JMenu invoker = (JMenu) popupMenu.getInvoker();
		MainFrame topLevel = (MainFrame) invoker.getTopLevelAncestor();

		if (e.getSource() == _refresh) {
	        // Refresh Entire MainFrame
			ContentView selected = topLevel.getSelectedContentView();
	        topLevel.init();
	        topLevel.setSelectedContentView(selected);

		} else if (e.getSource() == _exit) {
	        // Close the Top Window
	        topLevel.dispose();

		} else if (e.getSource() == _properties) {
			
			// Prompt user for Properties
		    while (true) {
		    	boolean cancelled = AppProperties.displayBackendPropertiesWindow(topLevel);
		    	if (cancelled) {
		    		AppProperties.loadSettings();
		    		break;
				}
		    	
		    	try {
			    	Source.test_connection();
			    	AppProperties.commitChanges();
			    	_refresh.doClick();
			    	break;
			    } catch (IOException ex) {
			    	JOptionPane.showMessageDialog(topLevel, "Connection failed!");
			    }
		    }

		} else if (e.getSource() == _player) {
			AppProperties.displayPlayerPropertiesWindow();
		} else if (e.getSource() == _trakt) {
			TraktManager.promptForTraktLogin();
		}
	}
}
