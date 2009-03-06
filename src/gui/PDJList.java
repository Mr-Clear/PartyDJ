package gui;

import gui.DnD.DragDropHandler;
import gui.DnD.DragEvent;
import gui.DnD.ListDropMode;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DropMode;
import javax.swing.JList;
import javax.swing.KeyStroke;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import basics.Controller;
import lists.EditableListModel;
import common.ListException;
import common.Track;

public class PDJList extends JList
{
	private static final long serialVersionUID = -8653111853374564564L;
	private ListDropMode ldMode;
	private ListModel listModel;
	private int count = 0;
	
	public PDJList(ListModel listModel)
	{
		super(listModel);
		initialise(listModel, ListDropMode.COPY_OR_MOVE, null);
	}
	
	public PDJList(ListModel listModel, ListDropMode ldMode, String name)
	{
		super(listModel);
		initialise(listModel, ldMode, name);
	}
	
	private void initialise(ListModel listModel, ListDropMode ldMode, String name)
	{
		final DragDropHandler handler = new DragDropHandler();
		
		this.setName(name);
		this.listModel = listModel;
		this.setListDropMode(ldMode);
		this.setTransferHandler(handler);
		this.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		this.setDragEnabled(true);
		this.addMouseMotionListener(new MyMouseMotionListener());
		this.addMouseListener(new MyMouseListener());
	
		this.getActionMap().put(TransferHandler.getCutAction().getValue(Action.NAME), TransferHandler.getCutAction());
		this.getActionMap().put(TransferHandler.getCopyAction().getValue(Action.NAME), TransferHandler.getCopyAction());
		this.getActionMap().put(TransferHandler.getPasteAction().getValue(Action.NAME), TransferHandler.getPasteAction());
		
		this.getInputMap().put(KeyStroke.getKeyStroke("ctrl X"),TransferHandler.getCutAction().getValue(Action.NAME));
		this.getInputMap().put(KeyStroke.getKeyStroke("ctrl C"),TransferHandler.getCopyAction().getValue(Action.NAME));
		this.getInputMap().put(KeyStroke.getKeyStroke("ctrl V"),TransferHandler.getPasteAction().getValue(Action.NAME));

		this.getInputMap().put(KeyStroke.getKeyStroke("DELETE"), "Delete");
		this.getActionMap().put("Delete", new AbstractAction("Delete") 
											{
												private static final long serialVersionUID = -2342506838333821595L;

												public void actionPerformed(ActionEvent evt) 
            									{
            										PDJList pdjList = (PDJList)evt.getSource();
            								        EditableListModel listModel = (EditableListModel)pdjList.getModel();
            								        int[] indices = pdjList.getSelectedIndices();
            								 
            								        	for(int i = indices.length; i > 0; i--)
            											{
            												try
            												{
            													listModel.remove(indices[i-1]);
            												}
            												catch (ListException e)
            												{
            													// TODO Auto-generated catch block
            													e.printStackTrace();
            												}
            											}
            									}
											});
		
		this.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "Return");
		this.getActionMap().put("Return", new AbstractAction("Return") 
								{
									private static final long serialVersionUID = -2342506838333821595L;
									public void actionPerformed(ActionEvent evt) 
									{
										Controller.instance.player.start(((Track)((PDJList)evt.getSource()).getSelectedValues()[0]));
									}
								});
		
		if(ldMode != ListDropMode.NONE && ldMode != ListDropMode.DELETE)
			this.setDropMode(DropMode.INSERT);		
		else
			this.setDropMode(DropMode.ON);
		
		
		this.setCellRenderer(new TrackRenderer());
	}

	public void setListDropMode(ListDropMode ldMode)
	{
		this.ldMode = ldMode;
	}

	public ListDropMode getListDropMode()
	{
		return ldMode;
	}

	public ListModel getListModel()
	{
		return listModel;
	}
	
	private class MyMouseMotionListener extends MouseMotionAdapter
	{
		private int startIndex;
		private int listSize;
		private int index;
		
		
		public void mouseDragged(MouseEvent dge)
		{	
			/*listSize = ((PDJList)dge.getComponent()).getModel().getSize();
			index = dge.getY() / ((PDJList)dge.getComponent()).getFixedCellHeight();
			
			if(count == 0)
			{
				if(index > listSize)
					startIndex = listSize - 1;
				
				else
					startIndex = index;
			}*/
				
			
			if(SwingUtilities.isLeftMouseButton(dge))
			{
				new DragEvent(dge);
			}
			
			if(SwingUtilities.isMiddleMouseButton(dge))
			{
				if(dge.getComponent() instanceof PDJList)
				{
					PDJList pdjList = ((PDJList)dge.getComponent());
					listSize = pdjList.getModel().getSize();
					index = dge.getY() / pdjList.getFixedCellHeight();
					
					if(index >= 0)
					{
						if(count == 0)
						{
							if(index > listSize)
								startIndex = listSize - 1;
							
							else
								startIndex = index;
						}
						
						pdjList.ensureIndexIsVisible(index);
						
						if(index == startIndex)
							pdjList.setSelectedIndex(index);
						
						if(startIndex < index)
						{
							pdjList.setSelectionInterval(index, startIndex);
							pdjList.ensureIndexIsVisible(index + 2);
						}
						
						if(index < startIndex)
						{
							pdjList.setSelectionInterval(startIndex, index);
							pdjList.ensureIndexIsVisible(index - 2);
						}
					}
					
					if(index < 0)
					{
						if(count == 0)
						{
							startIndex = 0;
						}
						
						if(startIndex > index)
						{
							pdjList.setSelectionInterval(startIndex, 0);
							pdjList.ensureIndexIsVisible(0);
						}
					}
				}	
				count++;
			}
		}
	}
	
	class MyMouseListener extends MouseAdapter
	{
		
		public void mouseClicked(MouseEvent e)
		{
			PDJList list = (PDJList)e.getSource();
			
			if(SwingUtilities.isRightMouseButton(e))
			{
				synchronized(list)
				{
					try
					{
						if(e.getY() / list.getFixedCellHeight() <= list.getLastVisibleIndex())
						{
							if(list.getSelectedIndex() == -1)
								list.setSelectedIndex(e.getY() / list.getFixedCellHeight());
							
							for(int i = list.getSelectedIndices().length; i > 0; i--)
							{
								if((int)(e.getY() / list.getFixedCellHeight()) == list.getSelectedIndices()[i-1])
								{
									if(list.getSelectedValue() != null)
									{
										PopupMenuGenerator.listPopupMenu(list, (Track)list.getSelectedValue()).show(list, e.getX(), e.getY());
										return;
									}
								}
							}
							list.setSelectedIndex(e.getY() / list.getFixedCellHeight());
							PopupMenuGenerator.listPopupMenu(list, (Track)list.getSelectedValue()).show(list, e.getX(), e.getY());	
						}
					}
					catch (IndexOutOfBoundsException ex)
					{
						return;
					}
					
					
				}
			}
		}
		
		public void mouseReleased(MouseEvent e)
		{
			count = 0;
		}		
	}	
}

