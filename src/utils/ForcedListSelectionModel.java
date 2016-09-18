package utils;

import javax.swing.DefaultListSelectionModel;
import javax.swing.ListSelectionModel;

public class ForcedListSelectionModel extends DefaultListSelectionModel {
	private static final long serialVersionUID = -2241810286759381043L;
	
    public ForcedListSelectionModel () {
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    @Override
    public void clearSelection() {

    }

    @Override
    public void removeSelectionInterval(int index0, int index1) {
    	
    }
}
