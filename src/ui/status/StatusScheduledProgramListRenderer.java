package ui.status;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.time.ZoneId;

import javax.swing.JEditorPane;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import data.StatusProgram;

public class StatusScheduledProgramListRenderer implements ListCellRenderer<StatusProgram> {

	@Override
	public Component getListCellRendererComponent(JList<? extends StatusProgram> list, StatusProgram value, int index,
			boolean isSelected, boolean cellHasFocus) {
		
		JEditorPane textarea = new JEditorPane("text/html", "");
		textarea.setFont(new Font("Arial", Font.PLAIN, 15));
		textarea.setText("&#9702; " + value.get_starttime().withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime() 
				+ " - <b>" + value.get_title() + "</b> : " + value.get_subtitle());
		
		JPanel content = new JPanel();
		content.setLayout(new BorderLayout());
		content.add(textarea, BorderLayout.CENTER);
		return content;
	}
}
