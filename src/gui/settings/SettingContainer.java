package gui.settings;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Panel;

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
