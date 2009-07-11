package gui;

import gui.dnd.ListDropMode;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import lists.BasicListModel;
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
	
	public PDJScrollList()
	{
		this(new BasicListModel());
	}
	
	public PDJScrollList(TrackListModel listModel)
	{
		this(listModel, ListDropMode.COPY_OR_MOVE, null);
	}
	
	public PDJScrollList(TrackListModel listModel, ListDropMode ldMode, String name)
	{
		super();
		
		list = new PDJList(listModel, ldMode, name);
		setBorder(new javax.swing.border.EmptyBorder(0,0,0,0));
		setViewportView(list);
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
