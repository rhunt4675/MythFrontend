package ui.rule;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableCellRenderer;

import data.Rule;

public class RuleRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = -3126153108568269763L;

	public RuleRenderer() {
		setOpaque(true);
	}
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		JPanel panel = new JPanel();
		Rule r = (Rule) value;
		
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		
		JLabel label = new JLabel();
		JTextArea subtitle = new JTextArea(r.get_title());
		subtitle.setFont(new Font("Arial", Font.BOLD, 18));
		subtitle.setEditable(false);
		subtitle.setLineWrap(true);
		subtitle.setWrapStyleWord(true);
		subtitle.setBorder(label.getBorder());
		subtitle.setBackground(label.getBackground());
		subtitle.setForeground(label.getForeground());
		subtitle.setOpaque(label.isOpaque());
		
		panel.add(new JLabel("Title: " + r.get_title()));
		panel.add(new JLabel("Type: " + r.get_type().name()));
		panel.add(new JLabel("AutoCommFlage: " + r.get_auto_commflag()));
		panel.add(new JLabel("AutoTranscode: " + r.get_auto_transcode()));
		panel.add(new JLabel("Active: " + (r.is_inactive() ? "false" : "true")));
		
		if (! r.is_inactive()) {
			panel.setBackground(Color.GREEN);
		}

		return panel;
	}
}