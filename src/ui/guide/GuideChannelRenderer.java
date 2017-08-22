package ui.guide;

import data.Channel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class GuideChannelRenderer extends DefaultTableCellRenderer {
    private Map<Channel, GuideChannelTableCell> _guideChannelCells = new HashMap<>();
    private final static JPanel _dummyJPanel = new JPanel();

    public GuideChannelRenderer() {
        setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {
        Channel channel = (Channel) value;
        if (channel == null) return _dummyJPanel;

        if (!_guideChannelCells.containsKey(channel))
            _guideChannelCells.put(channel, new GuideChannelTableCell(channel));

        GuideChannelTableCell cell = _guideChannelCells.get(channel);
        cell.update(isSelected, hasFocus, table, row, column);
        return cell;
    }
}