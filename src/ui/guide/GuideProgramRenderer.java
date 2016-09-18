package ui.guide;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import data.GuideProgram;

public class GuideProgramRenderer extends DefaultTableCellRenderer  {
	private static final long serialVersionUID = 7113533907086125788L;

	public GuideProgramRenderer() {
		setOpaque(true);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		JPanel cell = new JPanel();
		GuideProgram program = (GuideProgram) value;
		
		if (program != null) {
			JLabel title = new JLabel(program.get_title());
			title.setFont(new Font("Arial", Font.BOLD, 18));
			JLabel subtitle = new JLabel(program.get_subtitle());
			
			cell.setLayout(new BorderLayout());
			cell.setBackground(getCategoryColor(program.get_category()));
			
			if (program.get_status().getStatusInt() < 0)
				cell.setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
			else if (program.get_status().getStatusInt() > 0)
				cell.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
			
			cell.add(title, BorderLayout.NORTH);
			cell.add(subtitle, BorderLayout.CENTER);
		}
		return cell;
	}
	
	private Color getCategoryColor(String category) {
		switch (category) {
		
		case "Game":
		case "Gameshow":
			return new Color(112, 16, 16);
		
		case "Crime Mystery":
		case "Crimedrama":
			return new Color(16, 80, 80);
		
		case "Action":
		case "Adventure":
			return new Color(144, 96, 32);
			
		case "Comedy":
		case "Sitcom":
			return new Color(0, 96, 128);

		case "Drama": return new Color(64, 0, 96);
		case "Movie": return new Color(64, 48, 96);
		case "Sports": return new Color(0, 80, 48);
		case "Reality": return new Color(48, 64, 64);
		
		case "":
		default: 
			return Color.LIGHT_GRAY; /*System.out.println("Unknown category " + category);*/
		}

		/*case "Adult": return new Color(112, 32, 32); 
		case "Animals": return new Color(96, 144, 96); 
		case "Art Music": return new Color(128, 16, 96); 
		case "Business": return new Color(112, 48, 16); 
		case "Children": return new Color(176, 0, 16); 
		case "Documentary": return new Color(80, 64, 32);
		case "Educational": return new Color(96, 96, 64);
		case "Food": return new Color(32, 128, 64);
		case "Health Medical": return new Color(128, 96, 96);
		case "History": return new Color(128, 112, 32);
		case "HowTo": return new Color(160, 160, 0);
		case "Horror": return new Color(16, 16, 64);
		case "News": return new Color(96, 96, 64);
		case "Reality": return new Color(48, 64, 64);
		case "Romance": return new Color(160, 32, 80);
		case "Science Nature": return new Color(0, 128, 80);
		case "SciFi Fantasy": return new Color(96, 96, 144);
		case "Shopping": return new Color(16, 48, 16);
		case "Soaps": return new Color(80, 128, 128);
		case "Spiritual": return new Color(128, 64, 128);
		case "Talk": return new Color(32, 48, 64);
		case "Travel": return new Color(32, 96, 176);
		case "War": return new Color(176, 96, 80);
		case "Western": return new Color(128, 96, 64);*/
		
		/*.cat_Action         { background-color: #906020; }
		.cat_Adult          { background-color: #702020; }
		.cat_Animals        { background-color: #609060; }
		.cat_Art_Music      { background-color: #801060; }
		.cat_Business       { background-color: #703010; }
		.cat_Children       { background-color: #B00010; }
		.cat_Comedy         { background-color: #006080; }
		.cat_Crime_Mystery  { background-color: #105050; }
		.cat_Documentary    { background-color: #504020; }
		.cat_Drama          { background-color: #400060; }
		.cat_Educational    { background-color: #606060; }
		.cat_Food           { background-color: #208040; }
		.cat_Game           { background-color: #701010; }
		.cat_Health_Medical { background-color: #806060; }
		.cat_History        { background-color: #807020; }
		.cat_HowTo          { background-color: #A0A000; }
		.cat_Horror         { background-color: #101040; }
		.cat_Misc           { background-color: #403060; }
		.cat_News           { background-color: #606040; }
		.cat_Reality        { background-color: #304040; }
		.cat_Romance        { background-color: #A02050; }
		.cat_Science_Nature { background-color: #008050; }
		.cat_SciFi_Fantasy  { background-color: #606090; }
		.cat_Shopping       { background-color: #103010; }
		.cat_Soaps          { background-color: #508080; }
		.cat_Spiritual      { background-color: #804080; }
		.cat_Sports         { background-color: #005030; }
		.cat_Talk           { background-color: #203040; }
		.cat_Travel         { background-color: #2060B0; }
		.cat_War            { background-color: #B06050; }
		.cat_Western        { background-color: #806040; }
		.cat_Unknown        { background-color: #303030; }
		*/
	}
}