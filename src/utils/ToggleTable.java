package utils;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;

public class ToggleTable extends JTable {
	private static final long serialVersionUID = 6427640582845770986L;

	public ToggleTable() {
		super();
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}
	
	@Override 
	public void changeSelection(int rowIndex, int columnIndex, boolean toggle, boolean extend) {
        super.changeSelection(rowIndex, columnIndex, true, false);
    }
}
