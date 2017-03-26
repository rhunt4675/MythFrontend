package ui.recording;

import java.awt.BorderLayout;
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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
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

public class RecordingView extends ContentView {
	private static final long serialVersionUID = 7537158574729297160L;
	private static final String[] _sortTypeArray = {"Date", "Original Airdate", "Season/Episode", "Filesize"};
	private static final String[] _sortDirectionArray = {"Ascending", "Descending"};
	private static final int COUNT = 5;
	
	private final TitleBarView _titleBarView = new TitleBarView();
	private final RecordingRenderer _renderer = new RecordingRenderer();
	
	private JTable _recordingTable = new JTable();
	private TableRowSorter<TableModel> _sorter = new TableRowSorter<TableModel>();
	
	private Map<Title, TableModel> _models = new HashMap<Title, TableModel>();
	private Map<Title, Integer> _modelSelection = new HashMap<Title, Integer>();
	
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
		
		JPanel recordingDetailPanel = new JPanel();
		recordingDetailPanel.setLayout(new BorderLayout());
		recordingDetailPanel.add(new JScrollPane(_recordingTable), BorderLayout.CENTER);
		recordingDetailPanel.add(selectors, BorderLayout.NORTH);

		JSplitPane splitPane = new JSplitPane();
		splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setLeftComponent(_titleBarView);
		splitPane.setRightComponent(recordingDetailPanel);
		
		setLayout(new BorderLayout());
		add(splitPane, BorderLayout.CENTER);
		
		_playButton.addActionListener(_actionListener);
		_playButton.setFocusable(false);
		_playButton.setToolTipText("Play");
		_playButton.setIcon(new ImageIcon(getClass().getResource("/res/play.jpg")));
		_deleteButton.addActionListener(_actionListener);
		_deleteButton.setFocusable(false);
		_deleteButton.setToolTipText("Delete");
		_deleteButton.setIcon(new ImageIcon(getClass().getResource("/res/delete.jpg")));
		_refreshButton.addActionListener(_actionListener);
		_refreshButton.setFocusable(false);
		_refreshButton.setToolTipText("Refresh");
		_refreshButton.setIcon(new ImageIcon(getClass().getResource("/res/refresh.jpg")));
		_searchLabel.setText("Search: ");
		_searchTextField.setPreferredSize(new Dimension(100, 25));
		_searchTextField.getDocument().addDocumentListener(_documentListener);
		_sortLabel.setText("Sort: ");
		_sortTypeComboBox.addActionListener(_actionListener);
		_sortTypeComboBox.setModel(new DefaultComboBoxModel<String>(_sortTypeArray));
		_sortTypeComboBox.setPreferredSize(new Dimension(175, 25));
		_sortDirectionComboBox.addActionListener(_actionListener);
		_sortDirectionComboBox.setModel(new DefaultComboBoxModel<String>(_sortDirectionArray));
		_sortDirectionComboBox.setPreferredSize(new Dimension(175, 25));
		_sortTypeComboBox.setSelectedIndex(0);				// Initial Sorting == Descending Record Date
		_sortDirectionComboBox.setSelectedIndex(1); 		// Initial Sorting == Descending Record Date
		
		_titleBarView.addListSelectedListener(_titleListSelectionListener);
		_titleBarView.addKeyListener(_keyListener);
				
