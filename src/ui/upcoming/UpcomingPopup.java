package ui.upcoming;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

import data.Rule;
import data.Upcoming;
import data.UpcomingList;
import ui.rule.RuleModifier;

public class UpcomingPopup extends JPopupMenu {
	private static final long serialVersionUID = -3473095245933394365L;
	private JMenuItem _enablerecording;
	private JMenuItem _disablerecording;
	private JMenuItem _addoverride;
	private JMenuItem _deleteoverride;
	private JMenuItem _properties;
	private Upcoming _upcoming;

	public UpcomingPopup() {
		_enablerecording = new JMenuItem("Enable Recording");
		_disablerecording = new JMenuItem("Disable Recording");
		_addoverride = new JMenuItem("Add Override");
		_deleteoverride = new JMenuItem("Delete Override");
		_properties = new JMenuItem("Properties");
		
		add(_enablerecording);
		add(_disablerecording);
		addSeparator();
		add(_addoverride);
		add(_deleteoverride);
		addSeparator();
		add(_properties);
		
		_enablerecording.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
					@Override
					protected Void doInBackground() throws Exception {
						JTable table = (JTable) ((JPopupMenu)((JMenuItem) e.getSource()).getParent()).getInvoker();
						int row = table.getSelectedRow();
						int column = table.getSelectedColumn();
						
						try {
							_upcoming.enable();
							((DefaultTableModel) table.getModel()).fireTableCellUpdated(row, column);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						return null;
					}
					
					@Override
					protected void done() {
						try {
							get();
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				};
				
				worker.execute();
			}
		});
		
		_disablerecording.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
					@Override
					protected Void doInBackground() throws Exception {
						JTable table = (JTable) ((JPopupMenu)((JMenuItem) e.getSource()).getParent()).getInvoker();
						int row = table.getSelectedRow();
						int column = table.getSelectedColumn();
						
						try {
							_upcoming.disable();
							((DefaultTableModel) table.getModel()).fireTableCellUpdated(row, column);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						return null;
					}
					
					@Override
					protected void done() {
						try {
							get();
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				};
				
				worker.execute();
			}
		});
		
		_addoverride.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
					@Override
					protected Void doInBackground() throws Exception {
						JTable table = (JTable) ((JPopupMenu)((JMenuItem) e.getSource()).getParent()).getInvoker();
						int row = table.getSelectedRow();
						int column = table.getSelectedColumn();
						
						try {
							_upcoming.add_override();
							((DefaultTableModel) table.getModel()).fireTableCellUpdated(row, column);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						return null;
					}
					
					@Override
					protected void done() {
						try {
							get();
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				};
				
				worker.execute();
			}
		});
		
		_deleteoverride.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
					@Override
					protected Void doInBackground() throws Exception {
						JTable table = (JTable) ((JPopupMenu)((JMenuItem) e.getSource()).getParent()).getInvoker();
						int row = table.getSelectedRow();
						int column = table.getSelectedColumn();
						
						try {
							_upcoming.remove_override();
							((DefaultTableModel) table.getModel()).fireTableCellUpdated(row, column);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						return null;
					}
					
					@Override
					protected void done() {
						try {
							get();
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				};
				
				worker.execute();
			}
		});
		
		_properties.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JMenuItem menuItem = (JMenuItem) e.getSource();
		        JPopupMenu popupMenu = (JPopupMenu) menuItem.getParent();
		        JTable invoker = (JTable) popupMenu.getInvoker();
		        JFrame topLevel = (JFrame) invoker.getTopLevelAncestor();
		        Rule r = _upcoming.get_rule();
		        
				RuleModifier modifier = new RuleModifier(topLevel, r);
				modifier.setVisible(true);				
			}
		});
	}
	
	@Override
	public void show(Component invoker, int x, int y) {
		JTable table = (JTable) invoker;
		int row = table.rowAtPoint(new Point(x, y));
		int column = table.columnAtPoint(new Point(x, y));
		Rectangle r = table.getCellRect(row, column, true);
		
		UpcomingList upcominglist = (UpcomingList) table.getValueAt(row, column);
		int selectionIndex = UpcomingListRenderer.indexFromCoordinates(x, y, r);

		// User clicked out of list bounds
		if (selectionIndex < 0 || selectionIndex >= upcominglist.get_upcoming().size())
			return;
		
		_upcoming = upcominglist.get_upcoming().get(selectionIndex);
		Rule rule = _upcoming.get_rule();
		
		if (rule.is_disabled() || rule.is_override()) {
			_disablerecording.setEnabled(false);
			_addoverride.setEnabled(false);
		}
		
		if (!rule.is_disabled()) _enablerecording.setEnabled(false);
		if (!rule.is_override()) _deleteoverride.setEnabled(false);
		
		if (_upcoming.get_status().getStatusInt() < 0) _addoverride.setEnabled(false);
		else _disablerecording.setEnabled(false);
		
		super.show(invoker, x, y);
	}
}
