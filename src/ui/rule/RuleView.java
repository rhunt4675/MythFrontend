package ui.rule;

import data.Rule;
import ui.ContentView;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.List;

public class RuleView extends ContentView implements ListSelectionListener, MouseListener {
	private static final long serialVersionUID = -8855317235931128425L;
	private JTable _ruleTable = new JTable();

	public RuleView() {
		setLayout(new BorderLayout());
		add(new JScrollPane(_ruleTable), BorderLayout.CENTER);
		
		_ruleTable.setDefaultRenderer(Rule.class, new RuleRenderer());
		_ruleTable.getSelectionModel().addListSelectionListener(this);
		_ruleTable.addMouseListener(this);
		_ruleTable.setTableHeader(null);
		_ruleTable.setRowHeight(125);
	}
	
	// Download a List of Upcoming Recordings
	@Override
	public void init() {
		SwingWorker<DefaultTableModel, Void> worker = new SwingWorker<DefaultTableModel, Void>() {

			@Override
			protected DefaultTableModel doInBackground() {
				DefaultTableModel model;
				
				try {
					List<Rule> rules = Rule.get_rules();
					model = new DefaultTableModel() {
						private static final long serialVersionUID = -4009413705354498771L;

						@Override
						public int getRowCount() {
							return rules.size();
						}

						@Override
						public int getColumnCount() {
							return 1;
						}

						@Override
						public Object getValueAt(int rowIndex, int columnIndex) {
							return rules.get(rowIndex);
						}
						
						@Override
						public Class<?> getColumnClass(int columnIndex) {
							return Rule.class;
						}
					};
					
				} catch (IOException e) {
					e.printStackTrace();
					model = null;
				}
				
				return model;
			}
			
			@Override
			protected void done() {
			    try {
			    	DefaultTableModel model = get();
			    	if (model != null) _ruleTable.setModel(model);
			    } catch (InterruptedException ignore) {
			    } catch (java.util.concurrent.ExecutionException e) {
			            e.printStackTrace();
			    }
			}
		};
		
		worker.execute();
	}

	@Override public void valueChanged(ListSelectionEvent e) {}
	@Override public void mouseClicked(MouseEvent e) {}
	@Override public void mouseReleased(MouseEvent e) {}
	@Override public void mouseEntered(MouseEvent e) {}
	@Override public void mouseExited(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {
		if (SwingUtilities.isRightMouseButton(e)) {
			JTable table = (JTable) e.getSource();
			int row = table.rowAtPoint(e.getPoint());
			table.getSelectionModel().setSelectionInterval(row, row);
			
			if (e.isPopupTrigger()) {
				JPopupMenu menu = new RulePopup();
				menu.show(table, e.getX(), e.getY());
			}
		} else if (e.getClickCount() == 2) {
            // Refresh Rule Table
			init();
        }
	}
}