		_recordingTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		_recordingTable.getSelectionModel().addListSelectionListener(_recordingTableListSelectionListener);
		_recordingTable.setDefaultEditor(Object.class, null);
    	_recordingTable.setDefaultRenderer(Recording.class, _renderer);
		_recordingTable.setRowSorter(_sorter);
		_recordingTable.addMouseListener(_mouseListener);
		_recordingTable.addMouseListener(_renderer);
		_recordingTable.addMouseMotionListener(_renderer);
		_recordingTable.addKeyListener(_keyListener);
		_recordingTable.setTableHeader(null);
		_recordingTable.setRowHeight(100);
	}
	
	// Download a List of Titles
	@Override
	public void init() {
		_titleBarView.downloadAllTitlesAsync();
	}
	
	public void playSelectedRecording() {
		// Play on Button Press
		int row = _recordingTable.getSelectedRow();
		int column = _recordingTable.getSelectedColumn();
		Recording r = (Recording) _recordingTable.getValueAt(row, column);
		
		SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
			@Override protected Void doInBackground() {
				try {
					r.play();
				} catch (IOException ex) {
					JOptionPane.showMessageDialog(RecordingView.this, "Attempting to play video failed! [" + ex.getMessage() + "]");
				}
				
				return null;
			}
			
			@Override protected void done() {
				try {
					get();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		worker.execute();
	}
	
	public void deleteSelectedRecording() {
		// Delete on Button Press
		int row = _recordingTable.getSelectedRow();
		int column = _recordingTable.getSelectedColumn();
		Recording r = (Recording) _recordingTable.getValueAt(row, column);
		
		int result = JOptionPane.showConfirmDialog(null, "Allow re-record?", 
				"Delete Recording", JOptionPane.YES_NO_CANCEL_OPTION);
		if (result == JOptionPane.CANCEL_OPTION) return;

		((DefaultTableModel) _recordingTable.getModel()).removeRow(_recordingTable.convertRowIndexToModel(row));
		SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				boolean allow_rerecord = (result == JOptionPane.YES_OPTION);
				
				try {
					r.delete(allow_rerecord);
					_titleBarView.updateSelectedTitle();
					
					if (_recordingTable.getRowCount() == 0) {
						_refreshButton.doClick();
					} else {
						_recordingTable.getSelectionModel().setSelectionInterval(0, row - 1);
						_recordingTable.getSelectionModel().setSelectionInterval(0, 0);
					}
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null, "Delete failed!", "Delete Recording", JOptionPane.WARNING_MESSAGE);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				return null;
			}
		};
		worker.execute();
	}
	
	public void markSelectedRecordingWatched(boolean flag) {
		SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				int row = _recordingTable.getSelectedRow();
				int column = _recordingTable.getSelectedColumn();
				
				Recording recording = (Recording) _recordingTable.getValueAt(row, column);
				try {
					recording.mark_watched(flag);
				} catch (IOException exp) {
					exp.printStackTrace();
				}
				
				((DefaultTableModel) _recordingTable.getModel()).fireTableCellUpdated(row, 0);
				_titleBarView.updateSelectedTitle();
				return null;
			}
		};
		
		worker.execute();
	}
	
	public void refreshRecordingList() {
		// Find MainFrame by traversing tree
		Component source = (Component) this;
		while (source.getParent() != null)
			source = source.getParent();
		
		// Refresh the MainFrame
		((MainFrame) source).refresh();
	}

	private ListSelectionListener _recordingTableListSelectionListener = new ListSelectionListener() {
		@Override
		public void valueChanged(ListSelectionEvent e) {
			// Ignore Intermediate Changes
			if (e.getValueIsAdjusting()) return;
			
			// Update Selection Index
			if (_recordingTable.getSelectedRow() != -1) {
				Title selected = _titleBarView.getSelectedTitle();
				int row = _recordingTable.convertRowIndexToModel(_recordingTable.getSelectedRow());
				_modelSelection.put(selected, row);
			}
		}
	};
	
	private ListSelectionListener _titleListSelectionListener = new ListSelectionListener() {
		@Override
		public void valueChanged(ListSelectionEvent e) {
			// Ignore Intermediate Changes
			if (e.getValueIsAdjusting()) return;
			
			// Download a List of Recordings for a Selected Title
			Title selected = _titleBarView.getSelectedTitle();
			final int index = _titleBarView.getSelectedTitleIndex();
			
			// Clear the Recording List if Predicting a long wait time
			if (selected == null || !_models.containsKey(selected))
				_recordingTable.setModel(new DefaultTableModel());
			
			// Background Episode Download Task
			SwingWorker<Void, DefaultTableModel> worker = new SwingWorker<Void, DefaultTableModel>() {

				@Override
				protected Void doInBackground() {
					DefaultTableModel model;
					
					if (selected == null) {
						model = null;
					} else if (_models.containsKey(selected)) {
						model = (DefaultTableModel) _models.get(selected);
					} else {
						try {
							/* Model Definition */
							model = new DefaultTableModel() {
								private static final long serialVersionUID = -1225467351008175463L;

								@Override public Class<?> getColumnClass(int columnIndex) {
									switch (columnIndex) {
									case 0: return Recording.class;
									case 1: return ZonedDateTime.class;
									case 2: return LocalDate.class;
									case 3: return Double.class;
									case 4: return BigInteger.class;
									case 5: return String.class;
									case 6: return String.class;
									default: return null;
									}
								}
							};
							
							/* Model Columns */
							model.addColumn(null /* Recording */);
							model.addColumn(null /* Record Date */);
							model.addColumn(null /* Air Date */);
							model.addColumn(null /* Season/Episode */);
							model.addColumn(null /* File Size */);
							model.addColumn(null /* Subtitle */);
							model.addColumn(null /* Description */);
							
							/* Iterate in Chunks of 5 Until Empty */
							List<Recording> episodes; int chunk = 0;
							do {
								// Download Recordings
								episodes = selected.get_recordings(COUNT, COUNT * chunk);
								chunk++;
								
								// Add new rows to model
								for (Recording recording : episodes) {
									Object[] row = {
											(Object) recording,
											(Object) recording.get_starttime().withZoneSameInstant(ZoneId.systemDefault()),
											(Object) recording.get_airdate(),
											(Object) (recording.get_season() + 0.001 * recording.get_episode()),
											(Object) new BigInteger(recording.get_filesize()),
											(Object) recording.get_subtitle(),
											(Object) recording.get_description()
									};
									
									model.addRow(row);
								}
								
								// Publish Intermediate Results
								publish(model);
								_models.put(selected, model);

							} while (episodes.size() != 0);
							
						} catch (IOException e) {
							e.printStackTrace();
							model = null;
						}
					}
					
					publish(model);
					return null;
				}
				
				@Override
				protected void process(List<DefaultTableModel> models) {
			    	// Get the most recent TableModel
			    	DefaultTableModel model = models.get(models.size() - 1);
			    	
			    	if (model != null && _titleBarView.getSelectedTitle() == selected) {
				    	_recordingTable.setModel(model);
			    		_sorter.setModel(model);
			    		_sorter.setSortKeys(_sortKeys);
			    		
			    		// Hide extraneous "sort" columns
			    		while (_recordingTable.getColumnCount() > 1)
			    	    	_recordingTable.removeColumn(_recordingTable.getColumnModel().getColumn(1));
			    	}
			    	
		    		// Update TitleBarView
		    		_titleBarView.updateTitle(index);
				}
				
				@Override
				protected void done() {
					try {
						get();
					} catch (InterruptedException ignore) {
					} catch (ExecutionException e) {
						e.printStackTrace();
					}
					
					// Default Selection is Element 0
					_modelSelection.put(selected, 0);
					
					// Update Selection
					if (_titleBarView.getSelectedTitle() == selected) {
						_recordingTable.getSelectionModel().setSelectionInterval(0, _modelSelection.get(selected));
						_recordingTable.setColumnSelectionInterval(0, 0); 
					}
				}
			};
			worker.execute();						
		}
	};
	
	private ActionListener _actionListener = new ActionListener() {
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
				playSelectedRecording();
			} else if (e.getSource() == _deleteButton) {
				deleteSelectedRecording();
			} else if (e.getSource() == _refreshButton) {
				refreshRecordingList();
			}
		}
	};
	private KeyListener _keyListener = new KeyListener() {
		@Override
		public void keyReleased(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_F5) {
				// Find MainFrame by traversing tree
				Component source = (Component) e.getSource();
				while (source.getParent() != null)
					source = source.getParent();
				
				// Pass message to MainFrame
				((MainFrame) source).refresh();
			} else if (e.getKeyCode() == KeyEvent.VK_F) {
				// ^F is find
				if ((e.getModifiers() & ActionEvent.CTRL_MASK) != 0) {
					_searchTextField.requestFocusInWindow();
				}
			}
		}
		
		@Override public void keyTyped(KeyEvent e) {}
		@Override public void keyPressed(KeyEvent e) {}
	};
	private MouseListener _mouseListener = new MouseListener() {
		@Override public void mouseReleased(MouseEvent e) {}
		@Override public void mouseClicked(MouseEvent e) {}
		@Override public void mousePressed(MouseEvent e) {
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
				playSelectedRecording();
			}
		}
		@Override public void mouseEntered(MouseEvent e) {}
		@Override public void mouseExited(MouseEvent e) {}
	};
	private DocumentListener _documentListener = new DocumentListener() {
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
	};
}
