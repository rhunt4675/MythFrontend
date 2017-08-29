package ui.upcoming;

import data.UpcomingList;
import ui.ContentView;
import ui.MainFrame;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UpcomingView extends ContentView implements ListSelectionListener, KeyListener {
	private static final Logger LOGGER = Logger.getLogger(UpcomingView.class.getName());
	private JTable _upcomingListByDay = new JTable();
	
	public UpcomingView() {
		setLayout(new BorderLayout());
		add(new JScrollPane(_upcomingListByDay), BorderLayout.CENTER);
		
		UpcomingListRenderer renderer = new UpcomingListRenderer();

		_upcomingListByDay.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		_upcomingListByDay.getSelectionModel().addListSelectionListener(this);
		_upcomingListByDay.addMouseListener(renderer);
		_upcomingListByDay.addMouseMotionListener(renderer);
		_upcomingListByDay.addKeyListener(this);
		_upcomingListByDay.setTableHeader(null);
		_upcomingListByDay.setAutoscrolls(false);
		_upcomingListByDay.setRowHeight(1000);
		_upcomingListByDay.setDefaultRenderer(UpcomingList.class, renderer);
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
						@Override public Class<?> getColumnClass(int columnIndex) {
							return UpcomingList.class;
						}
					};
					
					model.addColumn(null, upcominglist.toArray(new UpcomingList[0]));
				} catch (IOException e) {
					LOGGER.log(Level.SEVERE, e.toString(), e);
					model = null;
				}
				
				return model;
			}
			
			@Override
			protected void done() {
			    try {
			    	DefaultTableModel model = get();
			    	if (model != null) _upcomingListByDay.setModel(model);
			    } catch (InterruptedException ignore) {
			    } catch (java.util.concurrent.ExecutionException e) {
		            LOGGER.log(Level.SEVERE, e.toString(), e);
			    }
			}
		};
		
		worker.execute();
	}

	@Override public void valueChanged(ListSelectionEvent e) {}
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
			((MainFrame) source).refresh();
		}
	}
}
