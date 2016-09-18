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

	public void init() {
		// Setup Main Window
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("MythTV Frontend");
        setSize(new Dimension(1600, 900));
        setLocation(100, 100);
        /*setLocationByPlatform(true);*/
        
        // Setup Tab Panes
        RecordingView rview = new RecordingView();
        UpcomingView uview = new UpcomingView();
        RuleView ruview = new RuleView();
        GuideView gview = new GuideView();
        StatusView sview = new StatusView();
        
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
        tabbedpane.add("Recordings", rview);
        tabbedpane.add("Upcoming", uview);
        tabbedpane.add("Rules", ruview);
        tabbedpane.add("Guide", gview);
        tabbedpane.add("Status", sview);
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
}
