package ui.rule;

import data.Rule;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class RuleRenderer extends DefaultTableCellRenderer {
	private static Map<Rule, RuleTableCell> _ruleCells = new HashMap<>();
	private static final JPanel _dummyJPanel = new JPanel();

	public RuleRenderer() { setOpaque(true); }

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
												   boolean isSelected, boolean hasFocus, int row, int column) {
		Rule rule = (Rule) value;
		if (rule == null) return _dummyJPanel;

		if (!_ruleCells.containsKey(rule))
			_ruleCells.put(rule, new RuleTableCell(rule));

		RuleTableCell cell = _ruleCells.get(rule);
		cell.update(isSelected, hasFocus, table, row, column);
		return cell;
	}
}