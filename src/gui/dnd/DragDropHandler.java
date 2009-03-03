package gui.DnD;

import gui.PDJList;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import javax.swing.JComponent;
import javax.swing.ListModel;
import javax.swing.TransferHandler;
import lists.EditableListModel;
import common.ListException;
import common.Track;

public class DragDropHandler extends TransferHandler
{
	private static final long serialVersionUID = 6601023550058648978L;
	
	public boolean canImport(TransferHandler.TransferSupport info)
	{	
		if (!info.isDataFlavorSupported(new DataFlavor(Track.class, "Track flavor")))
			return false;
		
		PDJList.DropLocation dropLocation = (PDJList.DropLocation)info.getDropLocation();
		
        if (dropLocation.getIndex() == -1)
        	return false;
        
        return true;
	}
	
	public synchronized boolean importData(TransferHandler.TransferSupport info)
	{
		Object[] data = null;
		
		if(!info.isDrop())
		{
			return false;
		}
					
		if (!info.isDataFlavorSupported(new DataFlavor(Track.class, "Track flavor")))
		{
			System.out.println("Unsupported Flavor");
			return false;
		}
		
		PDJList.DropLocation dropLocation = (PDJList.DropLocation)info.getDropLocation();
		
		PDJList pdjList = (PDJList)info.getComponent();
		ListModel listModel = (ListModel)pdjList.getModel();
		
		try
		{
			data = (Object[])info.getTransferable().getTransferData(new DataFlavor(Track.class, "Track flavor"));
		}
		catch (UnsupportedFlavorException e)
		{
			System.out.println("Unsupported Flavor");
			e.printStackTrace();
		}
		catch (IOException e)
		{
			System.out.println("importData: I/O exception");
			e.printStackTrace();
		}
		
		if (info.isDrop())
		{	
			if(info.getComponent() instanceof PDJList)
			{
					{
						if(DragEvent.dge.getComponent() != info.getComponent())
						{	
							switch (((PDJList)info.getComponent()).getListDropMode())
							{
							case NONE:					break;
							
							case COPY:					try
														{
															for(int i = data.length; i > 0; i--)
															{
																((EditableListModel)listModel).add(dropLocation.getIndex(), (Track)data[i-1]);
															}	
														}
														catch (ListException e)
														{
															// TODO Auto-generated catch block
															e.printStackTrace();
														}
														break;
										
							case MOVE:					System.out.println("MOVE not supported");
														break;
														
							case DELETE:				try
														{
															for(int i = data.length; i > 0; i--)
															{
																((EditableListModel)((PDJList)DragEvent.dge.getComponent()).getListModel()).remove(((PDJList)DragEvent.dge.getComponent()).getSelectedIndices()[i-1]);
															}
														}
														catch (Exception e)
														{
															// TODO Auto-generated catch block
															e.printStackTrace();
														}
														break;
														
							case COPY_OR_MOVE:			try
														{
															for(int i = data.length; i > 0; i--)
															{
																((EditableListModel)listModel).add(dropLocation.getIndex(), (Track)data[i-1]);
															}	
														}
														catch (ListException e)
														{
															// TODO Auto-generated catch block
															e.printStackTrace();
														}
														break;
							}
						}
						
						if(DragEvent.dge.getComponent() == info.getComponent())
						{
							switch (((PDJList)info.getComponent()).getListDropMode())
							{
							case NONE:					break;
							
							case COPY:					try
														{
															for(int i = data.length; i > 0; i--)
															{
																((EditableListModel)listModel).add(dropLocation.getIndex(), (Track)data[i-1]);
															}	
														}
														catch (ListException e)
														{
															// TODO Auto-generated catch block
															e.printStackTrace();
														}
														break;
										
							case MOVE:					System.out.println("MOVE not supported");
														break;
														
							case DELETE:				break;
														
							case COPY_OR_MOVE:			try
														{
															for(int i = data.length; i > 0; i--)
															{
																((EditableListModel)listModel).add(dropLocation.getIndex(), (Track)data[i-1]);
																((EditableListModel)listModel).remove(((PDJList)DragEvent.dge.getComponent()).getSelectedIndices()[i-1]);
															}	
														}
														catch (ListException e)
														{
															// TODO Auto-generated catch block
															e.printStackTrace();
														}
														break;

							}
						}
						
					}
			}
		}
		
		
		return false;
			
	}
	
	protected Transferable createTransferable(JComponent c)
	{
		PDJList pdjList = (PDJList)c;
		Object[] values = pdjList.getSelectedValues();
		return new TrackSelection(values);
	}
	
	public int getSourceActions(JComponent c)
	{
		 return COPY_OR_MOVE;
	}
	

}