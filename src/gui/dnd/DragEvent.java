package gui.dnd;

import java.awt.event.MouseEvent;

/**Wird nur noch für die alte SwingVersion des DragDropHandlers benötigt.
 * 
 * @author Sam
 */
public class DragEvent
{
	public static MouseEvent dge;
	
	public DragEvent(MouseEvent dge)
	{
		DragEvent.dge = dge;
	}
}


