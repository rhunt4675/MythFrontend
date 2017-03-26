package ui.recording;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import data.Recording;

public class RecordingRenderer extends DefaultTableCellRenderer implements ActionListener, MouseMotionListener, MouseListener {
	private static final long serialVersionUID = 7124170992120515156L;
	public static final int _unselectedCellHeight = 100, _channelIconHeight = 85;
	
	private Map<Recording, RecordingTableCell> _panelMap = new HashMap<>();
	private int hoverRow = 0, hoverCol = 0;

	public RecordingRenderer() {
		setOpaque(true);
	}
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		
		// Fix Row Height
		if (table.getRowHeight(row) != _unselectedCellHeight)
			table.setRowHeight(row, _unselectedCellHeight);
		
		// Temp Variables
		Recording recording = (Recording) value;
		boolean isHovered = row == hoverRow && column == hoverCol;
		
		// Create a New Panel
		if (!_panelMap.containsKey(recording))
			_panelMap.put(recording, new RecordingTableCell(recording));
		
		// Update
		RecordingTableCell rtc = _panelMap.get(recording);
		rtc.update(isSelected, isHovered, hasFocus, table, row, column);
		return rtc;
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