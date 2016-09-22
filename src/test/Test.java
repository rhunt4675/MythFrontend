package test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.json.JSONException;

import data.Source;
import ui.MainFrame;
import utils.AppProperties;

public class Test {
	public static void main(String[] args) throws IOException, JSONException, InvocationTargetException, InterruptedException, ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		run();
	}
	
	public static void run() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException, IOException {
	    // Set up Look & Feel, Application Properties
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	    AppProperties.readAndUpdate();
	    
	    // Verify Connectivity to the Backend
	    while (true) {
		    try {
		    	Source.test_connection();
		    	break;
		    } catch (IOException e) {
		    	AppProperties.displayPropertiesWindow();
		    	AppProperties.updateAndWrite();
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
