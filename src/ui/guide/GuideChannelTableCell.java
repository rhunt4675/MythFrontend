package ui.guide;

import data.Channel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GuideChannelTableCell extends JPanel {
    private static final Logger LOGGER = Logger.getLogger(GuideChannelTableCell.class.getName());
    private static final Dimension _iconDimension = new Dimension(0, 80);

    // Channel Data
    private Channel _c;
    private ImageIcon _channelIcon;

    // Layout Containers
    private JLabel _chanInfo = new JLabel();
    private JLabel _iconLabel = new JLabel();

    public GuideChannelTableCell(Channel c) {
        _c = c;

        // Channel Info Box
        _chanInfo.setHorizontalAlignment(JLabel.CENTER);

        // Setup the Cell
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        add(_iconLabel, BorderLayout.CENTER);
        add(_chanInfo, BorderLayout.SOUTH);
    }

    public void update(boolean isSelected, boolean hasFocus, JTable table, int row, int column) {
        // Channel Icon
        _iconLabel.setHorizontalAlignment(JLabel.CENTER);

        // Channel Info
        _chanInfo.setText(_c.get_channame() + " - " + _c.get_channum());

        // Artwork Download
        if (_channelIcon == null) getChannelArtwork(_c, _iconDimension, table, row, column);
    }

    private void getChannelArtwork(Channel channel, Dimension dimension, JTable table, int row, int column) {
        new SwingWorker<ImageIcon, Void>() {
            @Override
            protected ImageIcon doInBackground() throws Exception {
                return channel.get_artwork(dimension);
            }

            @Override
            protected void done() {
                try {
                    _channelIcon = get();
                    _iconLabel.setIcon(_channelIcon);
                } catch (InterruptedException | ExecutionException e) {
                    LOGGER.log(Level.SEVERE, e.toString(), e);
                }

                // Force Row Update
                if (row < table.getRowCount() && column < table.getColumnCount())
                    ((DefaultTableModel) table.getModel()).fireTableCellUpdated(
                            table.convertRowIndexToModel(row), table.convertColumnIndexToModel(column));
            }
        }.execute();
    }
}