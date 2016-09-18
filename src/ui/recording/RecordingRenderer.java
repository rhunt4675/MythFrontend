package ui.recording;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import data.Channel;
import data.Recording;
import data.Recording.Artwork;

public class RecordingRenderer extends DefaultTableCellRenderer implements ActionListener {

	private static final long serialVersionUID = 7124170992120515156L;
	private static final int /*_selectedCellHeight = 400,*/ _unselectedCellHeight = 100, _channelIconHeight = 85;
	
	private static final Dimension _previewDimension = new Dimension((_unselectedCellHeight * 16) / 9, _unselectedCellHeight);
	private static final Dimension _bannerDimension = new Dimension(0, _unselectedCellHeight);
	//private static final Dimension _fanartDimension = new Dimension(0, _selectedCellHeight);
	private static final Dimension _channelDimension = new Dimension(0, _channelIconHeight);

	public RecordingRenderer() {
		setOpaque(true);
	}
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		Recording r = (Recording) value;
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		
		if (table.getRowHeight(row) != _unselectedCellHeight)
			table.setRowHeight(row, _unselectedCellHeight);
		
		ImageIcon preview = downloadRecordingArtworkAsync(table, row, column, r, Artwork.PREVIEW, _previewDimension);
		ImageIcon banner = downloadRecordingArtworkAsync(table, row, column, r, Artwork.BANNER, _bannerDimension);
		ImageIcon channelicon = downloadChannelIconAsync(table, row, column, r.get_channel(), _channelDimension);
		
		JLabel label = new JLabel();
		JTextArea subtitle = new JTextArea(((r.get_season() != 0 && r.get_episode() != 0) 
				? (r.get_season() + "x" + String.format("%02d - ",r.get_episode())) : " ")
				+ (r.get_subtitle().isEmpty() ? r.get_title() : r.get_subtitle()));
		subtitle.setFont(new Font("Arial", Font.BOLD, 18));
		subtitle.setEditable(false);
		subtitle.setLineWrap(true);
		subtitle.setWrapStyleWord(true);
		subtitle.setBorder(label.getBorder());
		subtitle.setBackground(label.getBackground());
		subtitle.setForeground(label.getForeground());
		subtitle.setOpaque(label.isOpaque());

		JTextArea description = new JTextArea(r.get_description() + "\n\n" 
				+ "\t(" + r.get_starttime().toLocalDate().format(DateTimeFormatter.ofPattern("EEEE, MMM d, uuuu")) + "\t" + r.get_filesize() + "B)");
		description.setFont(new Font("Arial", Font.PLAIN, 12));
		description.setEditable(false);
		description.setLineWrap(true);
		description.setWrapStyleWord(true);
		description.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 0));
		description.setBackground(label.getBackground());
		description.setForeground(label.getForeground());
		description.setOpaque(label.isOpaque());
		
		JLabel channel = new JLabel(channelicon);
		channel.setAlignmentX(Component.CENTER_ALIGNMENT);
		JLabel channame = new JLabel("(" + r.get_channel().get_channame() + ")");
		channame.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		Box textcontent = Box.createVerticalBox();
		textcontent.add(Box.createVerticalStrut(10));
		textcontent.add(subtitle);
		textcontent.add(description);
		textcontent.add(Box.createGlue());
		
		Box programcontent = Box.createVerticalBox();
		programcontent.setOpaque(true);
		programcontent.setBackground(Color.WHITE);
		programcontent.add(channel);
		programcontent.add(channame);
					
		Box content = Box.createHorizontalBox();
		if (preview != null) content.add(new JLabel(preview));
		content.add(Box.createHorizontalStrut(10));
		content.add(textcontent);
		content.add(Box.createHorizontalStrut(10));
		content.add(programcontent);
		content.add(new JLabel(banner));
		
		/*JPanel unwatched = new JPanel();
		unwatched.setBackground(r.is_watched() ? Color.WHITE : Color.RED);
		unwatched.setPreferredSize(new Dimension(25, 100));*/

		panel.add(content, BorderLayout.CENTER);
		//panel.add(unwatched, BorderLayout.EAST);

		if (isSelected) {
			panel.setBackground(Color.LIGHT_GRAY);
		} else {
			panel.setBackground(Color.WHITE);
		}
		
		/*if (table.getRowHeight(row) != _selectedCellHeight)
		table.setRowHeight(row, _selectedCellHeight);
		
		JLabel title = new JLabel("\t" + r.get_title());
		title.setFont(new Font("Arial", Font.BOLD, 36));
		
		JLabel subtitle = new JLabel("S" + r.get_season() + "E" + r.get_episode() + ": " + r.get_subtitle());
		subtitle.setFont(new Font("Arial", Font.BOLD, 18));
		
		JPanel info = new JPanel();
		info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
		info.add(title);
		info.add(subtitle);
		info.add(new JLabel(r.get_description()));
		info.add(Box.createRigidArea(new Dimension(0, 50)));
		info.add(new JLabel("Airdate: " + r.get_starttime()));
		info.add(new JLabel("Channel: " + r.get_channel().get_chanid()));
		info.add(new JLabel("Filesize: " + r.get_filesize()));
		info.add(new JLabel("RecordedID: " + r.get_recordedid()));
		info.add(new JLabel("Watched: " + (r.is_watched() ? "true" : "false")));
	
		ImageIcon fanartImage = downloadRecordingArtworkAsync(table, row, column, r, Artwork.FANART, _fanartDimension);
		JPanel fanart = new JPanel();
		if (fanartImage != null) fanart.add(new JLabel(new AlphaImageIcon(fanartImage, 0.9F)));
		fanart.setBorder(BorderFactory.createEmptyBorder(-5, -5, -5, -5));
		//fanart.setSize(new Dimension(_selectedCellHeight * 16 / 9, _selectedCellHeight)); // 16x9 dimension is worst case width
		
		panel.setBackground(Color.LIGHT_GRAY);
		panel.add(info, BorderLayout.CENTER);
		panel.add(fanart, BorderLayout.EAST);*/
		
		return panel;
	}
	
	private ImageIcon downloadRecordingArtworkAsync(JTable table, int row, int column, Recording recording, 
			Artwork type, Dimension dimension) {
		
		if (recording.artwork_downloaded(type, dimension)) {
			try {
				return recording.get_artwork(type, dimension);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
	
				@Override
				protected Void doInBackground() throws Exception {
					recording.get_artwork(type, dimension);
					((DefaultTableModel) table.getModel()).fireTableCellUpdated(row, column);
					return null;
				}
				
				@Override
				protected void done() {
					try {
						get();
					} catch (InterruptedException | ExecutionException e) {
						e.printStackTrace();
					}
				}
			};
			
			worker.execute();
		}
		return null;
	}
	
	private ImageIcon downloadChannelIconAsync(JTable table, int row, int column, Channel channel, Dimension dimension) {
		if (channel.artwork_downloaded(dimension)) {
			try {
				return channel.get_artwork(dimension);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
	
				@Override
				protected Void doInBackground() throws Exception {
					channel.get_artwork(dimension);
					((DefaultTableModel) table.getModel()).fireTableCellUpdated(row, column);
					return null;
				}
				
				@Override
				protected void done() {
					try {
						get();
					} catch (InterruptedException | ExecutionException e) {
						e.printStackTrace();
					}
				}
			};
			
			worker.execute();
		}
		return null;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

	}
}