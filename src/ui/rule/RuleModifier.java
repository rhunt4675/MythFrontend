package ui.rule;

import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.GroupLayout.Alignment;

import data.Rule;

public class RuleModifier extends JDialog implements ActionListener {
	private static final long serialVersionUID = -8685640624319272471L;
	
	private JComboBox<Rule.RecordingType> _typecombobox;
	private JComboBox<Rule.RecordingDupMethodType> _dupmethodcombobox;
	private JComboBox<Rule.RecordingDupInType> _dupincombobox;
	private JTextField _startoffsettext, _endoffsettext;
	private JCheckBox _autocommflag, _autotranscode;
	private JButton _ok, _cancel;
	private Rule _rule;

	public RuleModifier(Window owner, Rule r) {
		super(owner);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle("Modify Recording Rule");
		
		_rule = r;
		_ok = new JButton("OK");
		_ok.addActionListener(this);
		_cancel = new JButton("Cancel");
		_cancel.addActionListener(this);
		
		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		
		JLabel title = new JLabel(r.get_title());
		title.setFont(new Font("Arial", Font.BOLD, 14));
		JLabel type = new JLabel("Type: ");
		JLabel dupmethod = new JLabel("DupMethod: ");
		JLabel dupin = new JLabel("DupIn: ");
		JLabel startoffset = new JLabel("StartOffset: ");
		JLabel endoffset = new JLabel("EndOffset: ");
		
		List<Rule.RecordingType> types = new ArrayList<Rule.RecordingType>();
		types.addAll(Arrays.asList(Rule.RecordingType.values()));
		types.remove(Rule.RecordingType.NotRecord);
		types.remove(Rule.RecordingType.DontRecord);	
		types.remove(Rule.RecordingType.OverrideRecord);
		types.remove(Rule.RecordingType.TemplateRecord);
		
		_typecombobox = new JComboBox<Rule.RecordingType>(types.toArray(new Rule.RecordingType[0]));
		_typecombobox.setSelectedItem(r.get_type());
		_dupmethodcombobox = new JComboBox<Rule.RecordingDupMethodType>(Rule.RecordingDupMethodType.values());
		_dupmethodcombobox.setSelectedItem(r.get_dupmethod());
		_dupincombobox = new JComboBox<Rule.RecordingDupInType>(Rule.RecordingDupInType.values());
		_dupincombobox.setSelectedItem(r.get_dupin());
		_autocommflag = new JCheckBox("Auto-Commflag");
		_autocommflag.setSelected(r.get_auto_commflag());
		_autotranscode = new JCheckBox("Auto-Transcode");
		_autotranscode.setSelected(r.get_auto_transcode());
		_startoffsettext = new JTextField(Integer.toString(r.get_startoffset()));
		_endoffsettext = new JTextField(Integer.toString(r.get_endoffset()));
		
		layout.setHorizontalGroup(layout.createParallelGroup(Alignment.CENTER)
			.addComponent(title)
			.addGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(type)
					.addComponent(dupmethod)
					.addComponent(dupin)
					.addComponent(startoffset)
					.addComponent(endoffset)
					.addComponent(_cancel))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(_typecombobox)
					.addComponent(_dupmethodcombobox)
					.addComponent(_dupincombobox)
					.addComponent(_startoffsettext)
					.addComponent(_endoffsettext)
					.addComponent(_autocommflag)
					.addComponent(_autotranscode)
					.addComponent(_ok))));

		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(title)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(type).addComponent(_typecombobox))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(dupmethod).addComponent(_dupmethodcombobox))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(dupin).addComponent(_dupincombobox))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(startoffset).addComponent(_startoffsettext))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(endoffset).addComponent(_endoffsettext))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(_autocommflag))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(_autotranscode))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(_cancel).addComponent(_ok))
		);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		
		pack();
		setResizable(false);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == _ok) {
			_rule.set_auto_commflag(_autocommflag.isSelected());
			_rule.set_auto_transcode(_autotranscode.isSelected());
			_rule.set_dupin((Rule.RecordingDupInType) _dupincombobox.getSelectedItem());
			_rule.set_dupmethod((Rule.RecordingDupMethodType) _dupmethodcombobox.getSelectedItem());
			_rule.set_endoffset(Integer.parseInt(_endoffsettext.getText()));
			_rule.set_startoffset(Integer.parseInt(_startoffsettext.getText()));
			_rule.set_type((Rule.RecordingType) _typecombobox.getSelectedItem());
			
			try {
				_rule.commit();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
		this.dispose();
	}
}
