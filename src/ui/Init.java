package ui;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.LogManager;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.json.JSONException;

import data.ArtworkManager;

public class Init {
	public static void main(String[] args) throws IOException, JSONException, InvocationTargetException, InterruptedException, ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		run();
	}
	
	public static void run() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException, IOException {
	    // Set up Look & Feel, Application Properties
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		
		// Initialize Local Artwork Cache
		ArtworkManager.initializeArtworkManager();
		
		// Suppress Platform-Level Warnings
		try {
			LogManager.getLogManager().reset();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		
	    // Create a MainFrame on the EDT
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				MainFrame mainframe = new MainFrame();
				mainframe.init();
			}
		});
	}
}
