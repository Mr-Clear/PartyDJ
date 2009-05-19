package gui.dnd;

import gui.PDJList;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSourceAdapter;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.event.MouseEvent;
import javax.swing.SwingUtilities;

/**DragListener abgeleitet von DragSourceAdapter und implementiert DragGestureListener
 * 
 * @author Sam
 * @date   15.05.09
 */
public class DragListener extends DragSourceAdapter implements DragGestureListener
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
	
	/**
	 * @return	Gibt die PDJList zurück, aus der gerade gedragt wurde.
	 */
	public static PDJList getList()
	{
		return list;
	}

	@Override
	public void dragDropEnd(DragSourceDropEvent dsde)
	{
		list = null;
	}
}
