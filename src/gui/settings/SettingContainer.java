package gui.settings;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Panel;

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

public class SettingContainer extends Panel
{
	private static final long serialVersionUID = -7000748047182190615L;
	public void setSettingComponent(Component component)
	{
		for(Component toRemove : getComponents())
		{
			if(toRemove instanceof Closeable)
				((Closeable)toRemove).close();
			remove(toRemove);
		}
		
		add(component, BorderLayout.CENTER);
		validate();
	}

}
