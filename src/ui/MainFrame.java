package ui;

import java.awt.Dimension;
import java.awt.Frame;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import ui.guide.GuideView;
import ui.recording.RecordingView;
import ui.rule.RuleView;
import ui.status.StatusView;
import ui.upcoming.UpcomingView;

public class MainFrame extends JFrame {
	private static final long serialVersionUID = 85923720912596620L;
	private final RecordingView _rview = new RecordingView();
	private final UpcomingView _uview = new UpcomingView();
    private final RuleView _ruview = new RuleView();
    private final GuideView _gview = new GuideView();
    private final StatusView _sview = new StatusView();
    
    public static enum ContentViewEnum {RecordingView, UpcomingView, RuleView, GuideView, StatusView};
    
	public void init() {
		// Setup Main Window
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("MythTV Frontend");
        setSize(new Dimension(1600, 900));
        setLocation(100, 100);
        /*setLocationByPlatform(true);*/
        
        // Initialize Tab Panels
        _rview.init();
        _uview.init();
        _ruview.init();
        _gview.init();
        _sview.init();        
        
        // Setup Tab Labels
        JLabel rlabel = new JLabel("Recordings", SwingConstants.CENTER);
        JLabel ulabel = new JLabel("Upcoming", SwingConstants.CENTER);
        JLabel rulabel = new JLabel("Rules", SwingConstants.CENTER);
        JLabel glabel = new JLabel("Guide", SwingConstants.CENTER);
        JLabel slabel = new JLabel("Status", SwingConstants.CENTER);
        rlabel.setPreferredSize(new Dimension(80, 15));
        ulabel.setPreferredSize(new Dimension(80, 15));
        rulabel.setPreferredSize(new Dimension(80, 15));
        glabel.setPreferredSize(new Dimension(80, 15));
        slabel.setPreferredSize(new Dimension(80, 15));
        
        // Setup Main Window Tabs
        JTabbedPane tabbedpane = new JTabbedPane();
        tabbedpane.setTabPlacement(JTabbedPane.BOTTOM);
        tabbedpane.add("Recordings", _rview);
        tabbedpane.add("Upcoming", _uview);
        tabbedpane.add("Rules", _ruview);
        tabbedpane.add("Guide", _gview);
        tabbedpane.add("Status", _sview);
        tabbedpane.setTabComponentAt(0, rlabel);
        tabbedpane.setTabComponentAt(1, ulabel);
        tabbedpane.setTabComponentAt(2, rulabel);
        tabbedpane.setTabComponentAt(3, glabel);
        tabbedpane.setTabComponentAt(4, slabel);
        add(tabbedpane);
        
        // Setup the Menu Bar
        MenuBar menubar = new MenuBar();
        setJMenuBar(menubar);
        
        // Show the Window
        setVisible(true);
        setState(Frame.NORMAL);
	}
	
	public ContentView getContentView(ContentViewEnum cve) {
		// Return the Requested JPanel
		switch (cve) {
		case GuideView:
			return _gview;
		case RecordingView:
			return _rview;
		case RuleView:
			return _ruview;
		case StatusView:
			return _sview;
		case UpcomingView:
			return _uview;
		default:
			return null;
		}
	}
}
