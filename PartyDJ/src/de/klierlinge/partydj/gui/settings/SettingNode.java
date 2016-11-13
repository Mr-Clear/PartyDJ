package de.klierlinge.partydj.gui.settings;

import java.awt.Component;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Knoten im Baum der Einstellungen im SettingWindow.
 * <p>Erweitert DefaultMutableTreeNode um eine Klasse die angezeit wird,
 * wenn auf den Knoten geklickt wird.
 * 
 * @author Eraser
 * 
 * @see DefaultMutableTreeNode
 * @see SettingWindow
 */
public class SettingNode extends DefaultMutableTreeNode
{
	private static final long serialVersionUID = -4450841493024896674L;
	private final Class<? extends Component> component;
	
	/**Erstellt eine neue SettingNode.
	 * 
	 * @param text Text den die Node hat.
	 * @param component Component die bei Klick auf die Node angezeigt wird.
	 */
	public SettingNode(final String text, final Class<? extends Component> component)
	{
		super(text);
		this.component = component;
	}

	Class<? extends Component> getComponent()
	{
		return component;
	}
}
