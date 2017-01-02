package ui.upcoming;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import data.Upcoming;
import data.UpcomingList;

public class UpcomingListRenderer implements TableCellRenderer, MouseListener, MouseMotionListener {
	private static final int _labelHeight = 75;
	private static final int _cellHeight = 75;
	private static final int _strutHeight = 30;
	private static final int _insetWidth = 40;
	
	private static Map<UpcomingList, Boolean> _upcomingHiddenMap = new HashMap<UpcomingList, Boolean>();
	private static Map<Upcoming, JPanel> _upcomingPanelMap = new HashMap<Upcoming, JPanel>();
	private static Upcoming _selectedUpcoming = null;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, 
			boolean isSelected, boolean hasFocus, int row, int column) {

		JPanel panel = new JPanel();
		UpcomingList ulist = (UpcomingList) value;
		
		// Lookup if this Cell is Minimized
		if (!_upcomingHiddenMap.containsKey(ulist))
			_upcomingHiddenMap.put(ulist, false);
		Boolean minimized = _upcomingHiddenMap.get(ulist);
		
		// Resize Row
		int calcHeight = _labelHeight + _strutHeight + 1 + (minimized ? 0 : ulist.get_upcoming().size() * _cellHeight);
		if (table.getRowHeight(row) != calcHeight) {
			table.setRowHeight(row, calcHeight);
			return panel;
		}
		
		// Date & Upcoming Counts
		JLabel dateLabel = new JLabel(ulist.get_date().format(DateTimeFormatter.ofPattern("EEEE, MMMM d, uuuu")));
		dateLabel.setFont(new Font("Arial", Font.BOLD, 36));
		JLabel counts = new JLabel("Active: " + ulist.get_active() + "; Inactive: " + ulist.get_inactive()
						+ "; Conflicts: " + ulist.get_conflicted() + "; Errored: " + ulist.get_errored());
		counts.setFont(new Font("Arial", Font.BOLD, 18));
		
		// Label Container
		JPanel label = new JPanel();
		label.setLayout(new BoxLayout(label, BoxLayout.Y_AXIS));
		label.add(dateLabel);
		label.add(counts);
		label.setPreferredSize(new Dimension(0, _labelHeight));
		
		// Various Layout Constraints
		GridBagConstraints cons = new GridBagConstraints(), cons2 = new GridBagConstraints(), cons3 = new GridBagConstraints();
		cons.fill = GridBagConstraints.HORIZONTAL; cons.weightx = 1; cons.gridx = 1; cons.gridy = 0;
		cons2.fill = GridBagConstraints.HORIZONTAL; cons2.weightx = 1; cons2.gridx = 0; cons2.gridwidth = 2; cons2.insets = new Insets(0, _insetWidth, 0, _insetWidth);
		cons3.fill = GridBagConstraints.HORIZONTAL; cons3.weightx = 0.02; cons3.gridx = 0; cons3.gridy = 0;
		
		// Final Cell Composition
		panel.setLayout(new GridBagLayout());
		panel.add(label, cons);
		
		if (!minimized) {
			boolean firstInList = true;
			for (Upcoming u : ulist.get_upcoming()) {
				JPanel p = getUpcomingPanel(u, false, firstInList); firstInList = false;
				p.setPreferredSize(new Dimension(0, _cellHeight));
				panel.add(p, cons2);
			}
		}
		
		panel.add(Box.createVerticalStrut(_strutHeight), cons);
		return (Component) panel;
	}
	
	private JPanel getUpcomingPanel(Upcoming u, boolean isSelected, boolean firstInList) {
		if (!_upcomingPanelMap.containsKey(u))
			_upcomingPanelMap.put(u, new JPanel());
		JPanel panel = _upcomingPanelMap.get(u);
		
		JLabel label = new JLabel();
		JTextArea title = new JTextArea(u.get_starttime()
				.withZoneSameInstant(ZoneId.systemDefault()).toLocalTime()
				.format(DateTimeFormatter.ofPattern("hh:mm a")) + " - " + u.get_title());
		title.setFont(new Font("Arial", Font.BOLD, 18));
		title.setEditable(false);
		title.setLineWrap(true);
		title.setWrapStyleWord(true);
		title.setBorder(label.getBorder());
		title.setBackground(label.getBackground());
		title.setForeground(label.getForeground());
		title.setOpaque(label.isOpaque());
		
		JTextArea subtitle = new JTextArea(u.get_subtitle() + "\n" + u.get_status().name());
		subtitle.setFont(new Font("Arial", Font.PLAIN, 14));
		subtitle.setEditable(false);
		subtitle.setLineWrap(true);
		subtitle.setWrapStyleWord(true);
		subtitle.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 0));
		subtitle.setBackground(label.getBackground());
		subtitle.setForeground(label.getForeground());
		subtitle.setOpaque(label.isOpaque());
		
		panel.removeAll();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(Box.createVerticalStrut(5));
		panel.add(title);
		panel.add(subtitle);
		
		if (u.get_rule().is_override()) {
			panel.setBackground(Color.YELLOW);
		} else if (u.get_status().getStatusInt() < 0) {
			panel.setBackground(Color.GREEN);
		} else {
			panel.setBackground((new JLabel()).getBackground());
		}
		
		if (u == _selectedUpcoming) {
			panel.setBorder(BorderFactory.createMatteBorder(firstInList ? 3 : 2, 3, 3, 3, Color.BLACK));
		} else {
			panel.setBorder(BorderFactory.createMatteBorder(firstInList ? 1 : 0, 1, 1, 1, Color.BLACK));
		}

		panel.addMouseListener(this);
		return panel;
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		// Highlight the Selected Upcoming Panel
		JTable table = (JTable) e.getSource();
		UpcomingList upcominglist = null;
		int row = table.rowAtPoint(e.getPoint());
		int column = table.columnAtPoint(e.getPoint());
		int selectionIndex = -1;
		
		if (row >= 0 && row < table.getRowCount() && column >= 0 && column < table.getColumnCount()) {			
			Rectangle r = table.getCellRect(row, column, true);
			upcominglist = (UpcomingList) table.getValueAt(row, column);
			selectionIndex = indexFromCoordinates(e.getX(), e.getY(), r);
		
			if (selectionIndex >= 0 && selectionIndex < upcominglist.get_upcoming().size()) {
				_selectedUpcoming = upcominglist.get_upcoming().get(selectionIndex);
				((DefaultTableModel) table.getModel()).fireTableCellUpdated(row, column);
			} else if (SwingUtilities.isLeftMouseButton(e)){
				_upcomingHiddenMap.put(upcominglist, !_upcomingHiddenMap.get(upcominglist));
				((DefaultTableModel) table.getModel()).fireTableCellUpdated(row, column);
			}
		}
		
		// Launch Context-Menu on Right Click					
		if (SwingUtilities.isRightMouseButton(e)) {
			if (e.isPopupTrigger()) {
				if (selectionIndex != -1 && upcominglist != null) {
					Upcoming upcoming = upcominglist.get_upcoming().get(selectionIndex);
					JPopupMenu menu = new UpcomingPopup(upcoming, selectionIndex, 0);
					menu.show(table, e.getX(), e.getY());
				}
			}
		}
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		// Highlight the Selected Upcoming Panel
		if (e.getSource() instanceof JTable) {
			JTable table = (JTable) e.getSource();
			int row = table.rowAtPoint(e.getPoint());
			int column = table.columnAtPoint(e.getPoint());
			
			if (row >= 0 && row < table.getRowCount() && column >= 0 && column < table.getColumnCount()) {			
				Rectangle r = table.getCellRect(row, column, true);
				UpcomingList upcominglist = (UpcomingList) table.getValueAt(row, column);
				int selectionIndex = indexFromCoordinates(e.getX(), e.getY(), r);
			
				if (selectionIndex >= 0 && selectionIndex < upcominglist.get_upcoming().size()) {
					_selectedUpcoming = upcominglist.get_upcoming().get(selectionIndex);
					((DefaultTableModel) table.getModel()).fireTableCellUpdated(row, column);
				}
			}
		}
	}
	
	@Override public void mouseEntered(MouseEvent e) {}
	@Override public void mouseExited(MouseEvent e) {}
	@Override public void mouseClicked(MouseEvent e) {}
	@Override public void mousePressed(MouseEvent e) {}
	@Override public void mouseDragged(MouseEvent e) {}
	
	private int indexFromCoordinates(int x, int y, Rectangle r) {
		if (y < _labelHeight + r.y || y > r.y + r.height - _strutHeight 
				|| x < _insetWidth + r.x || x > r.x + r.width - _insetWidth)
			return -1;
		
		return (y - r.y - _labelHeight) / _cellHeight;
	}
}