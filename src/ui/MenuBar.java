package ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import data.Source;
import ui.MainFrame.ContentViewEnum;
import utils.AppProperties;

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
		_exit = new JMenuItem("Exit", KeyEvent.VK_E);
		_exit.addActionListener(this);
		file.add(_refresh);
		file.addSeparator();
		file.add(_exit);
		
		// Build out "Edit" Menu
		_properties = new JMenuItem("Properties", KeyEvent.VK_P);
		_properties.addActionListener(this);
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
			JMenuItem menuItem = (JMenuItem) e.getSource();
	        JPopupMenu popupMenu = (JPopupMenu) menuItem.getParent();
	        JMenu invoker = (JMenu) popupMenu.getInvoker();
	        MainFrame topLevel = (MainFrame) invoker.getTopLevelAncestor();

	        // Refresh each ContentView Panel
	        for (ContentViewEnum cve : ContentViewEnum.values())
	        	topLevel.getContentView(cve).init();

		} else if (e.getSource() == _exit) {
			JMenuItem menuItem = (JMenuItem) e.getSource();
	        JPopupMenu popupMenu = (JPopupMenu) menuItem.getParent();
	        JMenu invoker = (JMenu) popupMenu.getInvoker();
	        JFrame topLevel = (JFrame) invoker.getTopLevelAncestor();
	        
	        // Close the Top Window
	        topLevel.dispose();
		} else if (e.getSource() == _properties) {
			
			// Prompt user for Properties			
		    while (true) {
		    	boolean cancelled = AppProperties.displayPropertiesWindow();
		    	if (cancelled) return;
		    	
		    	try {
			    	Source.test_connection(AppProperties.getSourceAddress(), AppProperties.getSourcePort(), AppProperties.isSourceSecure());
			    	AppProperties.updateAndWrite();
			    	break;
			    } catch (IOException ex) {
			    	JOptionPane.showMessageDialog(null, "Connection failed!");
			    }
		    }
		    
			_refresh.doClick();
		}
	}
}
