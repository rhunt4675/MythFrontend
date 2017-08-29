package ui.recording;

import data.Title;
import utils.ArtworkRoll;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TitleBarView extends JPanel {
	private static final Logger LOGGER = Logger.getLogger(TitleBarView.class.getName());
	private JList<Title> _titleList = new JList<Title>();
	private ArtworkRoll _artworkRoll = new ArtworkRoll();
	
	private static final Dimension _titleArtworkDimension = new Dimension(250, 0);

	public TitleBarView() {
		_titleList.setSelectionBackground(Color.DARK_GRAY);
		_titleList.addListSelectionListener(_onListSelectionChanged);
		_titleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				
		setLayout(new BorderLayout());
		setPreferredSize(_titleArtworkDimension);
		add(new JScrollPane(_titleList), BorderLayout.CENTER);
		add(_artworkRoll, BorderLayout.SOUTH);
	}
	
	public Title getSelectedTitle() {
		return _titleList.getSelectedValue();
	}
	
	public int getSelectedTitleIndex() {
		return _titleList.getSelectedIndex();
	}
	
	public void updateSelectedTitle() {
		int selectionIndex = _titleList.getSelectedIndex();
		updateTitle(selectionIndex);
	}
	
	public void updateTitle(int index) {
		if (index >= 0 && index < _titleList.getModel().getSize())
			_titleList.repaint(_titleList.getCellBounds(index, index));
	}
	
	public void addListSelectedListener(ListSelectionListener lsl) {
		_titleList.addListSelectionListener(lsl);
	}

	public void downloadAllTitlesAsync() {
		// Selection index before refresh
		int selected = Math.max(_titleList.getSelectedIndex(), 0);
		
		SwingWorker<List<Title>, Void> worker = new SwingWorker<List<Title>, Void>() {
			@Override
			protected List<Title> doInBackground() {
				List<Title> titles;
				try {
					titles = Title.get_titles();
				} catch (IOException e) {
					LOGGER.log(Level.SEVERE, e.toString(), e);
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
					LOGGER.log(Level.SEVERE, e.toString(), e);
			    }
			    
			    if (titles != null && !titles.isEmpty()) {
			    	_titleList.setListData(titles.toArray(new Title[0]));
			    	
			    	// Trigger Download of All Recordings
			    	for (int i = 0; i < titles.size(); i++) _titleList.setSelectedIndex(i);			    	
			    	_titleList.setSelectedIndex(Math.min(selected, _titleList.getModel().getSize() - 1));
			    }
			}
		};
		
		worker.execute();		
	}
	
	private ListSelectionListener _onListSelectionChanged = new ListSelectionListener() {
		
		@Override public void valueChanged(ListSelectionEvent e) {
			// Ignore Intermediate Changes
			if (e.getValueIsAdjusting()) return;
			Title selected = _titleList.getSelectedValue();
			if (selected == null) return;
			
			// Background Artwork Download Task
			SwingWorker<List<ImageIcon>, Void> imagedownloader = new SwingWorker<List<ImageIcon>, Void>() {
				@Override
				protected List<ImageIcon> doInBackground() throws Exception {
					if (_titleList.getSelectedValue() == selected)
						return selected.get_title_artwork(_titleArtworkDimension);
					else
						return null;
				}
				
				protected void done() {
					try {
						List<ImageIcon> artwork = get();
						if (_titleList.getSelectedValue() == selected) {
							_artworkRoll.setArtwork(artwork);
						}
					} catch (Exception e) {
						LOGGER.log(Level.SEVERE, e.toString(), e);
					}
				}
			};
			
			imagedownloader.execute();
		}
	};
}
