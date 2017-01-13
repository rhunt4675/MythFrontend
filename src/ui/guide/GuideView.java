package ui.guide;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import data.Channel;
import data.GuideProgram;
import data.VideoSource;
import ui.ContentView;

public class GuideView extends ContentView implements ListSelectionListener, ActionListener, MouseListener, KeyListener {
	private static final long serialVersionUID = -4244429202057252386L;
	private JTable _guideTable = new JTable();
	
	private JPanel _settingsPanel = new JPanel();
	private JComboBox<VideoSource> _sources;
	private JComboBox<LocalDate> _day;
	private JComboBox<LocalTime> _hour;
	private JButton _submit;
	
	private int _divisions = 6;
		
	public GuideView() {
		setLayout(new BorderLayout());
		add(_settingsPanel, BorderLayout.NORTH);
		add(new JScrollPane(_guideTable), BorderLayout.CENTER);
		
		_guideTable.setModel(new DefaultTableModel());
		_guideTable.getSelectionModel().addListSelectionListener(this);
		_guideTable.setDefaultEditor(Object.class, null);
		_guideTable.addMouseListener(this);
		_guideTable.getTableHeader().setReorderingAllowed(false);
		_guideTable.getTableHeader().setResizingAllowed(false);
		_guideTable.setRowHeight(100);
	}
	
	@Override
	public void init() {		
		_sources = new JComboBox<VideoSource>();
		_day = new JComboBox<LocalDate>();
		_hour = new JComboBox<LocalTime>();
		_submit = new JButton("Submit");
		_submit.addActionListener(this);
		
		_settingsPanel.removeAll();
		_settingsPanel.setLayout(new BoxLayout(_settingsPanel, BoxLayout.LINE_AXIS));
		_settingsPanel.add(new JLabel("Channel Source: "));
		_settingsPanel.add(_sources);
		_settingsPanel.add(Box.createVerticalStrut(50));
		_settingsPanel.add(Box.createHorizontalGlue());
		_settingsPanel.add(_day);
		_settingsPanel.add(Box.createHorizontalStrut(50));
		_settingsPanel.add(_hour);
		_settingsPanel.add(Box.createHorizontalStrut(50));
		_settingsPanel.add(_submit);
		
		SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				List<VideoSource> source_list = null;
				LocalDate first_program = LocalDate.now();
				LocalDate last_program = LocalDate.now();
				LocalTime earliest = LocalTime.MIN;
				
				try {
					source_list = VideoSource.get_videosources();
					first_program = GuideProgram.get_earliest_program();
					last_program = GuideProgram.get_latest_program();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if (source_list == null || first_program == null || last_program == null)
						return null;
				}
				
				do {
					_day.addItem(first_program);
				} while ((first_program = first_program.plusDays(1)).isBefore(last_program));
				
				do {
					_hour.addItem(earliest);
				} while ((earliest = earliest.plusHours(1)).isAfter(LocalTime.MIN));
				
				if (source_list != null)
					for (VideoSource source : source_list)
						_sources.addItem(source);
				_sources.setMaximumSize(_sources.getPreferredSize());
				/*_day.setMaximumSize(_day.getPreferredSize());
				_hour.setMaximumSize(_hour.getPreferredSize());*/
				
				LocalDateTime now = LocalDateTime.now();
				_day.setSelectedItem(now.toLocalDate());
				_hour.setSelectedItem(now.toLocalTime().withMinute(0).withSecond(0).withNano(0));
				_submit.doClick();
				
				return null;
			}
			
