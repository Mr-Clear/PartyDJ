package gui.settings;

import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import data.SettingException;
import basics.Controller;

/**
 * Setting-Panel das zuerst angezeit wird, wenn man die Einstellungen aufruft.
 * 
 * @author Eraser
 * 
 * @see SettingWindow
 */
public class About extends JPanel
{
	private static final long serialVersionUID = -4737289310199796273L;

	public About()
	{
		super();
		
		setLayout(new BorderLayout());
		Box box = Box.createVerticalBox();
		
		box.add(Box.createVerticalStrut(8));
		
		JLabel lbl = null;
		try
		{
			lbl = new JLabel(new ImageIcon("Resources/Schriftzug.png"));
		}
		catch (NullPointerException e)
		{
			lbl = new JLabel("Hier fehlt ein Bild :(");
		}
		lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
		box.add(lbl);

		box.add(Box.createVerticalGlue());
		lbl = new JLabel("PartyDJ Version " + Controller.getInstance().version);
		lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
		box.add(lbl);
		
		box.add(Box.createVerticalStrut(8));
		lbl = new JLabel("Geschrieben von Thomas Klier und Samantha Vordermeier");
		lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
		box.add(lbl);
		
		box.add(Box.createVerticalStrut(8));
		lbl = new JLabel("Tracks gesamt: " + Controller.getInstance().getListProvider().getMasterList().getSize());
		lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
		box.add(lbl);
		
		try
		{
			lbl = new JLabel("Ladedauer: " + Integer.parseInt(Controller.getInstance().getData().readSetting("LastLoadTime")) / 1000d + "Sekunden");
			lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
			box.add(lbl);
		}
		catch (SettingException e){}


		add(box);
	}
}
