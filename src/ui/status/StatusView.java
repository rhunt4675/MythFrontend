package ui.status;

import java.awt.BorderLayout;
import java.awt.Font;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingWorker;

import data.Encoder;
import data.MachineInfo.StorageGroup;
import data.Status;
import data.StatusProgram;
import ui.ContentView;

public class StatusView extends ContentView {
	private static final long serialVersionUID = 1560578633736651326L;
	private JList<StatusProgram> _scheduledProgramList;
	private JList<Encoder> _encoderList;
	
	private Status _status;
	
	@Override
	public void init() {		
		SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				_status = Status.get_status();
				return null;
			}
			
			@Override
			protected void done() {
				try {
					get();
					generateLayout();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		
		worker.execute();
	}
	
	private void generateLayout() {
		JLabel machineInfoLabel = new JLabel("Machine Info");
		machineInfoLabel.setFont(new Font("Arial", Font.BOLD, 36));
		
		JEditorPane machineInfoText = new JEditorPane("text/html", "");
		String text = "<b>Machine Load:</b> " + Arrays.toString(_status.get_machineinfo().get_machineload()) + "<br>"
				+ "<b>Guide Thru:</b> " + _status.get_machineinfo().get_guideDataThru() + " (" + _status.get_machineinfo().get_guideDays() + " Days)<br>"
				+ "<b>Guide Fill:</b> " + _status.get_machineinfo().get_guideStatus() + " (Start=" + _status.get_machineinfo().get_guideUpdateStart() + "; End=" + _status.get_machineinfo().get_guideUpdateEnd() + ")<br>"
				+ "<b>Storage:</b> " + _status.get_machineinfo().get_total().get_total() + "MB Total; " + _status.get_machineinfo().get_total().get_used() + "MB Used; " + _status.get_machineinfo().get_total().get_free() + "MB Free<br>";
		for (StorageGroup sg : _status.get_machineinfo().get_stores())
			text += "&#09;&#9702; <b>Drive #" + sg.get_id() + "</b> (" + sg.get_dir() + "): " + sg.get_total() + "MB Total; " + sg.get_used() + "MB Used; " + sg.get_free() + "MB Free<br>";
		machineInfoText.setText(text);
		machineInfoText.setFont(new Font("Arial", Font.PLAIN, 15));
		
		JLabel scheduledProgramLabel = new JLabel("Scheduled Programs");
		scheduledProgramLabel.setFont(new Font("Arial", Font.BOLD, 36));
		
		_scheduledProgramList = new JList<StatusProgram>();
		_scheduledProgramList.setModel(new DefaultListModel<StatusProgram>());
		for (StatusProgram sp : _status.get_schedules())
			((DefaultListModel<StatusProgram>) _scheduledProgramList.getModel()).addElement(sp);
		_scheduledProgramList.setCellRenderer(new StatusScheduledProgramListRenderer());
		JPanel scheduledProgramListPanel = new JPanel(new BorderLayout());
		scheduledProgramListPanel.add(_scheduledProgramList);
		
		JLabel encodersLabel = new JLabel("Encoders");
		encodersLabel.setFont(new Font("Arial", Font.BOLD, 36));
		
		_encoderList = new JList<Encoder>();
		_encoderList.setModel(new DefaultListModel<Encoder>());
		for (Encoder e : _status.get_encoders())
			((DefaultListModel<Encoder>) _encoderList.getModel()).addElement(e);
		_encoderList.setCellRenderer(new StatusEncoderListRenderer());
		JPanel encoderListPanel = new JPanel(new BorderLayout());
		encoderListPanel.add(_encoderList);
		
		JPanel topPane = new JPanel();
		GroupLayout layout = new GroupLayout(topPane);
		layout.setHorizontalGroup(
			layout.createParallelGroup()
				.addComponent(machineInfoLabel)
				.addComponent(machineInfoText)
				.addComponent(scheduledProgramLabel)
				.addComponent(scheduledProgramListPanel)
				.addComponent(encodersLabel)
				.addComponent(encoderListPanel));
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(machineInfoLabel)
				.addComponent(machineInfoText)
				.addGap(25)
				.addComponent(scheduledProgramLabel)
				.addComponent(scheduledProgramListPanel)
				.addGap(25)
				.addComponent(encodersLabel)
				.addComponent(encoderListPanel));
		topPane.setLayout(layout);
		topPane.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
		
		JPanel bottomPane = new JPanel();
		bottomPane.setBorder(BorderFactory.createEmptyBorder());
				
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setTopComponent(topPane);
		splitPane.setBottomComponent(bottomPane);
		splitPane.setDividerSize(0);
		JScrollPane scrollPane = new JScrollPane(splitPane);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		add(scrollPane);
	}
}
