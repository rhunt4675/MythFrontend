package ui.guide;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import data.Channel;

public class GuideChannelRenderer extends DefaultTableCellRenderer  {
	private static final long serialVersionUID = 3858092984404108408L;
	private static final Dimension _iconDimension = new Dimension(0, 80);

	public GuideChannelRenderer() {
		setOpaque(true);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		Channel channel = (Channel) value;
		
		JPanel cell = new JPanel();
		cell.setLayout(new BorderLayout());
		cell.setBackground(Color.WHITE);
		
		if (channel != null) {			
			ImageIcon icon = downloadChannelIconAsync(table, row, column, channel, _iconDimension);
			
			if (icon != null) {
				JLabel iconLabel = new JLabel(icon);
				cell.add(iconLabel, BorderLayout.CENTER);
			}
			
			JLabel chaninfo = new JLabel(channel.get_channame() + " - " + channel.get_channum());
			chaninfo.setHorizontalAlignment(JLabel.CENTER);
			cell.add(chaninfo, BorderLayout.SOUTH);
		}
		return cell;
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
}