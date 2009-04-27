package gui.dnd;

import java.awt.event.MouseEvent;

/**
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


