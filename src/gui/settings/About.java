package gui.settings;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import basics.Controller;

public class About extends JPanel
{
	private static final long serialVersionUID = -4737289310199796273L;

	public About()
	{
		super();
		
		setLayout(new BorderLayout());
		Box box = Box.createVerticalBox();
		
		box.add(Box.createRigidArea(new Dimension(8, 8)));
		
		JLabel lbl = null;
		try
		{
			lbl = new JLabel(new ImageIcon("Resources/Schriftzug.png"));
		}
		catch (NullPointerException e)
		{
			lbl = new JLabel("Hier fehlt ein Bild :(");
		}
		lbl.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		box.add(lbl);

		box.add(Box.createVerticalGlue());
		lbl = new JLabel("PartyDJ Version " + Controller.getInstance().version);
		lbl.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		box.add(lbl);
		
		box.add(Box.createRigidArea(new Dimension(8, 8)));
		lbl = new JLabel("Geschrieben von Thomas Klier und Samantha Vordermeier");
		lbl.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		box.add(lbl);
		add(box);
	}
}
