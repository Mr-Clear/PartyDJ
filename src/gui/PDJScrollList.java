package gui;

import gui.DnD.ListDropMode;
import javax.swing.JScrollPane;
import javax.swing.ListModel;

public class PDJScrollList extends JScrollPane
{
	private static final long serialVersionUID = -3341295051902533709L;
	private PDJList list;
	
	public PDJScrollList(ListModel listModel)
	{
		super();
		initialise(listModel, ListDropMode.COPY_OR_MOVE, null);
	}
	
	public PDJScrollList(ListModel listModel, ListDropMode ldMode, String name)
	{
		super();
		initialise(listModel, ldMode, name);
	}
	
	private void initialise(ListModel listModel, ListDropMode ldMode, String name)
	{
		list = new PDJList(listModel, ldMode, name);
		setBorder(new javax.swing.border.EmptyBorder(0,0,0,0));
		this.setViewportView(list);
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
