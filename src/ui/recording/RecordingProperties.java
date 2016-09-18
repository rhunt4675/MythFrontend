package ui.recording;

import java.awt.Window;

import javax.swing.GroupLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

import data.Recording;

public class RecordingProperties extends JDialog {
	private static final long serialVersionUID = -119252464633818211L;
	
	public RecordingProperties(Window owner, Recording r) {
		super(owner);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle("Properties");
		
		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		
		JLabel title = new JLabel("Title: ");
		JTextField titletext = new JTextField(r.get_title());
		JLabel subtitle = new JLabel("Subtitle: ");
		JTextField subtitletext = new JTextField(r.get_subtitle());
		JLabel description = new JLabel("Description: ");
		JTextField descriptiontext = new JTextField(r.get_description());
		JLabel category = new JLabel("Category: ");
		JTextField categorytext = new JTextField(r.get_category());
		JLabel filesize = new JLabel("Filesize: ");
		JTextField filesizetext = new JTextField(r.get_filesize());
		JLabel recordedid = new JLabel("RecordedID: ");
		JTextField recordedidtext = new JTextField(Integer.toString(r.get_recordedid()));
		JLabel season = new JLabel("Season: ");
		JTextField seasontext = new JTextField(Integer.toString(r.get_season()));
		JLabel episode = new JLabel("Episode: ");
		JTextField episodetext = new JTextField(Integer.toString(r.get_episode()));

		layout.setHorizontalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(title)
					.addComponent(subtitle)
					.addComponent(description)
					.addComponent(category)
					.addComponent(filesize)
					.addComponent(recordedid)
					.addComponent(season)
					.addComponent(episode))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(titletext)
					.addComponent(subtitletext)
					.addComponent(descriptiontext)
					.addComponent(categorytext)
					.addComponent(filesizetext)
					.addComponent(recordedidtext)
					.addComponent(seasontext)
					.addComponent(episodetext))
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(title).addComponent(titletext))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(subtitle).addComponent(subtitletext))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(description).addComponent(descriptiontext))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(category).addComponent(categorytext))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(filesize).addComponent(filesizetext))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(recordedid).addComponent(recordedidtext))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(season).addComponent(seasontext))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(episode).addComponent(episodetext))
		);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		pack();
	}
}
