package ui.upcoming;

import data.Rule;
import data.Upcoming;
import ui.rule.RuleModifier;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UpcomingPopup extends JPopupMenu {
	private static final Logger LOGGER = Logger.getLogger(UpcomingPopup.class.getName());
	private JMenuItem _enablerecording;
	private JMenuItem _disablerecording;
	private JMenuItem _addoverride;
	private JMenuItem _deleteoverride;
	private JMenuItem _properties;
	private Upcoming _upcoming;

	public UpcomingPopup(Upcoming upcoming, int row, int column) {
		_upcoming = upcoming;
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
		
		// Customize Menu to Upcoming Instance
		customize();
		
		_enablerecording.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
					@Override
					protected Void doInBackground() throws Exception {
						JTable table = (JTable) ((JPopupMenu)((JMenuItem) e.getSource()).getParent()).getInvoker();

						try {
							_upcoming.enable();
							((DefaultTableModel) table.getModel()).fireTableCellUpdated(row, column);
						} catch (IOException e1) {
							LOGGER.log(Level.SEVERE, e1.toString(), e1);
						}
						return null;
					}
					
					@Override
					protected void done() {
						try {
							get();
						} catch (Exception ex) {
							LOGGER.log(Level.SEVERE, ex.toString(), ex);
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
						
						try {
							_upcoming.disable();
							((DefaultTableModel) table.getModel()).fireTableCellUpdated(row, column);
						} catch (IOException e1) {
							LOGGER.log(Level.SEVERE, e1.toString(), e1);
						}
						return null;
					}
					
					@Override
					protected void done() {
						try {
							get();
						} catch (Exception ex) {
							LOGGER.log(Level.SEVERE, ex.toString(), ex);
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
						
						try {
							_upcoming.add_override();
							((DefaultTableModel) table.getModel()).fireTableCellUpdated(row, column);
						} catch (IOException e1) {
							LOGGER.log(Level.SEVERE, e1.toString(), e1);
						}
						return null;
					}
					
					@Override
					protected void done() {
						try {
							get();
						} catch (Exception ex) {
							LOGGER.log(Level.SEVERE, ex.toString(), ex);
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
						
						try {
							_upcoming.remove_override();
							((DefaultTableModel) table.getModel()).fireTableCellUpdated(row, column);
						} catch (IOException e1) {
							LOGGER.log(Level.SEVERE, e1.toString(), e1);
						}
						return null;
					}
					
					@Override
					protected void done() {
						try {
							get();
						} catch (Exception ex) {
							LOGGER.log(Level.SEVERE, ex.toString(), ex);
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
				modifier.setModal(true);
				modifier.setVisible(true);				
			}
		});
	}
	
	private void customize() {
		Rule rule = _upcoming.get_rule();
		
		if (rule.is_disabled() || rule.is_override()) {
			_disablerecording.setEnabled(false);
			_addoverride.setEnabled(false);
		}
		
		if (!rule.is_disabled()) _enablerecording.setEnabled(false);
		if (!rule.is_override()) _deleteoverride.setEnabled(false);
		
		if (_upcoming.get_status().getStatusInt() < 0) _addoverride.setEnabled(false);
		else _disablerecording.setEnabled(false);		
	}

	@Override
	public void show(Component invoker, int x, int y) {
		super.show(invoker, x, y);
	}
}
