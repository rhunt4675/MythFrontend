package ui.upcoming;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.time.format.DateTimeFormatter;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import data.Upcoming;
import data.UpcomingList;

public class UpcomingListEditorRenderer extends AbstractCellEditor
			implements TableCellEditor, TableCellRenderer {
	private static final long serialVersionUID = 8851218008322051385L;
	
	@Override
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		return getLayout(table, value, row, column);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, 
			boolean isSelected, boolean hasFocus, int row, int column) {
		return getLayout(table, value, row, column);
	}
	
	private Component getLayout(JTable table, Object value, 
			int row, int column) {
				
		UpcomingList ulist = (UpcomingList) value;
		
		DefaultListModel<Upcoming> upcomingListModel = new DefaultListModel<Upcoming>();
		for (Upcoming u : ulist.get_upcoming())
			upcomingListModel.addElement(u);

		JList<Upcoming> upcomingList = new JList<Upcoming>();
		upcomingList.setModel(upcomingListModel);
		upcomingList.setCellRenderer(new UpcomingRenderer());
		upcomingList.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		
		JLabel dateLabel = new JLabel(ulist.get_date().format(DateTimeFormatter.ofPattern("EEEE, MMMM d, uuuu")));
		dateLabel.setFont(new Font("Arial", Font.BOLD, 36));
		JLabel counts = new JLabel("Active: " + ulist.get_active() + "; Inactive: " + ulist.get_inactive()
						+ "; Conflicts: " + ulist.get_conflicted() + "; Errored: " + ulist.get_errored());
		counts.setFont(new Font("Arial", Font.BOLD, 18));
		
		GridBagConstraints cons = new GridBagConstraints();
		cons.fill = GridBagConstraints.HORIZONTAL;
		cons.weightx = 1; cons.gridx = 0;
		
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		panel.add(dateLabel, cons);
		panel.add(counts, cons);
		panel.add(upcomingList, cons);
		panel.add(Box.createVerticalStrut(20), cons);
		
		// Resize Row Height
		if (table.getRowHeight(row) != 400) table.setRowHeight(row, 400);
		//int calcHeight = ulist.get_upcoming().size() * 50 + 100;
		//if (table.getRowHeight(row) != calcHeight)
			//table.setRowHeight(row, calcHeight);
		
		return (Component) panel;
	}

	@Override
	public Object getCellEditorValue() {
		return null;
	}
}