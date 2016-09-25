package ui;

import javax.swing.JPanel;

public abstract class ContentView extends JPanel {
	private static final long serialVersionUID = 3484189929451424719L;
	
	/**
	 * Descendants must be able to be refreshed using the init() function call.
	 */
	public abstract void init();
}
