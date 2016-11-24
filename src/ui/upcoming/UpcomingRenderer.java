package ui.upcoming;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.BevelBorder;

import data.Upcoming;

public class UpcomingRenderer extends DefaultListCellRenderer {
	private static final long serialVersionUID = -83330661590734326L;
	
	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value,
			int index, boolean isSelected, boolean hasFocus) {

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
		}

		return panel;
	}
}