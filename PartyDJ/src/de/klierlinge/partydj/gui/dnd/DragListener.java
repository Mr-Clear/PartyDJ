package de.klierlinge.partydj.gui.dnd;

import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceAdapter;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import de.klierlinge.partydj.basics.Controller;
import de.klierlinge.partydj.common.Track;
import de.klierlinge.partydj.gui.PDJList;

/**DragListener abgeleitet von DragSourceAdapter und implementiert DragGestureListener
 * 
 * @author Sam
 * @date   15.05.09
 */
public class DragListener extends DragSourceAdapter implements DragGestureListener
{
	private static PDJList list = null;
	
	public DragListener(){}
	
	public DragListener(final JLabel label)
	{
		final DragSource dragSource = DragSource.getDefaultDragSource();
		dragSource.createDefaultDragGestureRecognizer(label, DnDConstants.ACTION_COPY, this);
	}

	@Override
	public void dragGestureRecognized(final DragGestureEvent dge)
	{
		if(dge.getTriggerEvent() instanceof MouseEvent)
		{
			if(SwingUtilities.isLeftMouseButton(((MouseEvent)dge.getTriggerEvent())))
				if(dge.getComponent() instanceof PDJList)
					list = (PDJList) dge.getComponent();
				else if(dge.getComponent() instanceof JLabel)
				{
					final Transferable transfer = new TrackSelection(new Track[]{Controller.getInstance().getPlayer().getCurrentTrack()});
				    dge.startDrag(null, transfer, this);
				}
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
	public void dragDropEnd(final DragSourceDropEvent dsde)
	{
		list = null;
	}
}
