package ui.recording;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import data.Recording;
import data.Title;
import ui.ContentView;
import ui.MainFrame;

public class RecordingView extends ContentView implements ListSelectionListener, MouseListener, KeyListener {
	private static final long serialVersionUID = 7537158574729297160L;
	private JList<Title> _titleList = new JList<Title>();
	private JTable _recordingTable = new JTable();
	private JLabel _titleArtwork = new JLabel();
	
	private Map<Title, TableModel> _models = new HashMap<Title, TableModel>();
	private Map<Title, Integer> _modelSelection = new HashMap<Title, Integer>();
	private Dimension _titleArtworkDimension = new Dimension(250, 0);

	public RecordingView() {
		JPanel sidepane = new JPanel();
		sidepane.setLayout(new BorderLayout());
		sidepane.add(new JScrollPane(_titleList), BorderLayout.CENTER);
		sidepane.add(_titleArtwork, BorderLayout.SOUTH);
		
		setLayout(new BorderLayout());
		add(sidepane, BorderLayout.WEST);
		add(new JScrollPane(_recordingTable), BorderLayout.CENTER);
		
		_titleList.addKeyListener(this);
		_titleList.addListSelectionListener(this);
		_titleList.setSelectionBackground(Color.DARK_GRAY);
		
		_recordingTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		_recordingTable.getSelectionModel().addListSelectionListener(this);
		_recordingTable.addMouseListener(this);
		_recordingTable.addKeyListener(this);
		_recordingTable.setTableHeader(null);
		_recordingTable.setRowHeight(100);
	}
	
	// Download a List of Titles
	@Override
	public void init() {
		// Selection index before refresh
		int selected = Math.max(_titleList.getSelectedIndex(), 0);
		
		SwingWorker<List<Title>, Void> worker = new SwingWorker<List<Title>, Void>() {
			@Override
			protected List<Title> doInBackground() {
				List<Title> titles;
				try {
					titles = Title.get_titles();
				} catch (IOException e) {
					e.printStackTrace();
					titles = new ArrayList<Title>();
				}
				
				return titles;
			}
			
			@Override
			protected void done() {
				List<Title> titles = null;
			    try {
			    	titles = get();
			    } catch (Exception e) {
			    	e.printStackTrace();
			    }
			    
			    if (titles != null && !titles.isEmpty()) {
			    	_titleList.setListData(titles.toArray(new Title[0]));
			    	_titleList.setSelectedIndex(Math.min(selected, _titleList.getModel().getSize() - 1));
			    }
			}
		};
		
		worker.execute();
	}
	
	private void onTitleListSelectionChanged() {
		// Download a List of Recordings for a Selected Title
		Title selected = _titleList.getSelectedValue();
		
		// Clear the Recording List if Predicting a long wait time
		if (selected == null || !_models.containsKey(selected))
			_recordingTable.setModel(new DefaultTableModel());
		
		// Background Episode Download Task
		SwingWorker<DefaultTableModel, Void> worker = new SwingWorker<DefaultTableModel, Void>() {

			@Override
			protected DefaultTableModel doInBackground() {
				DefaultTableModel model;
				
				if (selected == null) {
					model = null;
				} else if (!_models.containsKey(selected)) {
					try {
						List<Recording> episodes = Recording.get_recordings(selected.get_title());
						
						model = new DefaultTableModel();
						model.addColumn(null, episodes.toArray(new Recording[0]));
						
						_models.put(selected, model);							
					} catch (IOException e) {
						e.printStackTrace();
						model = null;
					}
				} else {
					model = (DefaultTableModel) _models.get(selected);
				}
				
				if (!_modelSelection.containsKey(selected))
					_modelSelection.put(selected, 0);
				
				return model;
			}
			
			@Override
			protected void done() {
			    try {
			    	DefaultTableModel model = get();
			    	
			    	if (model != null) {
				    	_recordingTable.setModel(model);
				    	_recordingTable.setDefaultEditor(Object.class, null);
				    	_recordingTable.getColumnModel().getColumn(0).setCellRenderer(new RecordingRenderer());
				    	_recordingTable.getSelectionModel().setSelectionInterval(_modelSelection.get(selected), _modelSelection.get(selected));
			    	}
			    } catch (InterruptedException ignore) {
			    } catch (ExecutionException e) {
			    	e.printStackTrace();
			    }
			}
		};
		
		// Background Artwork Download Task
		SwingWorker<ImageIcon, Void> imagedownloader = new SwingWorker<ImageIcon, Void>() {
			@Override
			protected ImageIcon doInBackground() throws Exception {
				return selected == null ? null : selected.get_title_artwork(_titleArtworkDimension);
			}
			
			protected void done() {
				try {
					ImageIcon icon = get();
					_titleArtwork.setIcon(icon);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		
		worker.execute();
		imagedownloader.execute();
	}
	
	private void onRecordingTableSelectionChanged(ListSelectionEvent e) {
		// Update Selection Index
		if (!e.getValueIsAdjusting() && _recordingTable.getSelectedRow() != -1) {
			Title selected = _titleList.getSelectedValue();
			int row = _recordingTable.getSelectedRow();
			_modelSelection.put(selected, row);
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (e.getSource() == _titleList)
			onTitleListSelectionChanged();
		else if (e.getSource() == _recordingTable.getSelectionModel())
			onRecordingTableSelectionChanged(e);
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
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (SwingUtilities.isRightMouseButton(e)) {
			
			// Launch Context-Menu on Right Click
			JTable table = (JTable) e.getSource();
			int row = table.rowAtPoint(e.getPoint());
			int col = table.columnAtPoint(e.getPoint());
			table.getSelectionModel().setSelectionInterval(row, row);
			table.setColumnSelectionInterval(col, col);
			
			if (e.isPopupTrigger()) {
				JPopupMenu menu = new RecordingPopup();
				menu.show(table, e.getX(), e.getY());
			}
		} else if (e.getClickCount() == 2) {
			
			// Play on Double Click
			JTable table = (JTable) e.getSource();
			int row = table.rowAtPoint(e.getPoint());
			
			Recording r = (Recording) table.getValueAt(row, 0);
			r.play();
		}
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
