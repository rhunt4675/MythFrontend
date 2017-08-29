package ui.recording;

import data.Channel;
import data.Recording;
import data.Recording.Artwork;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RecordingTableCell extends JPanel {
	private static final Logger LOGGER = Logger.getLogger(RecordingTableCell.class.getName());
	private static final Dimension _previewDimension = new Dimension((RecordingRenderer._unselectedCellHeight * 16) / 9, RecordingRenderer._unselectedCellHeight);
	private static final Dimension _bannerDimension = new Dimension(0, RecordingRenderer._unselectedCellHeight);
	private static final Dimension _channelDimension = new Dimension(0, RecordingRenderer._channelIconHeight);
	private static final Set<String> _requests = new HashSet<>();
	
	// Recording Data
	private Recording _r;
	private ImageIcon _channelIcon, _previewIcon, _bannerIcon;
	
	// Layout Containers
	private JTextArea _subtitle, _description;
	private JLabel _channel, _channame;
	private JPanel _preview, _banner;
	private Box _textcontent;
	
	// Default Coloring Reference Object
	private JLabel label = new JLabel();
	
	public RecordingTableCell(Recording r) {
		_r = r;
		
		// Subtitle
		_subtitle = new JTextArea();
		_subtitle.setEditable(false);
		_subtitle.setLineWrap(true);
		_subtitle.setWrapStyleWord(true);
		_subtitle.setBorder(label.getBorder());
		_subtitle.setBackground(label.getBackground());
		_subtitle.setForeground(label.getForeground());
		_subtitle.setOpaque(label.isOpaque());

		// Description
		_description = new JTextArea();
		_description.setFont(new Font("Arial", Font.PLAIN, 12));
		_description.setEditable(false);
		_description.setLineWrap(true);
		_description.setWrapStyleWord(true);
		_description.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 0));
		_description.setBackground(label.getBackground());
		_description.setForeground(label.getForeground());
		_description.setOpaque(label.isOpaque());
		
		// Channel Identification
		_channel = new JLabel();
		_channel.setAlignmentX(Component.CENTER_ALIGNMENT);
		_channame = new JLabel();
		_channame.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		// Text Content Container
		_textcontent = Box.createVerticalBox();
		_textcontent.add(Box.createVerticalStrut(10));
		_textcontent.add(_subtitle);
		_textcontent.add(_description);
		_textcontent.add(Box.createGlue());
		
		// Surrounding Box
		Box programContent = Box.createVerticalBox();
		programContent.setOpaque(true);
		programContent.setBackground(Color.WHITE);
		programContent.add(_channel);
		programContent.add(_channame);
		
		// Artwork Containers
		_preview = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		_preview.add(Box.createRigidArea(_previewDimension));
		_banner = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		
		// Master Container
		JPanel content = new JPanel(new BorderLayout());
		content.add(_textcontent, BorderLayout.CENTER);
		content.add(programContent, BorderLayout.EAST);
		
		setLayout(new BorderLayout());
		add(_preview, BorderLayout.WEST);
		add(content, BorderLayout.CENTER);
		add(_banner, BorderLayout.EAST);
	}
	
	public void update(boolean isSelected, boolean isHovered, boolean hasFocus, JTable table, int row, int column) {
		// Subtitle
		_subtitle.setText(((_r.get_season() != 0 && _r.get_episode() != 0) 
				? (_r.get_season() + "x" + String.format("%02d - ",_r.get_episode())) : " ")
				+ (_r.get_subtitle().isEmpty() ? _r.get_title() : _r.get_subtitle())
				+ (_r.get_trakt_watched() ? " *** " : ""));
		_subtitle.setFont(new Font("Arial", _r.is_watched() ? Font.PLAIN : Font.BOLD, 18));
		
		// Description
		_description.setText(_r.get_description() + "\n\n" 
				+ "\t(" + _r.get_starttime().withZoneSameInstant(ZoneId.systemDefault()).
				format(DateTimeFormatter.ofPattern("h:mm a EEEE, MMM d, uuuu")) +
				"\t" + _r.get_filesize() + "B)");
		
		// Text Content
		if (isHovered) _textcontent.setBorder(BorderFactory.createMatteBorder(6, 6, 6, 6, Color.BLUE));
		else _textcontent.setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, (isSelected ? Color.BLACK : label.getBackground())));
		_channame.setText("(" + _r.get_channel().get_channame() + ")");
		
		// Artwork Download
		if (_channelIcon == null) getChannelArtwork(_r.get_channel(), _channelDimension, table, row, column);
		if (_bannerIcon == null) getRecordingArtwork(Artwork.BANNER, _bannerDimension, table, row, column);
		if (_previewIcon == null) getRecordingArtwork(Artwork.PREVIEW, _previewDimension, table, row, column);
	}
	
	private void getRecordingArtwork(Artwork type, Dimension dimension, JTable table, int row, int column) {
		// Deny Concurrent Requests
		final String requestId = _r.get_recordedid() + type.toString() + dimension.toString();
		if (_requests.contains(requestId)) return;
		_requests.add(requestId);

		new SwingWorker<ImageIcon, Void>() {
			@Override
			protected ImageIcon doInBackground() throws Exception {
				return _r.get_artwork(type, dimension);
			}
			
			@Override
			protected void done() {
				try {
					ImageIcon recordingArtwork = get();
					if (type == Artwork.BANNER) {
						_banner.removeAll();
						_banner.add(new JLabel(recordingArtwork), BorderLayout.CENTER);
						_bannerIcon = recordingArtwork;
					} else if (type == Artwork.PREVIEW) {
						_preview.removeAll();
						_preview.add(new JLabel(recordingArtwork));
						_previewIcon = recordingArtwork;
					}
				} catch (InterruptedException | ExecutionException e) {
					LOGGER.log(Level.SEVERE, e.toString(), e);
				}

				// Force Row Update
				if (row < table.getRowCount() && column < table.getColumnCount())
					((DefaultTableModel) table.getModel()).fireTableCellUpdated(
							table.convertRowIndexToModel(row), table.convertColumnIndexToModel(column));
				_requests.remove(requestId);
			}
		}.execute();
	}
	
	
	private void getChannelArtwork(Channel channel, Dimension dimension, JTable table, int row, int column) {
		// Deny Concurrent Requests
		final String requestId = _r.get_recordedid() + channel.toString() + dimension.toString();
		if (_requests.contains(requestId)) return;
		_requests.add(requestId);

		new SwingWorker<ImageIcon, Void>() {
			@Override
			protected ImageIcon doInBackground() throws Exception {
				return channel.get_artwork(dimension);
			}
			
			@Override
			protected void done() {
				try {
					_channelIcon = get();
					_channel.setIcon(_channelIcon);
				} catch (InterruptedException | ExecutionException e) {
					LOGGER.log(Level.SEVERE, e.toString(), e);
				}
				
				// Force Row Update
				if (row < table.getRowCount() && column < table.getColumnCount())
					((DefaultTableModel) table.getModel()).fireTableCellUpdated(
							table.convertRowIndexToModel(row), table.convertColumnIndexToModel(column));
				_requests.remove(requestId);
			}
		}.execute();
	}
}
