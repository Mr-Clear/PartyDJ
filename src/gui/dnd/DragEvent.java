package gui.dnd;

import java.awt.event.MouseEvent;

/**Wird nur noch f�r die alte SwingVersion des DragDropHandlers ben�tigt.
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


