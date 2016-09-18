package test;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.json.JSONException;

import data.Source;
import ui.MainFrame;

public class Test {
	public static void main(String[] args) throws IOException, JSONException, InvocationTargetException, InterruptedException, ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
	    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

	    Source.set_address("127.0.0.1");
		Source.set_port("6544");
		Source.set_secure(false);
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				MainFrame mainframe = new MainFrame();
				mainframe.init();
			}
		});
	}
}
