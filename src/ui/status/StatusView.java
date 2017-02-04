package ui.status;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

import data.Status;
import ui.ContentView;

public class StatusView extends ContentView {
	private static final long serialVersionUID = 1560578633736651326L;
	private JTextArea _text;
	
	public StatusView() {
		_text = new JTextArea();
		_text.setFont(new Font("Arial", Font.PLAIN, 12));
		_text.setEditable(false);
		_text.setLineWrap(true);
		_text.setWrapStyleWord(true);
		
		
		setLayout(new BorderLayout());
		add(new JScrollPane(_text), BorderLayout.CENTER);
	}
	
	@Override
	public void init() {		
		SwingWorker<Status, Void> worker = new SwingWorker<Status, Void>() {
			@Override
			protected Status doInBackground() throws Exception {
				Status status = Status.get_status();
				return status;
			}
			
			@Override
			protected void done() {
				try {
					Status status = get();
					String text = "Machine Info: " + status.get_machineinfo()
						+ "\n\nJob Queue: " + "(add this here!!!)"
						+ "\n\nEncoders: " + status.get_encoders()
						+ "\n\nSchedules: " + status.get_schedules();
					
					_text.setText(text);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		
		worker.execute();
	}
}
