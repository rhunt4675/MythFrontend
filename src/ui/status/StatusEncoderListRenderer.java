package ui.status;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JEditorPane;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import data.Encoder;
import data.StatusProgram;

public class StatusEncoderListRenderer implements ListCellRenderer<Encoder> {

	@Override
	public Component getListCellRendererComponent(JList<? extends Encoder> list, Encoder value, int index,
			boolean isSelected, boolean cellHasFocus) {
		JEditorPane textarea = new JEditorPane("text/html", "");
		String text = "<b>Encoder #" + value.get_id() + ": </b>" + value.get_devlabel();
		for (StatusProgram sp : value.get_inProgressPrograms())
			text += "<br>&#09;&#9702;<b>" + sp.get_title() + "</b> : " + sp.get_subtitle(); 
		textarea.setText(text);
		
		JPanel content = new JPanel();
		content.setLayout(new BorderLayout());
		content.add(textarea, BorderLayout.CENTER);
		return content;
	}

}
