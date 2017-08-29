package ui;

import data.Source;
import ui.guide.GuideView;
import ui.recording.RecordingView;
import ui.rule.RuleView;
import ui.status.StatusView;
import ui.upcoming.UpcomingView;
import utils.AppProperties;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

public class MainFrame extends JFrame  {
	private static final Dimension _tabDimension = new Dimension(80, 15);
	
	private final MenuBar _menubar = new MenuBar();
	private final JTabbedPane _tabbedpane = new JTabbedPane();
	private final RecordingView _rview = new RecordingView();
	private final UpcomingView _uview = new UpcomingView();
    private final RuleView _ruview = new RuleView();
    private final GuideView _gview = new GuideView();
    private final StatusView _sview = new StatusView();
        
	public void init() {
		// Setup Main Window
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("MythTV Frontend");
        setIconImage(new ImageIcon(getClass().getResource("/res/win32_icon.jpg")).getImage());
        setSize(new Dimension(1600, 900));
        setLocation(100, 100);
        setJMenuBar(_menubar);
        addKeyListener(_keyListener);
        /*setLocationByPlatform(true);*/    
        
        // Setup Tab Labels
        JLabel rlabel = new JLabel("Recordings", SwingConstants.CENTER);
        JLabel ulabel = new JLabel("Upcoming", SwingConstants.CENTER);
        JLabel rulabel = new JLabel("Rules", SwingConstants.CENTER);
        JLabel glabel = new JLabel("Guide", SwingConstants.CENTER);
        JLabel slabel = new JLabel("Status", SwingConstants.CENTER);
        rlabel.setPreferredSize(_tabDimension);
        ulabel.setPreferredSize(_tabDimension);
        rulabel.setPreferredSize(_tabDimension);
        glabel.setPreferredSize(_tabDimension);
        slabel.setPreferredSize(_tabDimension);
        
        // Setup Main Window Tabs
        _tabbedpane.setTabPlacement(JTabbedPane.BOTTOM);
        _tabbedpane.add("Recordings", _rview);
        _tabbedpane.add("Upcoming", _uview);
        _tabbedpane.add("Rules", _ruview);
        _tabbedpane.add("Guide", _gview);
        _tabbedpane.add("Status", _sview);
        _tabbedpane.setTabComponentAt(0, rlabel);
        _tabbedpane.setTabComponentAt(1, ulabel);
        _tabbedpane.setTabComponentAt(2, rulabel);
        _tabbedpane.setTabComponentAt(3, glabel);
        _tabbedpane.setTabComponentAt(4, slabel);
        add(_tabbedpane);
                
        // Show the Window
        setVisible(true);
        requestFocus();
        setState(Frame.NORMAL);
        
        // Check Network Connectivity
        SwingUtilities.invokeLater(new Runnable() {
            @Override public void run() {
                while (true) {
                    try {
                        Source.test_connection();
                        AppProperties.commitChanges();
                        break;
                    } catch (IOException e) {
                        if (!AppProperties.getSourceAddress().isEmpty())
                            JOptionPane.showMessageDialog(MainFrame.this, "Connection failed!");
                        boolean cancelled = AppProperties.displayBackendPropertiesWindow(MainFrame.this);
                        if (cancelled) return;
                    }
                }

                // Initialize Tab Panels
                _rview.init();
                _uview.init();
                _ruview.init();
                _gview.init();
                _sview.init();
            }
        });
	}
	
	// Return the Requested ContentView JPanel
	public ContentView getSelectedContentView() {
		return (ContentView) _tabbedpane.getSelectedComponent();
	}

	public void setSelectedContentView(ContentView cv) { _tabbedpane.setSelectedComponent(cv); }

	public void refresh() {
		_menubar.getMenu(0).getItem(0).doClick();
	}

    private KeyListener _keyListener = new KeyListener() {
		@Override public void keyTyped(KeyEvent e) {}
		@Override public void keyPressed(KeyEvent e) {}
		
		@Override
		public void keyReleased(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_F5) {
				refresh();
			}
		}
    };
}
