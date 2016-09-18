package ui.guide;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;

import data.GuideProgram;
import data.Rule;
import ui.rule.RuleModifier;

public class GuidePopup extends JPopupMenu {
	private static final long serialVersionUID = -365737674696220771L;
	private JMenuItem _addSchedule;
	private JMenuItem _properties;
	
	public GuidePopup() {
		super();
		
		_addSchedule = new JMenuItem("Add Schedule");
		_properties = new JMenuItem("Properties");

		add(_addSchedule);
		addSeparator();
		add(_properties);

		
		_addSchedule.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JTable table = (JTable) ((JPopupMenu)((JMenuItem) e.getSource()).getParent()).getInvoker();
				JFrame toplevel = (JFrame) table.getTopLevelAncestor();
				GuideProgram program = (GuideProgram) table.getValueAt(table.getSelectedRow(), table.getSelectedColumn());
								
				try {
					Rule newrule = Rule.get_default();
					newrule.load_from_guideprogram(program);
					
					RuleModifier modifier = new RuleModifier(toplevel, newrule);
					modifier.setVisible(true);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
			}
		});
		
		_properties.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JDialog dialog = new JDialog();
				dialog.setVisible(true);
			}
		});
	}

	@Override
	public void show(Component invoker, int x, int y) {
		/*JTable table = (JTable) invoker;
		int row = table.rowAtPoint(new Point(x, y));
		int column = table.columnAtPoint(new Point(x, y));*/
		
		super.show(invoker, x, y);
	}
}
