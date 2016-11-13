package de.klierlinge.partydj.gui.settings;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import de.klierlinge.partydj.basics.Controller;
import de.klierlinge.partydj.data.SettingException;

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
	protected final transient Controller controller = Controller.getInstance();

	public About()
	{
		super();
		
		setLayout(new BorderLayout());
		final Box box = Box.createVerticalBox();
		
		box.add(Box.createVerticalStrut(8));
		
		JLabel lbl = null;
		try
		{
			lbl = new JLabel(new ImageIcon("Resources/Schriftzug.png"));
		}
		catch (final NullPointerException e)
		{
			lbl = new JLabel("Hier fehlt ein Bild :(");
		}
		lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
		box.add(lbl);

		//TODO: Revision Info
		
		box.add(Box.createVerticalStrut(8));
		addText("Entwickelt von Thomas Klier und Samantha Vordermeier", box);
		addText("http://usseraser.no-ip.com/PartyDJ", box);

		
		box.add(Box.createVerticalStrut(8));
		addText("Tracks gesamt: " + controller.getListProvider().getMasterList().getSize(), box);

		
		try
		{
			addText("Ladedauer: " + Integer.parseInt(controller.getData().readSetting("LastLoadTime")) / 1000d + "Sekunden", box);
		}
		catch (final SettingException e) { /* ignore */ }

		box.add(Box.createVerticalStrut(8));
		addText("Speicherpfad: " + de.klierlinge.partydj.common.Functions.getFolder(), box);
		addText("Datenbank: " + controller.getData().getDbPath(), box);

		add(box);
	}
	
	JTextField addText(final String text, final Box box)
	{
		final JTextField txt = new JTextField(text);
		txt.setEditable(false);
		//txt.setPreferredSize(new Dimension(0, 0));
		txt.setMaximumSize(new Dimension(Integer.MAX_VALUE, 0));
		txt.setHorizontalAlignment(SwingConstants.CENTER);
		txt.setAlignmentX(Component.CENTER_ALIGNMENT);
		txt.setBackground(getBackground());
		txt.setBorder(null);
		box.add(txt);
		return txt;
	}
}
