package de.klierlinge.partydj.gui;

import javax.swing.JScrollPane;
import javax.swing.ListModel;
import de.klierlinge.partydj.common.Track;
import de.klierlinge.partydj.gui.dnd.ListDropMode;
import de.klierlinge.partydj.lists.BasicListModel;
import de.klierlinge.partydj.lists.TrackListModel;

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
	private final PDJList list;
	
	public PDJScrollList()
	{
		this(new BasicListModel());
	}
	
	public PDJScrollList(final TrackListModel listModel)
	{
		this(listModel, ListDropMode.COPY_OR_MOVE, null);
	}
	
	public PDJScrollList(final TrackListModel listModel, final ListDropMode ldMode, final String name)
	{
		super();
		
		list = new PDJList(listModel, ldMode, name);
		setBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 0));
		setViewportView(list);
	}
	
	public PDJList getList()
	{
		return list;
	}
	
	public ListModel<Track> getListmodel()
	{
		return list.getListModel();
	}
	
	public int[] getSelectedIndices()
	{
		return list.getSelectedIndices();
	}
}
