package ui.rule;

import data.Rule;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RulePopup extends JPopupMenu {
	private static final Logger LOGGER = Logger.getLogger(RulePopup.class.getName());
	private JMenuItem _modifyrule;
	private JMenuItem _enablerule;
	private JMenuItem _disablerule;
	private JMenuItem _deleterule;

	public RulePopup() {
		super();
		
		_modifyrule = new JMenuItem("Modify Rule");
		_enablerule = new JMenuItem("Enable Rule");
		_disablerule = new JMenuItem("Disable Rule");
		_deleterule = new JMenuItem("Delete Rule");
		
		add(_modifyrule);
		addSeparator();
		add(_enablerule);
		add(_disablerule);
		addSeparator();
		add(_deleterule);
		
		_modifyrule.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JMenuItem menuItem = (JMenuItem) e.getSource();
		        JPopupMenu popupMenu = (JPopupMenu) menuItem.getParent();
		        JTable invoker = (JTable) popupMenu.getInvoker();
		        JFrame topLevel = (JFrame) invoker.getTopLevelAncestor();
		        Rule r = (Rule) invoker.getValueAt(invoker.getSelectedRow(), invoker.getSelectedColumn());
		        
				RuleModifier modifier = new RuleModifier(topLevel, r);
				modifier.setVisible(true);
			}
		});
		
		_enablerule.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
					@Override
					protected Void doInBackground() throws Exception {
						JTable table = (JTable) ((JPopupMenu)((JMenuItem) e.getSource()).getParent()).getInvoker();
						int row = table.getSelectedRow();
						int column = table.getSelectedColumn();
						
						try {
							((Rule) table.getValueAt(row, column)).enable(true);
							((DefaultTableModel) table.getModel()).fireTableCellUpdated(row, column);
						} catch (IOException e1) {
							LOGGER.log(Level.SEVERE, e1.toString(), e1);
						}
						return null;
					}	
				};
				
				worker.execute();
			}
		});
		
		_disablerule.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
					@Override
					protected Void doInBackground() throws Exception {
						JTable table = (JTable) ((JPopupMenu)((JMenuItem) e.getSource()).getParent()).getInvoker();
						int row = table.getSelectedRow();
						int column = table.getSelectedColumn();
						
						try {
							((Rule) table.getValueAt(row, column)).enable(false);
							((DefaultTableModel) table.getModel()).fireTableCellUpdated(row, column);
						} catch (IOException e1) {
							LOGGER.log(Level.SEVERE, e1.toString(), e1);
						}
						return null;
					}	
				};
				
				worker.execute();
			}
		});
		
		_deleterule.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JTable table = (JTable) ((JPopupMenu)((JMenuItem) e.getSource()).getParent()).getInvoker();
				int row = table.getSelectedRow();
				int column = table.getSelectedColumn();

				((DefaultTableModel) table.getModel()).removeRow(table.convertRowIndexToModel(row));
				((DefaultTableModel) table.getModel()).fireTableDataChanged();

				SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
					@Override
					protected Void doInBackground() throws Exception {
						try {
							((Rule) table.getValueAt(row, column)).delete();
						} catch (IOException e) {
							JOptionPane.showMessageDialog(null, "Delete failed!", "Delete Rule", JOptionPane.WARNING_MESSAGE);
						}

						return null;
					}
				};
				
				worker.execute();
			}
		});	
	}
	
	@Override
	public void show(Component invoker, int x, int y) {
		JTable table = (JTable) invoker;
		int row = table.rowAtPoint(new Point(x, y));
		int column = table.columnAtPoint(new Point(x, y));
		
		Rule rule = (Rule) table.getValueAt(row, column);
		if (rule.is_inactive()) _disablerule.setEnabled(false);
		else _enablerule.setEnabled(false);
		
		super.show(invoker, x, y);
	}
}
