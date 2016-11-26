package ui.upcoming;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.border.BevelBorder;
import javax.swing.table.TableCellRenderer;

import data.Upcoming;
import data.UpcomingList;
import ui.recording.RecordingPopup;

public class UpcomingListRenderer implements TableCellRenderer {
	private static final int _labelHeight = 75;
	private static final int _cellHeight = 75;
	private static final int _strutHeight = 30;
	private static final int _insetWidth = 40;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, 
			boolean isSelected, boolean hasFocus, int row, int column) {
		return getLayout(table, value, row, column);
	}
	
	private Component getLayout(JTable table, Object value, 
			int row, int column) {

		JPanel panel = new JPanel();
		UpcomingList ulist = (UpcomingList) value;
		
		// Date & Upcoming Counts
		JLabel dateLabel = new JLabel(ulist.get_date().format(DateTimeFormatter.ofPattern("EEEE, MMMM d, uuuu")));
		dateLabel.setFont(new Font("Arial", Font.BOLD, 36));
		JLabel counts = new JLabel("Active: " + ulist.get_active() + "; Inactive: " + ulist.get_inactive()
						+ "; Conflicts: " + ulist.get_conflicted() + "; Errored: " + ulist.get_errored());
		counts.setFont(new Font("Arial", Font.BOLD, 18));
		
		// Label Container
		JPanel label = new JPanel();
		label.setLayout(new BoxLayout(label, BoxLayout.Y_AXIS));
		label.add(dateLabel);
		label.add(counts);
		label.setPreferredSize(new Dimension(0, _labelHeight));
		
		// Various Layout Constraints
		GridBagConstraints cons = new GridBagConstraints(), cons2 = new GridBagConstraints();
		cons.fill = GridBagConstraints.HORIZONTAL; cons.weightx = 1; cons.gridx = 0;
		cons2.fill = GridBagConstraints.HORIZONTAL; cons2.weightx = 1; cons2.gridx = 0; cons2.insets = new Insets(0, _insetWidth, 0, _insetWidth);
		
		// Final Cell Composition
		panel.setLayout(new GridBagLayout());
		panel.add(label, cons);
		boolean firstInList = true;
		for (Upcoming u : ulist.get_upcoming()) {
			JPanel p = getUpcomingPanel(u, false, firstInList); firstInList = false;
			p.setPreferredSize(new Dimension(0, _cellHeight));
			panel.add(p, cons2);
		}
		panel.add(Box.createVerticalStrut(_strutHeight), cons);
		
		// Resize Row
		int calcHeight = ulist.get_upcoming().size() * _cellHeight + _labelHeight + _strutHeight + 1;
		if (table.getRowHeight(row) != calcHeight)
			table.setRowHeight(row, calcHeight);

		return (Component) panel;
	}
	
	private JPanel getUpcomingPanel(Object value, boolean isSelected, boolean firstInList) {
		JPanel panel = new JPanel();
		Upcoming u = (Upcoming) value;
		
		JLabel label = new JLabel();
		JTextArea title = new JTextArea(u.get_starttime()
				.withZoneSameInstant(ZoneId.systemDefault()).toLocalTime()
				.format(DateTimeFormatter.ofPattern("hh:mm a")) + " - " + u.get_title());
		title.setFont(new Font("Arial", Font.BOLD, 18));
		title.setEditable(false);
		title.setLineWrap(true);
		title.setWrapStyleWord(true);
		title.setBorder(label.getBorder());
		title.setBackground(label.getBackground());
		title.setForeground(label.getForeground());
		title.setOpaque(label.isOpaque());
		
		JTextArea subtitle = new JTextArea(u.get_subtitle() + "\n" + u.get_status().name());
		subtitle.setFont(new Font("Arial", Font.PLAIN, 14));
		subtitle.setEditable(false);
		subtitle.setLineWrap(true);
		subtitle.setWrapStyleWord(true);
		subtitle.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 0));
		subtitle.setBackground(label.getBackground());
		subtitle.setForeground(label.getForeground());
		subtitle.setOpaque(label.isOpaque());
		
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(Box.createVerticalStrut(5));
		panel.add(title);
		panel.add(subtitle);
		
		if (u.get_rule().is_override()) {
			panel.setBackground(Color.YELLOW);
		} else if (u.get_status().getStatusInt() < 0) {
			panel.setBackground(Color.GREEN);
		}
		
		if (isSelected) {
			panel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		} else {
			panel.setBorder(BorderFactory.createMatteBorder(firstInList ? 1 : 0, 1, 1, 1, Color.BLACK));
		}

		panel.setComponentPopupMenu(new RecordingPopup());
		return panel;
	}
	
	public static int indexFromCoordinates(int x, int y, Rectangle r) {
		if (y < _labelHeight + r.y || y > r.y + r.height - _strutHeight 
				|| x < _insetWidth + r.x || x > r.x + r.width - _insetWidth)
			return -1;
		
		return (y - r.y - _labelHeight) / _cellHeight;
	}
}