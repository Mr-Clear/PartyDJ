package gui.settings;

import java.awt.BorderLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class About extends JPanel
{
	private static final long serialVersionUID = -4737289310199796273L;

	public About()
	{
		super();
		JLabel picture = null;
		try
		{
			picture = new JLabel(new ImageIcon("Resources/Schriftzug.png"));
		}
		catch (NullPointerException e)
		{
			picture = new JLabel("Hier fehlt ein Bild :(");
		}
		
		picture.setHorizontalAlignment(JLabel.CENTER);
		add(picture, BorderLayout.CENTER);
	}
}
