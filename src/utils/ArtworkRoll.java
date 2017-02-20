package utils;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class ArtworkRoll extends JPanel {
	private static final long serialVersionUID = 6375826821868427307L;
	private JLabel _iconLabel = new JLabel();
	private JPanel _navPanel = new JPanel();
	private JLabel _indexLabel = new JLabel();
	private JButton _backButton = new JButton("\u2190");
	private JButton _nextButton = new JButton("\u2192");
	
	private List<ImageIcon> _artwork;
	private int _artworkIterator = 0;
	
	public ArtworkRoll() {
		_backButton.addActionListener(_onBackPressed);
		_nextButton.addActionListener(_onNextPressed);
		_indexLabel.setHorizontalAlignment(SwingConstants.CENTER);
		_iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		_navPanel.setLayout(new BorderLayout());
		_navPanel.add(_backButton, BorderLayout.WEST);
		_navPanel.add(_indexLabel, BorderLayout.CENTER);
		_navPanel.add(_nextButton, BorderLayout.EAST);
		
		setLayout(new BorderLayout());
		add(_iconLabel, BorderLayout.NORTH);
		add(_navPanel, BorderLayout.SOUTH);
	}

	public void setArtwork(List<ImageIcon> artwork) {
		_artwork = artwork;
		
		// Start the Roll
		_artworkIterator = -1;
		_nextButton.doClick();
	}
	
	private ActionListener _onBackPressed = new ActionListener() {
		@Override public void actionPerformed(ActionEvent e) {
			if (_artwork != null) {
				_artworkIterator = (_artworkIterator - 1) % _artwork.size();
				ImageIcon newImage = _artwork.get(_artworkIterator);
				_iconLabel.setIcon(newImage);
				_indexLabel.setText("Image: " + (_artworkIterator + 1) + " of " + _artwork.size());
			}
		}
	};
	
	private ActionListener _onNextPressed = new ActionListener() {
		@Override public void actionPerformed(ActionEvent e) {
			if (_artwork != null) {
				_artworkIterator = (_artworkIterator + 1) % _artwork.size();
				ImageIcon newImage = _artwork.get(_artworkIterator);
				_iconLabel.setIcon(newImage);
				_indexLabel.setText("Image: " + (_artworkIterator + 1) + " of " + _artwork.size());
			}
		}
	};
}
