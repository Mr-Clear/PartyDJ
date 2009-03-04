package gui;

import gui.DnD.DragDropHandler;
import gui.DnD.DragEvent;
import gui.DnD.ListDropMode;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DropMode;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import basics.Controller;
import lists.EditableListModel;
import common.ListException;
import common.Track;
//Servus
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
			listSize = ((PDJList)dge.getComponent()).getModel().getSize();
			index = dge.getY() / ((PDJList)dge.getComponent()).getFixedCellHeight();
			
			if(count == 0)
			{
				if(index > listSize)
					startIndex = listSize - 1;
				
				else
					startIndex = index;
			}
				
			
			if(SwingUtilities.isLeftMouseButton(dge))
			{
				new DragEvent(dge);
			}
			
			if(SwingUtilities.isMiddleMouseButton(dge))
			{
				count++;

				if(((PDJList)dge.getComponent()).getSelectedIndex() > -1)
					((PDJList)dge.getComponent()).ensureIndexIsVisible(((PDJList)dge.getComponent()).getSelectedIndices()[(((PDJList)dge.getComponent()).getSelectedIndices().length -1)]);
				
				if(dge.getComponent() instanceof PDJList)
				{
					if(((PDJList)dge.getComponent()).getLastVisibleIndex() <= index)
					{
						((PDJList)dge.getComponent()).ensureIndexIsVisible(index + 1);
						((PDJList)dge.getComponent()).setSelectionInterval(index + 1, startIndex);
					}
					
					if(((PDJList)dge.getComponent()).getFirstVisibleIndex() <= startIndex)
					{
						((PDJList)dge.getComponent()).ensureIndexIsVisible(index - 1);
						((PDJList)dge.getComponent()).setSelectionInterval(index, startIndex - 1);
					}
				
					if(index > listSize)
						((PDJList)dge.getComponent()).setSelectionInterval(listSize - 1, startIndex);
					
					else if(index < 0)
						((PDJList)dge.getComponent()).setSelectionInterval(0, startIndex);
					
					else
						((PDJList)dge.getComponent()).setSelectionInterval(index, startIndex);
				}
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
						list.setSelectedIndex(e.getY() / list.getFixedCellHeight());
					}
					catch (IndexOutOfBoundsException ex)
					{
						return;
					}
					
					if(list.getSelectedValue() != null)
					{
						JPopupMenu popup = new JPopupMenu();
						JMenuItem newItem = new JMenuItem(list.getSelectedValue().toString());
						newItem.setEnabled(false);
						popup.add(newItem);
						popup.addSeparator();
						newItem = new JMenuItem("Bearbeiten...");
						newItem.addActionListener(new EditListener((Track)list.getSelectedValue()));
						popup.add(newItem);
		
						popup.show(list, e.getX(), e.getY());
					}
				}
			}
			
			
		}
		
		public void mouseReleased(MouseEvent e)
		{
			count = 0;
		}
		
		
		
		private class EditListener implements ActionListener
		{
			private Track item;
			EditListener(Track item)
			{
				this.item = item;
			}
			public void actionPerformed(ActionEvent e)
			{
					new EditTrackWindow(item);			
			}				
		}
		
	}	
}

