package gui.dnd;

import java.awt.event.MouseEvent;

public class DragEvent
{
	public static MouseEvent dge;
	
	public DragEvent(MouseEvent dge)
	{
		DragEvent.dge = dge;
	}
}


