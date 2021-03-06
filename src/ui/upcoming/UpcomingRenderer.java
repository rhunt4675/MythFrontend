package ui.upcoming;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.border.BevelBorder;
import javax.swing.table.DefaultTableCellRenderer;

import data.Upcoming;

public class UpcomingRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = -83330661590734326L;

	public UpcomingRenderer() {
		setOpaque(true);
	}
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		JPanel panel = new JPanel();
		Upcoming u = (Upcoming) value;
		
		JLabel label = new JLabel();
		JTextArea title = new JTextArea(ZonedDateTime.ofLocal(u.get_starttime(), ZoneOffset.UTC, null)
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
		}

		return panel;
	}
}