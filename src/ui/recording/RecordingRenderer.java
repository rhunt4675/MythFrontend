package ui.recording;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.time.ZoneId;
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

public class RecordingRenderer extends DefaultTableCellRenderer implements ActionListener, MouseMotionListener, MouseListener {

	private static final long serialVersionUID = 7124170992120515156L;
	private static final int /*_selectedCellHeight = 400,*/ _unselectedCellHeight = 100, _channelIconHeight = 85;
	
	private static final Dimension _previewDimension = new Dimension((_unselectedCellHeight * 16) / 9, _unselectedCellHeight);
	private static final Dimension _bannerDimension = new Dimension(0, _unselectedCellHeight);
	private static final Dimension _channelDimension = new Dimension(0, _channelIconHeight);
	//private static final Dimension _fanartDimension = new Dimension(0, _selectedCellHeight);
	
	private int hoverRow = 0, hoverCol = 0;

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
		subtitle.setFont(new Font("Arial", r.is_watched() ? Font.PLAIN : Font.BOLD, 18));
		subtitle.setEditable(false);
		subtitle.setLineWrap(true);
		subtitle.setWrapStyleWord(true);
		subtitle.setBorder(label.getBorder());
		subtitle.setBackground(label.getBackground());
		subtitle.setForeground(label.getForeground());
		subtitle.setOpaque(label.isOpaque());

		JTextArea description = new JTextArea(r.get_description() + "\n\n" 
				+ "\t(" + r.get_starttime().withZoneSameInstant(ZoneId.systemDefault()).
				format(DateTimeFormatter.ofPattern("h:mm a EEEE, MMM d, uuuu")) +
				"\t" + r.get_filesize() + "B)");
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
		JLabel channame = new JLabel(r.get_channel().get_channame());
		channame.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		Box textcontent = Box.createVerticalBox();
		textcontent.add(Box.createVerticalStrut(10));
		textcontent.add(subtitle);
		textcontent.add(description);
		textcontent.add(Box.createGlue());
		
		if (row == hoverRow && column == hoverCol) textcontent.setBorder(BorderFactory.createMatteBorder(6, 6, 6, 6, Color.BLUE));
		else textcontent.setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, (isSelected ? Color.BLACK : label.getBackground())));
		
		Box programcontent = Box.createVerticalBox();
		programcontent.setOpaque(true);
		programcontent.setBackground(Color.WHITE);
		programcontent.add(channel);
		programcontent.add(channame);
					
		Box content = Box.createHorizontalBox();
		if (preview != null) content.add(new JLabel(preview)); else content.add(Box.createRigidArea(_previewDimension));
		content.add(textcontent);
		content.add(programcontent);
		content.add(new JLabel(banner));
		
		panel.add(content, BorderLayout.CENTER);
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
					return null;
				}
				
				@Override
				protected void done() {
					try {
						get();
					} catch (InterruptedException | ExecutionException e) {
						e.printStackTrace();
					}
					
					if (row < table.getRowCount() && column < table.getColumnCount())
						((DefaultTableModel) table.getModel()).fireTableCellUpdated(
								table.convertRowIndexToModel(row), table.convertColumnIndexToModel(column));
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
					return null;
				}
				
				@Override
				protected void done() {
					try {
						get();
					} catch (InterruptedException | ExecutionException e) {
						e.printStackTrace();
					}
					
					((DefaultTableModel) table.getModel()).fireTableCellUpdated(
							table.convertRowIndexToModel(row), table.convertColumnIndexToModel(column));
				}
			};
			
			worker.execute();
		}
		return null;
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// Highlight the Selected Recording Panel
		if (e.getSource() instanceof JTable) {
			JTable table = (JTable) e.getSource();
			int row = table.rowAtPoint(e.getPoint());
			int column = table.columnAtPoint(e.getPoint());
			
			if (row >= 0 && column >= 0 && (row != hoverRow || column != hoverCol)) {
				int tmpRow = hoverRow, tmpCol = hoverCol;
				hoverRow = row; hoverCol = column;
				
				if (tmpRow < table.getRowCount() && tmpCol < table.getColumnCount() && tmpRow >= 0 && tmpCol >= 0)
					((DefaultTableModel) table.getModel()).fireTableCellUpdated(
							table.convertRowIndexToModel(tmpRow), table.convertColumnIndexToModel(tmpCol));
				((DefaultTableModel) table.getModel()).fireTableCellUpdated(
						table.convertRowIndexToModel(hoverRow), table.convertColumnIndexToModel(hoverCol));
			}
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// Remove Hover Highlight when mouse leaves panel
		if (e.getSource() instanceof JTable) {
			JTable table = (JTable) e.getSource();
			
			int tmpRow = hoverRow, tmpCol = hoverCol;
			hoverRow = -1; hoverCol = -1;
			if (tmpRow < table.getRowCount() && tmpCol < table.getColumnCount() && tmpRow >= 0 && tmpCol >= 0)
				((DefaultTableModel) table.getModel()).fireTableCellUpdated(
						table.convertRowIndexToModel(tmpRow), table.convertColumnIndexToModel(tmpCol));
		}
	}

	@Override public void mouseDragged(MouseEvent e) {}
	@Override public void actionPerformed(ActionEvent e) {}
	@Override public void mouseClicked(MouseEvent e) {}
	@Override public void mousePressed(MouseEvent e) {}
	@Override public void mouseReleased(MouseEvent e) {}
	@Override public void mouseEntered(MouseEvent e) {}
}