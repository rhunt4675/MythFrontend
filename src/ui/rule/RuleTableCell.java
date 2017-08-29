package ui.rule;

import data.Rule;

import javax.swing.*;
import java.awt.*;

public class RuleTableCell extends JPanel {
    // Rule Data
    private Rule _r;

    // Layout Containers
    private JLabel _chanInfo = new JLabel();
    private JLabel _iconLabel = new JLabel();

    public RuleTableCell(Rule r) {
        _r = r;

        JLabel label = new JLabel();
        JTextArea title = new JTextArea(_r.get_title());
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setEditable(false);
        title.setLineWrap(true);
        title.setWrapStyleWord(true);
        title.setBorder(label.getBorder());
        title.setBackground(label.getBackground());
        title.setForeground(label.getForeground());
        title.setOpaque(label.isOpaque());

        JTextArea subtitle = new JTextArea("Start Offset: " + _r.get_startoffset()
            + "\nEndOffset: " + _r.get_endoffset() + "\nMax Episodes: " + _r.get_maxepisodes()
            + "\nAutoExpire: " + _r.get_autoexpire() + "\nDupMethod: " + _r.get_dupmethod().name());
        subtitle.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitle.setEditable(false);
        subtitle.setLineWrap(true);
        subtitle.setWrapStyleWord(true);
        subtitle.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 0));
        subtitle.setBackground(label.getBackground());
        subtitle.setForeground(label.getForeground());
        subtitle.setOpaque(label.isOpaque());

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(Box.createVerticalStrut(5));
        add(title);
        add(subtitle);
    }

    public void update(boolean isSelected, boolean hasFocus, JTable table, int row, int column) {
        // Check if Active
        if (!_r.is_inactive()) setBackground(Color.GREEN);
        else setBackground(Color.WHITE);
    }
}
