package gui;

import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.event.MouseEvent;
import javax.swing.SwingUtilities;

public class DragGestureList implements DragGestureListener
{
	private static PDJList list = null;

	@Override
	public void dragGestureRecognized(DragGestureEvent dge)
	{
		if(dge.getTriggerEvent() instanceof MouseEvent)
		{
			if(SwingUtilities.isLeftMouseButton(((MouseEvent)dge.getTriggerEvent())))
				if(dge.getComponent() instanceof PDJList)
					list = (PDJList) dge.getComponent();
		}
	}
	
	public static PDJList getList()
	{
		return list;
	}
	
	public static void setList(PDJList list)
	{
		DragGestureList.list = list;
	}
}
