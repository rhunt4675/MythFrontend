package ui.recording;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.regex.PatternSyntaxException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import data.Recording;
import data.Title;
import ui.ContentView;
import ui.MainFrame;

public class RecordingView extends ContentView implements ListSelectionListener, 
			ActionListener, MouseListener, KeyListener, DocumentListener {
	private static final long serialVersionUID = 7537158574729297160L;
	private static final String[] _sortTypeArray = {"Date", "Original Airdate", "Season/Episode", "Filesize"};
	private static final String[] _sortDirectionArray = {"Ascending", "Descending"};
	
	private TableRowSorter<TableModel> _sorter = new TableRowSorter<TableModel>();
	private JList<Title> _titleList = new JList<Title>();
	private JTable _recordingTable = new JTable();
	private JLabel _titleArtwork = new JLabel();
	
	private Map<Title, TableModel> _models = new HashMap<Title, TableModel>();
	private Map<Title, Integer> _modelSelection = new HashMap<Title, Integer>();
	private static final Dimension _titleArtworkDimension = new Dimension(250, 0);
	private static final RecordingRenderer _renderer = new RecordingRenderer();
	
	private List<SortKey> _sortKeys = new ArrayList<SortKey>();
	private JLabel _searchLabel = new JLabel();
	private JLabel _sortLabel = new JLabel();
	private JTextField _searchTextField = new JTextField();
	private JComboBox<String> _sortTypeComboBox = new JComboBox<String>();
	private JComboBox<String> _sortDirectionComboBox = new JComboBox<String>();
	private JButton _playButton = new JButton();
	private JButton _deleteButton = new JButton();
	private JButton _refreshButton = new JButton();

	public RecordingView() {
		JPanel sidepane = new JPanel();
		sidepane.setLayout(new BorderLayout());
		sidepane.add(new JScrollPane(_titleList), BorderLayout.CENTER);
		sidepane.add(_titleArtwork, BorderLayout.SOUTH);
		
		JPanel recoperations = new JPanel();
		recoperations.add(_playButton);
		recoperations.add(_deleteButton);
		recoperations.add(_refreshButton);
		
		JPanel recfiltering = new JPanel();
		recfiltering.add(_searchLabel);
		recfiltering.add(_searchTextField);
		recfiltering.add(_sortLabel);
		recfiltering.add(_sortTypeComboBox);
		recfiltering.add(_sortDirectionComboBox);
		
		JPanel selectors = new JPanel();
		selectors.setLayout(new BorderLayout());
		selectors.add(recoperations, BorderLayout.WEST);
		selectors.add(recfiltering, BorderLayout.EAST);
		
		JPanel mainpane = new JPanel();
		mainpane.setLayout(new BorderLayout());
		mainpane.add(new JScrollPane(_recordingTable), BorderLayout.CENTER);
		mainpane.add(selectors, BorderLayout.NORTH);
		
		setLayout(new BorderLayout());
		add(sidepane, BorderLayout.WEST);
		add(mainpane, BorderLayout.CENTER);
		
		_playButton.addActionListener(this);
		_playButton.setFocusable(false);
		_playButton.setToolTipText("Play");
		_playButton.setIcon(new ImageIcon(getClass().getResource("/res/play.jpg")));
		_deleteButton.addActionListener(this);
		_deleteButton.setFocusable(false);
		_deleteButton.setToolTipText("Delete");
		_deleteButton.setIcon(new ImageIcon(getClass().getResource("/res/delete.jpg")));
		_refreshButton.addActionListener(this);
		_refreshButton.setFocusable(false);
		_refreshButton.setToolTipText("Refresh");
		_refreshButton.setIcon(new ImageIcon(getClass().getResource("/res/refresh.jpg")));
		_searchLabel.setText("Search: ");
		_searchTextField.setPreferredSize(new Dimension(100, 25));
		_searchTextField.getDocument().addDocumentListener(this);
		_sortLabel.setText("Sort: ");
		_sortTypeComboBox.addActionListener(this);
		_sortTypeComboBox.setModel(new DefaultComboBoxModel<String>(_sortTypeArray));
		_sortTypeComboBox.setPreferredSize(new Dimension(175, 25));
		_sortDirectionComboBox.addActionListener(this);
		_sortDirectionComboBox.setModel(new DefaultComboBoxModel<String>(_sortDirectionArray));
		_sortDirectionComboBox.setPreferredSize(new Dimension(175, 25));
		_sortTypeComboBox.setSelectedIndex(0);				// Initial Sorting == Descending Record Date
		_sortDirectionComboBox.setSelectedIndex(1); 		// Initial Sorting == Descending Record Date
		
		_titleList.addKeyListener(this);
		_titleList.addListSelectionListener(this);
		_titleList.setSelectionBackground(Color.DARK_GRAY);
				
		_recordingTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		_recordingTable.getSelectionModel().addListSelectionListener(this);
		_recordingTable.setDefaultEditor(Object.class, null);
		_recordingTable.setRowSorter(_sorter);
		_recordingTable.addMouseListener(this);
		_recordingTable.addMouseListener(_renderer);
		_recordingTable.addMouseMotionListener(_renderer);
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
						Recording[] recordings = new Recording[episodes.size()];
						ZonedDateTime[] recdates = new ZonedDateTime[episodes.size()];
						LocalDate[] airdates = new LocalDate[episodes.size()];
						Double[] seasonepisodes = new Double[episodes.size()];
						BigInteger[] filesizes = new BigInteger[episodes.size()];
						
						for (int i = 0; i < episodes.size(); i++) {
							recordings[i] = episodes.get(i);
							recdates[i] = episodes.get(i).get_starttime().withZoneSameInstant(ZoneId.systemDefault());
							airdates[i] = episodes.get(i).get_airdate();
							seasonepisodes[i] = episodes.get(i).get_season() + 0.001 * episodes.get(i).get_episode();
							filesizes[i] = new BigInteger(episodes.get(i).get_filesize());
						}
						
						model = new DefaultTableModel() {
							private static final long serialVersionUID = -1225467351008175463L;

							@Override public Class<?> getColumnClass(int columnIndex) {
								switch (columnIndex) {
								case 0: return Recording.class;
								case 1: return ZonedDateTime.class;
								case 2: return LocalDate.class;
								case 3: return Double.class;
								case 4: return BigInteger.class;
								default: return null;
								}
							}
						};
						
						model.addColumn(null, recordings);
						model.addColumn(null, recdates);
						model.addColumn(null, airdates);
						model.addColumn(null, seasonepisodes);
						model.addColumn(null, filesizes);
						
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
			    		_sorter.setModel(model);
			    		_sorter.setSortKeys(_sortKeys);
			    		
			    		// Hide extraneous "sort" columns
			    		while (_recordingTable.getColumnCount() > 1)
			    	    	_recordingTable.removeColumn(_recordingTable.getColumnModel().getColumn(1));
			    		
				    	_recordingTable.setDefaultRenderer(Recording.class, _renderer);
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
			int row = _recordingTable.convertRowIndexToModel(_recordingTable.getSelectedRow());
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
			int column = table.getSelectedColumn();
			Recording r = (Recording) table.getValueAt(row, column);
			
			try {
				r.play();
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(this, "Attempting to play video failed! [" + ex.getMessage() + "]");
			}
		}
	}

	@Override public void mouseClicked(MouseEvent e) {}
	@Override public void mousePressed(MouseEvent e) {}
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
			((MainFrame) source).refresh();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == _sortTypeComboBox || e.getSource() == _sortDirectionComboBox) {
			// Get Selected Values
			int sortColumn = _sortTypeComboBox.getSelectedIndex() + 1;
			SortOrder sortOrder = _sortDirectionComboBox.getSelectedIndex() == 0 ? SortOrder.ASCENDING : SortOrder.DESCENDING;
			
			// Apply (unless sortOrder == 0)
			_sortKeys.clear();
    		_sortKeys.add(new SortKey(sortColumn, sortOrder));
    		
			// Check if TableModel is ready
			if (_sorter.getModel() != null)
	    		_sorter.setSortKeys(_sortKeys);
		} else if (e.getSource() == _playButton) {
			
			// Play on Button Press
			int row = _recordingTable.getSelectedRow();
			int column = _recordingTable.getSelectedColumn();
			Recording r = (Recording) _recordingTable.getValueAt(row, column);
			
			try {
				r.play();
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(this, "Attempting to play video failed! [" + ex.getMessage() + "]");
			}
		} else if (e.getSource() == _deleteButton) {
			
			// Delete on Button Press
			int row = _recordingTable.getSelectedRow();
			int column = _recordingTable.getSelectedColumn();
			Recording r = (Recording) _recordingTable.getValueAt(row, column);
			
			int result = JOptionPane.showConfirmDialog(null, "Allow re-record?", 
					"Delete Recording", JOptionPane.YES_NO_CANCEL_OPTION);
			if (result == JOptionPane.CANCEL_OPTION) return;

			((DefaultTableModel) _recordingTable.getModel()).removeRow(row);
			SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
				@Override
				protected Void doInBackground() throws Exception {
					boolean allow_rerecord = (result == JOptionPane.YES_OPTION);
					
					try {
						r.delete(allow_rerecord);
					} catch (IOException e) {
						JOptionPane.showMessageDialog(null, "Delete failed!", "Delete Recording", JOptionPane.WARNING_MESSAGE);
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					return null;
				}
			};
			
			worker.execute();
		} else if (e.getSource() == _refreshButton) {
			// Find MainFrame by traversing tree
			Component source = (Component) e.getSource();
			while (source.getParent() != null)
				source = source.getParent();
			
			// Refresh the MainFrame
			((MainFrame) source).refresh();
		}
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
        String text = _searchTextField.getText();

        if (text.trim().length() == 0) {
            _sorter.setRowFilter(null);
        } else {
        	try {
        		_sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        	} catch (PatternSyntaxException ex) {
        		System.err.println(ex.getMessage());
        	}
        }
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        String text = _searchTextField.getText();

        if (text.trim().length() == 0) {
            _sorter.setRowFilter(null);
        } else {
        	try {
        		_sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        	} catch (PatternSyntaxException ex) {
        		System.err.println(ex.getMessage());
        	}
        }
    }
    
    @Override public void changedUpdate(DocumentEvent e) {}
}
