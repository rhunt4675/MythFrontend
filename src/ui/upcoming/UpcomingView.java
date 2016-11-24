package ui.upcoming;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import data.UpcomingList;
import ui.ContentView;
import ui.MainFrame;

public class UpcomingView extends ContentView implements ListSelectionListener, MouseListener, KeyListener {
	private static final long serialVersionUID = -7264563780524135180L;
	private JTable _upcomingListByDay = new JTable();
	
	public UpcomingView() {
		setLayout(new BorderLayout());
		add(new JScrollPane(_upcomingListByDay), BorderLayout.CENTER);

		_upcomingListByDay.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		_upcomingListByDay.getSelectionModel().addListSelectionListener(this);
		_upcomingListByDay.addMouseListener(this);
		_upcomingListByDay.addKeyListener(this);
		_upcomingListByDay.setTableHeader(null);
		_upcomingListByDay.setDefaultRenderer(UpcomingList.class, new UpcomingListEditorRenderer());
		_upcomingListByDay.setDefaultEditor(UpcomingList.class, new UpcomingListEditorRenderer());
	}
	
	// Download a List of Upcoming Recordings
	@Override
	public void init() {
		SwingWorker<DefaultTableModel, Void> worker = new SwingWorker<DefaultTableModel, Void>() {

			@Override
			protected DefaultTableModel doInBackground() {
				DefaultTableModel model;
				
				try {
					List<UpcomingList> upcominglist = UpcomingList.get_upcoming_by_day();
					model = new DefaultTableModel() {
						private static final long serialVersionUID = -2950671252908293184L;
						@Override public Class<?> getColumnClass(int columnIndex) {
							return UpcomingList.class;
						}
					};
					
					model.addColumn(null, upcominglist.toArray(new UpcomingList[0]));
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
			    	_upcomingListByDay.setModel(model);
			    } catch (InterruptedException ignore) {
			    } catch (java.util.concurrent.ExecutionException e) {
			            e.printStackTrace();
			    }
			}
		};
		
		worker.execute();
	}

	@Override public void valueChanged(ListSelectionEvent e) {}
	@Override public void mouseClicked(MouseEvent e) {}
	@Override public void mousePressed(MouseEvent e) {}
	@Override public void mouseReleased(MouseEvent e) {}
	@Override public void mouseEntered(MouseEvent e) {}
	@Override public void mouseExited(MouseEvent e) {}
	@Override public void keyTyped(KeyEvent e) {}
	@Override public void keyPressed(KeyEvent e) {}

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
