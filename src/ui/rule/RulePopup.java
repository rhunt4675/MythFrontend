package ui.rule;

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

import data.Rule;

public class RulePopup extends JPopupMenu {
	private static final long serialVersionUID = -1370490057232727856L;
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
							e1.printStackTrace();
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
							e1.printStackTrace();
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
				SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
					@Override
					protected Void doInBackground() throws Exception {
						JTable table = (JTable) ((JPopupMenu)((JMenuItem) e.getSource()).getParent()).getInvoker();
						int row = table.getSelectedRow();
						int column = table.getSelectedColumn();
						
						try {
							((Rule) table.getValueAt(row, column)).delete();
							((DefaultTableModel) table.getModel()).removeRow(row);
							((DefaultTableModel) table.getModel()).fireTableRowsDeleted(row, row);
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
}
