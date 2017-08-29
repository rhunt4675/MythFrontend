package ui.rule;

import data.Rule;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RuleModifier extends JDialog implements ActionListener, KeyListener {
	private static final Logger LOGGER = Logger.getLogger(RuleModifier.class.getName());
	
	private JComboBox<Rule.RecordingType> _typecombobox;
	private JComboBox<Rule.RecordingDupMethodType> _dupmethodcombobox;
	private JComboBox<Rule.RecordingDupInType> _dupincombobox;
	private JTextField _startoffsettext, _endoffsettext, _maxepisodestext;
	private JCheckBox _autocommflag, _autotranscode, _autoexpire, _expireoldrecordnew;
	private JButton _ok, _cancel;
	private Rule _rule;

	public RuleModifier(Window owner, Rule r) {
		super(owner);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle("Modify Recording Rule");
		addKeyListener(this);
		
		_rule = r;
		_ok = new JButton("OK");
		_ok.addActionListener(this);
		_ok.setMinimumSize(new Dimension(100, 0));
		_cancel = new JButton("Cancel");
		_cancel.addActionListener(this);
		_cancel.setMinimumSize(new Dimension(100, 0));
		
		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		
		JLabel title = new JLabel(r.get_title());
		title.setFont(new Font("Arial", Font.BOLD, 14));
		JLabel type = new JLabel("Type: ");
		JLabel dupmethod = new JLabel("DupMethod: ");
		JLabel dupin = new JLabel("DupIn: ");
		JLabel startoffset = new JLabel("StartOffset: ");
		JLabel endoffset = new JLabel("EndOffset: ");
		JLabel maxepisodes = new JLabel("MaxEpisodes: ");
		Component strut = Box.createVerticalStrut(10);
		
		List<Rule.RecordingType> types = new ArrayList<Rule.RecordingType>();
		types.addAll(Arrays.asList(Rule.RecordingType.values()));
		types.remove(Rule.RecordingType.NotRecord);
		types.remove(Rule.RecordingType.DontRecord);	
		types.remove(Rule.RecordingType.OverrideRecord);
		types.remove(Rule.RecordingType.TemplateRecord);
		
		_typecombobox = new JComboBox<Rule.RecordingType>(types.toArray(new Rule.RecordingType[0]));
		_typecombobox.setSelectedItem(r.get_type());
		_typecombobox.addKeyListener(this);
		_dupmethodcombobox = new JComboBox<Rule.RecordingDupMethodType>(Rule.RecordingDupMethodType.values());
		_dupmethodcombobox.setSelectedItem(r.get_dupmethod());
		_dupmethodcombobox.addKeyListener(this);
		_dupincombobox = new JComboBox<Rule.RecordingDupInType>(Rule.RecordingDupInType.values());
		_dupincombobox.setSelectedItem(r.get_dupin());
		_dupincombobox.addKeyListener(this);
		_autocommflag = new JCheckBox("Auto-Commflag");
		_autocommflag.setSelected(r.get_auto_commflag());
		_autocommflag.addKeyListener(this);
		_autotranscode = new JCheckBox("Auto-Transcode");
		_autotranscode.setSelected(r.get_auto_transcode());
		_autotranscode.addKeyListener(this);
		_autoexpire = new JCheckBox("Auto-Expire");
		_autoexpire.setSelected(r.get_autoexpire());
		_autoexpire.addKeyListener(this);
		_expireoldrecordnew = new JCheckBox("ExpireOldRecordNew");
		_expireoldrecordnew.setSelected(r.get_expireoldrecordnew());
		_expireoldrecordnew.addKeyListener(this);
		_startoffsettext = new JTextField(Integer.toString(r.get_startoffset()));
		_startoffsettext.addKeyListener(this);
		_endoffsettext = new JTextField(Integer.toString(r.get_endoffset()));
		_endoffsettext.addKeyListener(this);
		_maxepisodestext = new JTextField(Integer.toString(r.get_maxepisodes()));
		_maxepisodestext.addKeyListener(this);
		
		layout.setHorizontalGroup(layout.createParallelGroup(Alignment.CENTER)
			.addComponent(title)
			.addGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(type)
					.addComponent(dupmethod)
					.addComponent(dupin)
					.addComponent(startoffset)
					.addComponent(endoffset)
					.addComponent(maxepisodes))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(_typecombobox)
					.addComponent(_dupmethodcombobox)
					.addComponent(_dupincombobox)
					.addComponent(_startoffsettext)
					.addComponent(_endoffsettext)
					.addComponent(_maxepisodestext)
					.addComponent(_autoexpire)
					.addComponent(_expireoldrecordnew)
					.addComponent(_autocommflag)
					.addComponent(_autotranscode)
					.addComponent(strut)
					.addGroup(layout.createSequentialGroup()
							.addComponent(_cancel)
							.addComponent(_ok)))));

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
					.addComponent(maxepisodes).addComponent(_maxepisodestext))
				.addComponent(_autoexpire)
				.addComponent(_expireoldrecordnew)
				.addComponent(_autocommflag)
				.addComponent(_autotranscode)
				.addComponent(strut)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(_cancel).addComponent(_ok))
		);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		
		pack();
		setLocationRelativeTo(null);
		setResizable(false);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == _ok) {
			_rule.set_autoexpire(_autoexpire.isSelected());
			_rule.set_auto_commflag(_autocommflag.isSelected());
			_rule.set_auto_transcode(_autotranscode.isSelected());
			_rule.set_dupin((Rule.RecordingDupInType) _dupincombobox.getSelectedItem());
			_rule.set_dupmethod((Rule.RecordingDupMethodType) _dupmethodcombobox.getSelectedItem());
			_rule.set_endoffset(Integer.parseInt(_endoffsettext.getText()));
			_rule.set_startoffset(Integer.parseInt(_startoffsettext.getText()));
			_rule.set_maxepisodes(Integer.parseInt(_maxepisodestext.getText()));
			_rule.set_expireoldrecordnew(_expireoldrecordnew.isSelected());
			_rule.set_type((Rule.RecordingType) _typecombobox.getSelectedItem());
			
			try {
				_rule.commit();
			} catch (IOException e1) {
				LOGGER.log(Level.SEVERE, e1.toString(), e1);
			}
		}
		
		this.dispose();
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_ESCAPE:
			dispose();
		}
	}

	@Override public void keyTyped(KeyEvent e) {}
	@Override public void keyPressed(KeyEvent e) {}
}
