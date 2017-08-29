package ui;

import javax.swing.*;

public abstract class ContentView extends JPanel {
	/**
	 * Descendants must be able to be refreshed using the init() function call.
	 */
	public abstract void init();
}
