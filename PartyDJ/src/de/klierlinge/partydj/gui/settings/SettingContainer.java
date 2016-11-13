package de.klierlinge.partydj.gui.settings;

import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.JPanel;

/**
 * Nimmt Objekte vom Typ Component auf und zeigt sie im SettingWindow.
 * <br>Wenn die Komponente nach Anzeigen geschlossen werden muss, muss die Closeable impelentieren.
 * 
 * @author Eraser
 * 
 * @see SettingWindow
 * @see Component
 * @see Closeable
 */

public class SettingContainer extends JPanel
{
	private static final long serialVersionUID = -7000748047182190615L;
	public void setSettingComponent(final Component component)
	{
		for(final Component toRemove : getComponents())
		{
			if(toRemove instanceof Closeable)
				((Closeable)toRemove).close();
			remove(toRemove);
		}
		if(component != null)
			add(component, BorderLayout.CENTER);
		validate();
	}

}
