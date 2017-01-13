package ui.recording;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;

import data.Recording;

public class RecordingPopup extends JPopupMenu {
	private static final long serialVersionUID = -6576346817771398195L;
	private JMenuItem _playitem;
	private JMenuItem _deleteitem;
	private JMenuItem _markwatched;
	private JMenuItem _markunwatched;
	private JMenuItem _propertyitem;

	public RecordingPopup() {
		super();
		
		_playitem = new JMenuItem("Play");
		_deleteitem = new JMenuItem("Delete");
		_markwatched = new JMenuItem("Mark Watched");
		_markunwatched = new JMenuItem("Mark Unwatched");
		_propertyitem = new JMenuItem("Properties");
		
		add(_playitem);
		add(_deleteitem);
		addSeparator();
		add(_markwatched);
		add(_markunwatched);
		addSeparator();
		add(_propertyitem);
		
		_playitem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Search for RecordingView Panel
				Component c = (Component) ((JPopupMenu)((JMenuItem) e.getSource()).getParent()).getInvoker();
				while (!(c instanceof RecordingView))
					c = c.getParent();
				
				((RecordingView) c).playSelectedRecording();
			}
		});
		
		_deleteitem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Search for RecordingView Panel
				Component c = (Component) ((JPopupMenu)((JMenuItem) e.getSource()).getParent()).getInvoker();
				while (!(c instanceof RecordingView))
					c = c.getParent();
				
				((RecordingView) c).deleteSelectedRecording();
			}
		});
		
		_markwatched.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {				
				// Search for RecordingView Panel
				Component c = (Component) ((JPopupMenu)((JMenuItem) e.getSource()).getParent()).getInvoker();
				while (!(c instanceof RecordingView))
					c = c.getParent();
				
				((RecordingView) c).markSelectedRecordingWatched(true);
			}
		});
		
		_markunwatched.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Search for RecordingView Panel
				Component c = (Component) ((JPopupMenu)((JMenuItem) e.getSource()).getParent()).getInvoker();
				while (!(c instanceof RecordingView))
					c = c.getParent();
				
				((RecordingView) c).markSelectedRecordingWatched(false);
			}
		});
		
		_propertyitem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JMenuItem menuItem = (JMenuItem) e.getSource();
		        JPopupMenu popupMenu = (JPopupMenu) menuItem.getParent();
		        JTable invoker = (JTable) popupMenu.getInvoker();
		        JFrame topLevel = (JFrame) invoker.getTopLevelAncestor();
		        Recording r = (Recording) invoker.getValueAt(invoker.getSelectedRow(), 0);
		        
				RecordingProperties recordingProperties = new RecordingProperties(topLevel, r);
				recordingProperties.setVisible(true);
			}
		});
	}

	@Override
	public void show(Component invoker, int x, int y) {
		JTable table = (JTable) invoker;
		int row = table.rowAtPoint(new Point(x, y));
		int column = table.columnAtPoint(new Point(x, y));
		
		boolean watched = ((Recording) table.getValueAt(row, column)).is_watched();
		if (watched) _markwatched.setEnabled(false);
		else _markunwatched.setEnabled(false);
		
		super.show(invoker, x, y);
	}
}
