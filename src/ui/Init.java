package ui;

import data.ArtworkManager;
import org.json.JSONException;

import javax.swing.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class Init {
	public static void main(String[] args) throws IOException, JSONException, InvocationTargetException, InterruptedException, ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		run();
	}
	
	private static void run() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException, IOException {
	    // Set up Look & Feel, Application Properties
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		
		// Initialize Local Artwork Cache
		ArtworkManager.initializeArtworkManager();
		
	    // Create a MainFrame on the EDT
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				MainFrame mainframe = new MainFrame();
				mainframe.init();
			}
		});
	}
}
