package de.klierlinge.partydj.gui;

import javax.swing.JFrame;
import de.klierlinge.partydj.basics.Controller;
import de.klierlinge.partydj.lists.ListException;

public class ListWindow extends JFrame
{
	private static final long serialVersionUID = 3149870698027861160L;
	protected final PDJScrollList list;
	
	public ListWindow(final String listName) throws ListException
	{
		list = new PDJScrollList(Controller.getInstance().getListProvider().getDbList(listName));
		list.getList().setAppearance(TrackListAppearance.getGreenAppearance(22));
		add(list);
		setSize(800, 600);
		setTitle(listName);
		setVisible(true);
	}
}
