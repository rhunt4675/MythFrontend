package ui.upcoming;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.List;

import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import data.Upcoming;
import ui.ContentView;
import ui.MainFrame;

public class UpcomingView extends ContentView implements ListSelectionListener, MouseListener, KeyListener {
	private static final long serialVersionUID = -7264563780524135180L;
	
	private JTable _upcomingTable = new JTable();

	public UpcomingView() {
		setLayout(new BorderLayout());
		add(new JScrollPane(_upcomingTable), BorderLayout.CENTER);
		
		_upcomingTable.setDefaultRenderer(Upcoming.class, new UpcomingRenderer());
		_upcomingTable.getSelectionModel().addListSelectionListener(this);
		_upcomingTable.addMouseListener(this);
		_upcomingTable.addKeyListener(this);
		_upcomingTable.setTableHeader(null);
		_upcomingTable.setRowHeight(75);
	}
	
	// Download a List of Upcoming Recordings
	@Override
	public void init() {
		SwingWorker<DefaultTableModel, Void> worker = new SwingWorker<DefaultTableModel, Void>() {

			@Override
			protected DefaultTableModel doInBackground() {
				DefaultTableModel model;
				
				try {
					List<Upcoming> upcoming = Upcoming.get_upcoming();
					
					model = new DefaultTableModel();
					model.addColumn(null, upcoming.toArray(new Upcoming[0]));					
				} catch (IOException e) {
					e.printStackTrace();
					model = null;
				}
				
				return model;
			}
			
			@Override
			protected void done() {
			    try {
			    	DefaultTableModel model = get();
			    	_upcomingTable.setModel(model);
			    	_upcomingTable.setDefaultEditor(Object.class, null);
			    	_upcomingTable.getColumnModel().getColumn(0).setCellRenderer(new UpcomingRenderer());
			    } catch (InterruptedException ignore) {
			    } catch (java.util.concurrent.ExecutionException e) {
			            e.printStackTrace();
			    }
			}
		};
		
		worker.execute();
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (SwingUtilities.isRightMouseButton(e)) {
			JTable table = (JTable) e.getSource();
			int row = table.rowAtPoint(e.getPoint());
			int col = table.columnAtPoint(e.getPoint());
			table.getSelectionModel().setSelectionInterval(row, row);
			table.setColumnSelectionInterval(col, col);
			
			if (e.isPopupTrigger()) {
				JPopupMenu menu = new UpcomingPopup();
				menu.show(table, e.getX(), e.getY());
			}
		} else if (e.getClickCount() == 2) {
            // Double Click Event
        }
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_F5) {
			// Find MainFrame by traversing tree
			Component source = (Component) e.getSource();
			while (source.getParent() != null)
				source = source.getParent();
			
			// Pass message to MainFrame
			((MainFrame) source).keyPressed(e);
		}
	}
}
