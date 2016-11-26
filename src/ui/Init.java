package ui;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.json.JSONException;

import data.Source;
import utils.AppProperties;

public class Init {
	public static void main(String[] args) throws IOException, JSONException, InvocationTargetException, InterruptedException, ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		run();
	}
	
	public static void run() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException, IOException {
	    // Set up Look & Feel, Application Properties
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	    
	    // Verify Connectivity to the Backend
	    while (true) {
		    try {
		    	Source.test_connection();
		    	AppProperties.updateAndWrite();
		    	break;
		    } catch (IOException e) {
		    	JOptionPane.showMessageDialog(null, "Connection failed!");
		    	boolean cancelled = AppProperties.displayBackendPropertiesWindow();
		    	if (cancelled) System.exit(1);
		    }
	    }
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				MainFrame mainframe = new MainFrame();
				mainframe.init();
			}
		});
	}
}