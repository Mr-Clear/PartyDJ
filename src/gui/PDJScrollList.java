package gui;

import java.lang.reflect.InvocationTargetException;
import gui.dnd.ListDropMode;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import lists.TrackListModel;

/**
 * Bettet die PLJList in ein JScollPane ein.
 * 
 * @author Eraser
 * 
 * @see PDJList
 */
public class PDJScrollList extends JScrollPane
{
	private static final long serialVersionUID = -3341295051902533709L;
	private PDJList list;
	private JScrollPane me = this;
	
	public PDJScrollList(TrackListModel listModel)
	{
		super();
		initialise(listModel, ListDropMode.COPY_OR_MOVE, null);
	}
	
	public PDJScrollList(TrackListModel listModel, ListDropMode ldMode, String name)
	{
		super();
		initialise(listModel, ldMode, name);
	}
	
	private void initialise(final TrackListModel listModel, final ListDropMode ldMode, final String name)
	{
		if(SwingUtilities.isEventDispatchThread())
		{
			list = new PDJList(listModel, ldMode, name);
			setBorder(new javax.swing.border.EmptyBorder(0,0,0,0));
			me.setViewportView(list);
		}
		else
		{
			try
			{
				SwingUtilities.invokeAndWait(new Runnable(){
					@Override
					public void run()
					{
						list = new PDJList(listModel, ldMode, name);
						setBorder(new javax.swing.border.EmptyBorder(0,0,0,0));
						me.setViewportView(list);
					}});
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (InvocationTargetException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public PDJList getList()
	{
		return list;
	}
	
	public ListModel getListmodel()
	{
		return list.getListModel();
	}
	
	public int[] getSelectedIndices()
	{
		return list.getSelectedIndices();
	}
}