			@Override
			protected void done() {
				try {
					get();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		worker.execute();
	}
	
	private void fill_guidetable(VideoSource source, ZonedDateTime start, ZonedDateTime end, int divisions) {
		SwingWorker<List<Channel>, Void> worker = new SwingWorker<List<Channel>, Void>() {

			@Override
			protected List<Channel> doInBackground() throws IOException {
				if (source.get_channels() == null)
					source.set_channels(Channel.get_channels(source.get_id()));
				return source.get_channels();
			}
			
			@Override
			protected void done() {
				try {
					List<Channel> channels = get();
					fill_guidetablehelper(channels, start, end, divisions);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		
		worker.execute();
	}
	
	private void fill_guidetablehelper(List<Channel> channels, ZonedDateTime start, ZonedDateTime end, int divisions) {
		long diff_seconds = end.toEpochSecond() - start.toEpochSecond();
		long interval_seconds = diff_seconds / divisions;
		
		DefaultTableModel model = (DefaultTableModel) (_guideTable.getModel());
		model.setRowCount(0); model.setColumnCount(0);
		model.setColumnCount(divisions + 1);
		model.setRowCount(channels.size());
		
		_guideTable.getColumnModel().getColumn(0).setCellRenderer(new GuideChannelRenderer());
		_guideTable.getColumnModel().getColumn(0).setHeaderValue(null);
		for (int i = 1; i < divisions + 1; i++) {
			_guideTable.getColumnModel().getColumn(i).setCellRenderer(new GuideProgramRenderer());
			_guideTable.getColumnModel().getColumn(i).setHeaderValue(start.plusSeconds(interval_seconds * (i - 1))
					.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime().toString());
		}
		_guideTable.getTableHeader().repaint();
		
		SwingWorker<Void, List<GuideProgram>> worker = new SwingWorker<Void, List<GuideProgram>>() {
			
			@Override
			protected Void doInBackground() throws IOException {				
				for (Channel channel : channels) {
					List<GuideProgram> guide = GuideProgram.get_guideprogram_list(channel.get_chanid(), start, end);
					model.setValueAt(channel, channels.indexOf(channel), 0);

					publish(guide);
				}
				
				return null;
			}
			
			@Override
			protected void process(List<List<GuideProgram>> guide) {
				for(List<GuideProgram> channelguide : guide) {
					if (channelguide.isEmpty())
						continue;
					
					Channel channel = channelguide.get(0).get_channel();
					int chanIndex = channels.indexOf(channel);
					int guideIndex = 0;
					
					for (int i = 0; i < divisions; i++) {
						if (guideIndex < 0 || guideIndex >= channelguide.size()) {
							System.err.println("Missing data, chanIndex=" + chanIndex); break;
						}
						
						GuideProgram program = channelguide.get(guideIndex);
						ZonedDateTime progstart = program.get_starttime();
						ZonedDateTime progend = program.get_endtime();
						ZonedDateTime dividerleft = start.plusSeconds(interval_seconds * i);
						ZonedDateTime dividerright = start.plusSeconds(interval_seconds * (i + 1));
						
						if (progstart.compareTo(dividerright) >= 0) {
							guideIndex--; i--; continue;
						} else if (progend.compareTo(dividerleft) <= 0) {
							guideIndex++; i--; continue;
						} else {
							model.setValueAt(program, chanIndex, i + 1);
						}
					}
				}
			}
			
			@Override
			protected void done() {
				try {
					get();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		
		worker.execute();
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (SwingUtilities.isRightMouseButton(e)) {
			JTable table = (JTable) e.getSource();
			int row = table.rowAtPoint(e.getPoint());
			int column = table.columnAtPoint(e.getPoint());
			table.setRowSelectionInterval(row, row);
			table.setColumnSelectionInterval(column, column);
			
			if (e.isPopupTrigger()) {
				JPopupMenu menu = new GuidePopup();
				menu.show(table, e.getX(), e.getY());
			}
		} else if (e.getClickCount() == 2) {
			// Double Click
		}		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		VideoSource source = (VideoSource) _sources.getSelectedItem();
		ZonedDateTime start = ZonedDateTime.of((LocalDate) _day.getSelectedItem(), (LocalTime) _hour.getSelectedItem(),
				ZoneId.systemDefault()).withZoneSameInstant(ZoneOffset.UTC);
		ZonedDateTime end = start.plusHours(3);
		
		if (source != null)
			fill_guidetable(source, start, end, _divisions);		
	}
}
