package ui.rule;

import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import data.Rule;

public class RuleView extends JPanel implements ListSelectionListener, MouseListener {
	private static final long serialVersionUID = -8855317235931128425L;
	private JTable _ruleTable = new JTable();

	public RuleView() {
		init();
		
		setLayout(new BorderLayout());
		add(new JScrollPane(_ruleTable), BorderLayout.CENTER);
		
		_ruleTable.setDefaultRenderer(Rule.class, new RuleRenderer());
		_ruleTable.getSelectionModel().addListSelectionListener(this);
		_ruleTable.addMouseListener(this);
		_ruleTable.setTableHeader(null);
		_ruleTable.setRowHeight(125);
	}
	
	// Download a List of Upcoming Recordings
	private void init() {
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
			    	_ruleTable.setModel(model);
			    } catch (InterruptedException ignore) {
			    } catch (java.util.concurrent.ExecutionException e) {
			            e.printStackTrace();
			    }
			}
		};
		
		worker.execute();
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
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

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}
