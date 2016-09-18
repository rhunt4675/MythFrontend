package ui.recording;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

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
				JTable table = (JTable) ((JPopupMenu)((JMenuItem) e.getSource()).getParent()).getInvoker();
				int row = table.getSelectedRow();
				int column = table.getSelectedColumn();
				((Recording) table.getValueAt(row, column)).play();
			}
		});
		
		_deleteitem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JTable table = (JTable) ((JPopupMenu)((JMenuItem) e.getSource()).getParent()).getInvoker();
				int row = table.getSelectedRow();
				int column = table.getSelectedColumn();
				if (row == -1) return;
				
				int result = JOptionPane.showConfirmDialog(null, "Allow re-record?", "Delete Recording", JOptionPane.YES_NO_CANCEL_OPTION);
				if (result == JOptionPane.CANCEL_OPTION) return;

				Recording recording = (Recording) table.getValueAt(row, column);
				((DefaultTableModel) table.getModel()).removeRow(row);
				
				SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
					@Override
					protected Void doInBackground() throws Exception {
						boolean allow_rerecord = (result == JOptionPane.YES_OPTION);
						
						try {
							recording.delete(allow_rerecord);
						} catch (IOException e) {
							JOptionPane.showMessageDialog(null, "Delete failed!", "Delete Recording", JOptionPane.WARNING_MESSAGE);
						} catch (Exception e) {
							e.printStackTrace();
						}
						
						return null;
					}
				};
				
				worker.execute();
			}
		});
		
		_markwatched.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
					@Override
					protected Void doInBackground() throws Exception {
						JTable table = (JTable) ((JPopupMenu)((JMenuItem) e.getSource()).getParent()).getInvoker();
						int row = table.getSelectedRow();
						int column = table.getSelectedColumn();
						
						Recording recording = (Recording) table.getValueAt(row, column);
						try {
							recording.mark_watched(true);
						} catch (IOException exp) {
							exp.printStackTrace();
						}
						
						((DefaultTableModel) table.getModel()).fireTableCellUpdated(row, 0);
						return null;
					}
				};
				
				worker.execute();
			}
		});
		
		_markunwatched.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
					@Override
					protected Void doInBackground() throws Exception {
						JTable table = (JTable) ((JPopupMenu)((JMenuItem) e.getSource()).getParent()).getInvoker();
						int row = table.getSelectedRow();
						int column = table.getSelectedColumn();
						
						Recording recording = (Recording) table.getValueAt(row, column);
						try {
							recording.mark_watched(false);
						} catch (IOException exp) {
							exp.printStackTrace();
						}
						
						((DefaultTableModel) table.getModel()).fireTableCellUpdated(row, 0);
						return null;
					}
				};
				
				worker.execute();
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
